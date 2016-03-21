package pl.com.vsadga.service.process.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.process.IndicatorData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.VolumeProcessor;

public class VolumeProcessorImpl implements VolumeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);

	private ConfigDataService configDataService;

	private IndicatorData indicatorData;
	
	private BarDataDao barDataDao;

	@Override
	public int getAbsorptionVolume(BarData actualBar, String frameDesc) throws BaseServiceException {
		if (!isProcessVolume()) {
			LOGGER.info("   [VOL] Usluga przetwarzania wolumenu absorbcyjnego jest wylaczona.");
			return 0;
		}

		// czy jest już chociaż 1 bar do porównania:
		if (indicatorData.isWritedShortTermBars(1)) {
			LOGGER.info("   [VOL] Dane jeszcze nie sa gotowe do wyliczenia wolumenu absorbcyjnego.");
			return 0;
		}

		// pobierz poprzedni bar:
		BarStatsData prev_bar = indicatorData.getLastBarData();
		int prev_vol = 0;
		int result_vol = 0;
		
		// jaki jest poprzedni wolumen:
		if (prev_bar.getVolumeAbsorb() != null)
			prev_vol = prev_bar.getVolumeAbsorb().intValue();
		
		if (actualBar.getBarType() == prev_bar.getBarType()) {
			// typ bara nie zmienił się
			result_vol = prev_vol + actualBar.getBarVolume();
			
			// usunąć z poprzedniego bara:
			barDataDao.updateVolumeAbsorbtion(frameDesc, actualBar.getId(), null);
		} else {
			result_vol = actualBar.getBarVolume();
		}

		return result_vol;
	}

	@Override
	public String getVolumeThermometer(BarData actualBar) throws BaseServiceException {
		if (!isProcessVolume()) {
			LOGGER.info("   [VOL] Usluga przetwarzania trendu wolumenu jest wylaczona.");
			return null;
		}

		// czy CACHE dla UP/DOWN bar jest wypełniony:
		if (!indicatorData.isReadyBarDataCache()) {
			LOGGER.info("   [VOL] Dane jeszcze nie sa gotowe do wyliczenia trendu wolumenu.");
			return "N";
		}

		// sprawdzenie, który wolumen jest większy:
		int vol_comp = indicatorData.compareLastVolumeData(actualBar);

		if (vol_comp > 0)
			return "U";
		else if (vol_comp < 0)
			return "D";
		else
			return "L";
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
