package pl.com.vsadga.service.process.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.process.IndicatorData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.VolumeProcessor;

public class VolumeProcessorImpl implements VolumeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);

	private ConfigDataService configDataService;

	private IndicatorData indicatorData;

	@Override
	public String getVolumeThermometer(BarData actualBar) throws BaseServiceException {
		if (!isProcessVolume()) {
			LOGGER.info("   [VOL] Usluga przetwarzania trendu wolumenu jest wylaczona.");
			return null;
		}

		// czy cały CACHE jest wypełniony:
		if (!indicatorData.isReadyShortTermData()) {
			LOGGER.info("   [VOL] Dane jeszcze nie sa gotowe do wyliczenia trendu wolumenu.");
			return "N";
		}

		return indicatorData.getVolumeThermometer(actualBar);
	}

	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	/**
	 * @param indicatorData
	 *            the indicatorData to set
	 */
	public void setIndicatorData(IndicatorData indicatorData) {
		this.indicatorData = indicatorData;
	}

	private boolean isProcessVolume() throws BaseServiceException {
		String param_value = configDataService.getParam("IS_PROCESS_VOLUME");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [TREND] Brak parametru IS_PROCESS_VOLUME [" + param_value + "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [TREND] Parametr IS_PROCESS_VOLUME [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1)
			return true;
		else
			return false;
	}

}
