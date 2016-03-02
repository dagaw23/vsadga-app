package pl.com.vsadga.service.process.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.process.TrendData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.TrendProcessor;

public class TrendProcessorImpl implements TrendProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrendProcessorImpl.class);

	private ConfigDataService configDataService;

	private TrendData prevTrendData;

	@Override
	public void clearTrendData() {
		this.prevTrendData = null;
	}

	@Override
	public TrendData getActualTrend(BarData barData) throws BaseServiceException {
		if (!isProcessTrend()) {
			LOGGER.info("   [TREND] Usluga przetwarzania trendu jest wylaczona.");
			return null;
		}

		// brak jeszcze barów do wyliczenia:
		if (prevTrendData == null) {
			return getUpdated("S", 0, barData.getImaCount());
		}

		// porównaj AKTUALNY bar - z poprzednim (średnia krocząca):
		int bar_compare = barData.getImaCount().compareTo(prevTrendData.getImaCount());

		// trend w poprzednim barze:
		String trend = prevTrendData.getTrendIndicator();
		int weight = prevTrendData.getTrendWeight().intValue();

		// *** poprzedni bar to UPTREND ***
		if (trend.equals("U")) {
			if (bar_compare > 0) {
				// kontynuacja UP
				if (weight == 6 || weight == 7)
					return getUpdated("U", (weight + 1), barData.getImaCount());
				else
					return getUpdated("U", weight, barData.getImaCount());
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return getUpdated(trend, weight, barData.getImaCount());
			} else {
				// zmiana na DOWN
				if (weight == 6)
					return getUpdated("S", 1, barData.getImaCount());
				else
					return getUpdated("U", 6, barData.getImaCount());
			}
		}
		// *** poprzedni bar to DOWNTREND ***
		else if (trend.equals("D")) {
			if (bar_compare < 0) {
				// kontynuacja DOWN
				if (weight == 1 || weight == 2)
					return getUpdated("D", (weight + 1), barData.getImaCount());
				else
					return getUpdated("D", weight, barData.getImaCount());
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return getUpdated(trend, weight, barData.getImaCount());
			} else {
				// zmiana na UP
				if (weight == 1)
					return getUpdated("S", 6, barData.getImaCount());
				else
					return getUpdated("D", 1, barData.getImaCount());
			}
		}
		// *** poprzedni bar jest BOCZNY ***
		else if (trend.equals("S")) {
			if (bar_compare > 0) {
				// bar UP
				if (weight == 6)
					return getUpdated("S", 7, barData.getImaCount());
				else if (weight == 7)
					return getUpdated("U", 8, barData.getImaCount());
				else if (weight == 0 || weight == 1 || weight == 2)
					return getUpdated("S", 6, barData.getImaCount());
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					return getUpdated("S", 0, barData.getImaCount());
				}

			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return getUpdated(trend, weight, barData.getImaCount());
			} else {
				// bar DOWN
				if (weight == 1)
					return getUpdated("S", 2, barData.getImaCount());
				else if (weight == 2)
					return getUpdated("D", 3, barData.getImaCount());
				else if (weight == 0 || weight == 6 || weight == 7)
					return getUpdated("S", 1, barData.getImaCount());
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					return getUpdated("S", 0, barData.getImaCount());
				}
			}
		} else {
			LOGGER.info("   [ERROR] Unexpected situation [" + trend + "," + weight + "," + bar_compare
					+ "], aktualny bar [" + barData + "].");
			return getUpdated("S", 0, barData.getImaCount());
		}
	}
	
	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	private TrendData getUpdated(String trendIndy, Integer trendWeight, BigDecimal imaCount) {
		prevTrendData = new TrendData(trendIndy, trendWeight, imaCount);
		
		return prevTrendData;
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
