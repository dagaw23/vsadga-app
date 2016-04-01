package pl.com.vsadga.service.process.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.dto.process.TrendData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.TrendProcessor;

public class TrendProcessorImpl implements TrendProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrendProcessorImpl.class);

	private ConfigDataService configDataService;

	private DataCache dataCache;

	@Override
	public TrendData getActualTrend(BarData barData) throws BaseServiceException {
		if (!isProcessTrend()) {
			LOGGER.info("   [TREND] Usluga przetwarzania trendu jest wylaczona.");
			return null;
		}

		// czy jest ostatni bar do wyliczenia:
		BarStatsData prev_bar = dataCache.getLastBarData();
		if (prev_bar == null) {
			return new TrendData("S", 0);
		}

		// porównaj AKTUALNY bar - z poprzednim (średnia krocząca):
		int bar_compare = barData.getImaCount().compareTo(prev_bar.getImaCount());
		TrendData result = null;

		// trend w poprzednim barze:
		String trend = prev_bar.getTrendIndicator();
		int weight = prev_bar.getTrendWeight().intValue();

		// *** poprzedni bar to UPTREND ***
		if (trend.equals("U")) {
			if (bar_compare > 0) {
				// kontynuacja UP
				if (weight == 6 || weight == 7)
					result = new TrendData("U", (weight + 1));
				else
					result = new TrendData("U", weight);
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				result = new TrendData(trend, weight);
			} else {
				// zmiana na DOWN
				if (weight == 6)
					result = new TrendData("S", 1);
				else
					result = new TrendData("U", 6);
			}
		}
		// *** poprzedni bar to DOWNTREND ***
		else if (trend.equals("D")) {
			if (bar_compare < 0) {
				// kontynuacja DOWN
				if (weight == 1 || weight == 2)
					result = new TrendData("D", (weight + 1));
				else
					result = new TrendData("D", weight);
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				result = new TrendData(trend, weight);
			} else {
				// zmiana na UP
				if (weight == 1)
					result = new TrendData("S", 6);
				else
					result = new TrendData("D", 1);
			}
		}
		// *** poprzedni bar jest BOCZNY ***
		else if (trend.equals("S")) {
			if (bar_compare > 0) {
				// bar UP
				if (weight == 6 || weight == 7)
					result = new TrendData("U", (weight + 1));
				else if (weight == 0 || weight == 1 || weight == 2)
					result = new TrendData("S", 6);
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					result = new TrendData("S", 0);
				}

			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				result = new TrendData(trend, weight);
			} else {
				// bar DOWN
				if (weight == 1 || weight == 2)
					result = new TrendData("D", (weight + 1));
				else if (weight == 0 || weight == 6 || weight == 7)
					result = new TrendData("S", 1);
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					result = new TrendData("S", 0);
				}
			}
		} else {
			LOGGER.info("   [ERROR] Unexpected situation [" + trend + "," + weight + "," + bar_compare
					+ "], aktualny bar [" + barData + "].");
			result = new TrendData("S", 0);
		}

		return result;
	}

	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	/**
	 * @param dataCache
	 */
	public void setDataCache(DataCache dataCache) {
		this.dataCache = dataCache;
	}

	private boolean isProcessTrend() throws BaseServiceException {
		String param_value = configDataService.getParam("IS_PROCESS_TREND");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [TREND] Brak parametru IS_PROCESS_TREND [" + param_value + "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [TREND] Parametr IS_PROCESS_TREND [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1)
			return true;
		else
			return false;
	}

}
