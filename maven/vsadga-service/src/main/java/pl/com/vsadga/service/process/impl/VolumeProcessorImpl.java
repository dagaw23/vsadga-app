package pl.com.vsadga.service.process.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.VolumeProcessor;

public class VolumeProcessorImpl implements VolumeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);

	private BarDataDao barDataDao;

	private ConfigDataService configDataService;

	private DataCache dataCache;

	@Override
	public int getAbsorptionVolume(BarData actualBar, String frameDesc) throws BaseServiceException {
		if (!isProcessVolume()) {
			LOGGER.info("   [ABS] Usluga przetwarzania wolumenu absorbcyjnego jest wylaczona.");
			return 0;
		}

		// czy jest już chociaż 1 bar do porównania:
		if (!dataCache.isWritedShortTermBars(1)) {
			LOGGER.info("   [ABS] Dane jeszcze nie sa gotowe do wyliczenia wolumenu absorbcyjnego.");
			return 0;
		}

		// pobierz poprzedni bar:
		BarStatsData prev_bar = dataCache.getLastBarData();
		//LOGGER.info("   [ABS] " + prev_bar.getBarVolume() + "-" + prev_bar.getBarType() + "-" + prev_bar.getVolumeAbsorb());
		//LOGGER.info("         " + actualBar.getBarVolume() + "-" + actualBar.getBarType() + ".");
		int prev_vol = 0;
		int result_vol = 0;

		// jaki jest poprzedni wolumen:
		if (prev_bar.getVolumeAbsorb() != null)
			prev_vol = prev_bar.getVolumeAbsorb().intValue();

		if (actualBar.getBarType() == prev_bar.getBarType()) {
			// typ bara nie zmienił się
			result_vol = prev_vol + actualBar.getBarVolume();

			// usunąć z poprzedniego bara:
			barDataDao.updateVolumeAbsorbtion(frameDesc, prev_bar.getId(), null);
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
		if (!dataCache.isWritedShortTermBars(3)) {
			LOGGER.info("   [VOL] Dane jeszcze nie sa gotowe do wyliczenia trendu wolumenu.");
			return "N";
		}

		// sprawdzenie, który wolumen jest większy:
		int vol_comp = dataCache.compareLastVolumeData(actualBar);

		if (vol_comp > 0)
			return "U";
		else if (vol_comp < 0)
			return "D";
		else
			return "L";
	}

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
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

	private boolean isProcessVolume() throws BaseServiceException {
		String param_value = configDataService.getParam("IS_PROCESS_VOLUME");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [VOL] Brak parametru IS_PROCESS_VOLUME [" + param_value + "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [VOL] Parametr IS_PROCESS_VOLUME [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1)
			return true;
		else
			return false;
	}

}
