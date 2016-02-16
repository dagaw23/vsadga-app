package pl.com.vsadga.batch;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.io.BarDataFileReader;
import pl.com.vsadga.io.ReaderException;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.service.writer.CurrencyDbWriterService;
import pl.com.vsadga.utils.DateConverter;

@Component
public class DataRewriterBatchBean {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataRewriterBatchBean.class);

	@Autowired
	private BarDataFileReader barDataFileReader;

	@Autowired
	private ConfigDataService configDataService;

	@Autowired
	private CurrencyDbWriterService currencyDbWriterService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Scheduled(cron = "7 * * * * *")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;
		String file_path = null;

		// data formatter:
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		// pobierz listę aktywnych symboli:
		symbol_list = symbolService.getActiveSymbols();
		// czy są zdefiniowane aktywne symbole:
		if (symbol_list.isEmpty()) {
			LOGGER.info("   ### Brak aktywnych symboli [" + symbol_list.size() + "].");
			return;
		}

		// pobierz listę aktywnych timeframe:
		tmefrm_list = timeFrameService.getAllActive();
		// czy są zdefiniowane timeFrames:
		if (tmefrm_list.isEmpty()) {
			LOGGER.info("   ### Brak aktywnych timeFrame [" + tmefrm_list.size() + "].");
			return;
		}

		try {
			// pobierz ścieżkę dostępu do plików z danymi:
			file_path = configDataService.getParam("MT4_PATH");

			if (file_path == null || file_path.trim().isEmpty()) {
				LOGGER.info("   ### Brak parametru o nazwie MT4_PATH [" + file_path + "].");
				return;
			}

			for (CurrencySymbol sym : symbol_list) {
				for (TimeFrame frm : tmefrm_list) {
					LOGGER.info("   [WRITE] " + sym.getSymbolName() + "-" + frm.getTimeFrameDesc() + ".");
					rewriteFileContent2db(sym, frm, file_path);
				}
			}

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		}

		LOGGER.info("Invoked on " + dateFormat.format(System.currentTimeMillis()) + ".");
	}

	private long getRecordTime(String record) throws ParseException {
		String[] rec_tab = record.split(";");

		return DateConverter.stringToDate(rec_tab[0], "yyyy.MM.dd HH:mm:ss").getTime();
	}

	private void rewriteFileContent2db(CurrencySymbol symbol, TimeFrame timeFrame, String filePath)
			throws BaseServiceException {
		List<String> rec_list = null;

		try {
			// pobierz całą zawartość pliku:
			rec_list = barDataFileReader.readAll(filePath, symbol.getSymbolName(), timeFrame.getTimeFrameDesc());

			// jeśli pusta lista rekordów: przejdź do następnego
			if (rec_list.isEmpty()) {
				LOGGER.info("   [EMPTY] Lista rekordow SYM,FRM [" + symbol.getSymbolName() + ","
						+ timeFrame.getTimeFrameDesc() + "].");
				return;
			}

			// wpisz rekordy lub aktualizuj w DB:
			currencyDbWriterService.write(symbol, timeFrame, rec_list);

		} catch (ReaderException e) {
			e.printStackTrace();
			LOGGER.error("::rewriteFileContent2db:: wyjatek ReaderException!");
		}
	}

	private int writeFilePart(List<String> recordList, Timestamp lastWriteTime, CurrencySymbol symbol,
			TimeFrame timeFrame) throws ParseException, BaseServiceException {
		List<String> rec_2_write = new ArrayList<String>();
		int write_count = 0;

		for (String rec : recordList) {
			if (lastWriteTime.getTime() < getRecordTime(rec)) {
				LOGGER.info("   ### Zapis wg daty z rekordu [" + rec + "].");

				rec_2_write.add(rec);

				write_count++;
			}
		}

		// zapis batchowy:
		currencyDbWriterService.writeAll(rec_2_write, symbol, timeFrame);
		return write_count;
	}
}
