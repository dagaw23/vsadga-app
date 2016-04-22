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
		Date end_date = null;
		int bar_count = 0;

		try {
			// sprawdzenie, czy BATCH nie jest zatrzymany:
			if (!isProcessBatch())
				return;

			// wpisanie konfiguracji do VolumeProcessor:
			initConfigData();

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

	private int getAnalyseBarCount() throws BaseServiceException {
		Integer param_int = getIntParamValue("ANALYSE_BAR_COUNT");

		if (param_int == null)
			throw new BaseServiceException("::getAnalyseBarCount:: brak parametru ANALYSE_BAR_COUNT [" + param_int
					+ "] w CONFIG_DATA.");

		return param_int;
	}

	private Date getAnalyseEndDate() throws BaseServiceException {
		String param_value = getStringParamValue("ANALYSE_END_DATE");

		if (param_value == null)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		sdf.setLenient(false);

		try {
			return sdf.parse(param_value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<BarData> getBarData(Date endDate, int barCount, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException {
		List<BarData> bar_list = null;

		// czy jest podana data graniczna dla barów:
		if (endDate == null)
			bar_list = currencyDataService.getLastNbarData(barCount, symbol, timeFrame);
		else
			bar_list = currencyDataService.getLastNbarDataToDate(barCount, symbol, timeFrame, endDate);

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

	private void initConfigData() throws BaseServiceException {
		volumeProcessor.initLevelPositions(getStringParamValue("H4_LEVELS").split(","),
				getStringParamValue("H1_LEVELS").split(","),
				getStringParamValue("M15_LEVELS").split(","),
				getStringParamValue("M5_LEVELS").split(","));
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
