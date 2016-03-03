package pl.com.vsadga.batch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.data.CurrencyDataService;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.symbol.SymbolService;
import pl.com.vsadga.service.timeframe.TimeFrameService;
import pl.com.vsadga.utils.DateConverter;

@Component
public class DataAnalyseBatch {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyseBatch.class);

	@Autowired
	private ConfigDataService configDataService;

	@Autowired
	private CurrencyDataService currencyDataService;

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private TimeFrameService timeFrameService;

	@Autowired
	private BarDataProcessor barDataProcessor;

	@Scheduled(cron = "10 * * * * MON-FRI")
	public void cronJob() {
		List<CurrencySymbol> symbol_list = null;
		List<TimeFrame> tmefrm_list = null;
		List<BarData> data_list = null;
		Date end_date = null;
		int bar_count = 0;

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

			// pobierz konfigurację o dacie przesunięcia danych do przetwarzania:
			end_date = getAnalyseEndDate();
			// oraz liczbę barów do przetworzenia:
			bar_count = getAnalyseBarCount();

			for (CurrencySymbol symbol : symbol_list) {
				for (TimeFrame tme_frame : tmefrm_list) {
					LOGGER.info("   [PROC] Symbol [" + symbol.getSymbolName() + "] in ["
							+ tme_frame.getTimeFrameDesc() + "].");

					// pobierz listę danych z bara:
					data_list = getBarData(end_date, bar_count, symbol, tme_frame);

					// przetwórz listę barów:
					barDataProcessor.processBarsData(data_list, tme_frame);
				}
			}
		} catch (BaseServiceException e) {
			e.printStackTrace();
		}
	}

	private List<BarData> getBarData(Date endDate, int barCount, CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException {
		List<BarData> bar_list = null;

		// liczba barów musi zostać podana:
		if (barCount == 0)
			throw new BaseServiceException("::getBarData:: brak liczby barow ANALYSE_BAR_COUNT w CONFIG_DATA.");

		// czy jest podana data graniczna dla barów:
		if (endDate == null)
			bar_list = currencyDataService.getLastNbarData(barCount, symbol, timeFrame);
		else
			bar_list = currencyDataService.getLastNbarDataFromTime(barCount, symbol, timeFrame, endDate);

		// wypisy kontrolne:
		int list_size = bar_list.size();
		String msg = "   [PROC] Liczba barow do przetworzenia: " + list_size;

		if (list_size > 0)
			msg += " (" + DateConverter.dateToString(bar_list.get(0).getBarTime(), "yy/MM/dd HH:mm") + ","
					+ DateConverter.dateToString(bar_list.get(list_size - 1).getBarTime(), "yy/MM/dd HH:mm")
					+ ").";

		LOGGER.info(msg);

		return bar_list;
	}

	private int getAnalyseBarCount() throws BaseServiceException {
		String param_value = configDataService.getParam("ANALYSE_BAR_COUNT");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru IS_BATCH_ANALYSE [" + param_value + "] w tabeli CONFIG_DATA.");
			return 0;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [BATCH] Parametr IS_BATCH_ANALYSE [" + param_value + "] nie jest numeryczny.");
			return 0;
		}

		return Integer.valueOf(param_value);
	}

	private Date getAnalyseEndDate() throws BaseServiceException {
		String param_value = configDataService.getParam("ANALYSE_END_DATE");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru ANALYSE_END_DATE [" + param_value + "] w tabeli CONFIG_DATA.");
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		sdf.setLenient(false);

		try {
			return sdf.parse(param_value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean isProcessBatch() throws BaseServiceException {
		String param_value = configDataService.getParam("IS_BATCH_ANALYSE");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [BATCH] Brak parametru IS_BATCH_ANALYSE [" + param_value + "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [BATCH] Parametr IS_BATCH_ANALYSE [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1) {
			return true;
		} else {
			LOGGER.info("   [BATCH] Wylaczony Batch analizy barow [" + is_proc + "].");
			return false;
		}
	}

}
