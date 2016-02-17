package pl.com.vsadga.batch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
		int hr_shift = 0;

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

			// pobierz przesunięcie godzin w dacie:
			hr_shift = getHourShift();

			for (CurrencySymbol sym : symbol_list) {
				for (TimeFrame frm : tmefrm_list) {
					LOGGER.info("   [WRITE] " + sym.getSymbolName() + "-" + frm.getTimeFrameDesc() + ".");
					rewriteFileContent2db(sym, frm, file_path, hr_shift);
				}
			}

		} catch (BaseServiceException e) {
			e.printStackTrace();
			LOGGER.error("::cronJob:: wyjatek BaseServiceException!");
		}

		LOGGER.info("Invoked on " + dateFormat.format(System.currentTimeMillis()) + ".");
	}

	private int getHourShift() throws BaseServiceException {
		String hr_shift = configDataService.getParam("HOUR_SHIFT");
		String shift_value = null;
		boolean is_negative = false;
		int shift_int = 0;

		if (hr_shift == null) {
			LOGGER.info("   [SHIFT] Brak parametru konfiguracyjnego HOUR_SHIFT [" + hr_shift + "].");
			return 0;
		}
		
		if (hr_shift.startsWith("-")) {
			shift_value = hr_shift.substring(1);
			is_negative = true;
		} else
			shift_value = hr_shift;

		if (!StringUtils.isNumeric(shift_value)) {
			LOGGER.info("   [SHIFT] Parametr konfiguracyjny HOUR_SHIFT [" + hr_shift + "] nie jest numeryczny [" + shift_value + "].");
			return 0;
		}
		
		shift_int = Integer.valueOf(shift_value);
		
		if (is_negative)
			shift_int *= -1;
			
		
		LOGGER.info("   [SHIFT] HOUR_SHIFT [" + hr_shift + "], numeric [" + shift_value + "], result [" + shift_int + "].");
		return shift_int;
	}

	private void rewriteFileContent2db(CurrencySymbol symbol, TimeFrame timeFrame, String filePath, int hourShift)
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
			currencyDbWriterService.write(symbol, timeFrame, rec_list, hourShift);

		} catch (ReaderException e) {
			e.printStackTrace();
			LOGGER.error("::rewriteFileContent2db:: wyjatek ReaderException!");
		}
	}
}
