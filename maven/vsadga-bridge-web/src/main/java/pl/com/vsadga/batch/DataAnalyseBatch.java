package pl.com.vsadga.batch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.process.VolumeProcessor;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.utils.DateConverter;

@Component
public class DataAnalyseBatch extends BaseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyseBatch.class);

	@Autowired
	private BarDataProcessor barDataProcessor;

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private VolumeProcessor volumeProcessor;

	@Scheduled(cron = "10 * * * * SUN-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;
		List<BarData> data_list = null;
		int bar_back_count = 0;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// pobierz listę aktywnych symboli:
			symbol_list = symbolService.getActiveSymbols();
			// pobierz listę aktywnych timeframe:
			tmefrm_list = timeFrameService.getAllActive();

			if (symbol_list.isEmpty() || tmefrm_list.isEmpty()) {
				LOGGER.info("   [BATCH] Zadne symbole nie sa aktywne [" + symbol_list.size()
						+ "] ani ramy czasowe [" + tmefrm_list.size() + "].");
				return;
			}

			// pobierz konfigurację o  liczbie barów do przetworzenia:
			bar_back_count = getAnalyseBarCount();

			for (CurrencySymbol symbol : symbol_list) {
				for (TimeFrame tme_frame : tmefrm_list) {
					LOGGER.info("   [PROC] Symbol [" + symbol.getSymbolName() + "] in ["
							+ tme_frame.getTimeFrameDesc() + "].");

					// pobierz listę barów ze statusem 1: 
					data_list = getBarData(bar_back_count, symbol, tme_frame, 1);

					// przetwórz listę barów:
					barDataProcessor.processBarsData(data_list, tme_frame);
				}
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}

	private int getAnalyseBarCount() throws BaseServiceException {
		Integer param_int = getIntParamValue("ANALYSE_BAR_COUNT");

		if (param_int == null)
			throw new BaseServiceException("::getAnalyseBarCount:: brak parametru ANALYSE_BAR_COUNT [" + param_int
					+ "] w CONFIG_DATA.");

		return param_int;
	}

	private List<BarData> getBarData(int barBackCount, CurrencySymbol symbol, TimeFrame timeFrame,
			Integer processPhase) throws BaseServiceException {
		List<BarData> bar_list = currencyDataService.getBarDataListByPhase(symbol.getId(),
				timeFrame.getTimeFrameDesc(), processPhase);

		// wypisy kontrolne:
		int list_size = bar_list.size();
		String msg = "   [PROC] Liczba barow do przetworzenia: [" + list_size + "] ze statusem [" + processPhase
				+ "]";

		if (list_size > 0)
			msg += " (" + DateConverter.dateToString(bar_list.get(0).getBarTime(), "yy/MM/dd HH:mm") + ","
					+ DateConverter.dateToString(bar_list.get(list_size - 1).getBarTime(), "yy/MM/dd HH:mm")
					+ ").";

		LOGGER.info(msg);

		return bar_list;
	}

	private boolean isProcessBatch() throws BaseServiceException {
		Integer is_proc = getIntParamValue("IS_BATCH_ANALYSE");

		if (is_proc == null)
			return false;

		if (is_proc.intValue() == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch analizy barow [" + is_proc + "].");
			return false;
		}
	}

}
