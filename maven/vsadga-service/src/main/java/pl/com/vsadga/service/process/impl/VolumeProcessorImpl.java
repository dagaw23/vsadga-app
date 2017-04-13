package pl.com.vsadga.service.process.impl;

import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.SpreadSize;
import pl.com.vsadga.data.VolumeSize;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.dto.cache.IndicatorData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.VolumeProcessor;

public class VolumeProcessorImpl implements VolumeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);

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
		BarData prev_bar = dataCache.getPrevBarData(1);
		int prev_vol = 0;
		int result_vol = 0;

		// jaki jest poprzedni wolumen:
		if (prev_bar.getVolumeAbsorb() != null)
			prev_vol = prev_bar.getVolumeAbsorb().intValue();

		if (actualBar.getBarType() == prev_bar.getBarType())
			// typ bara nie zmienił się
			result_vol = prev_vol + actualBar.getBarVolume();
		else
			result_vol = actualBar.getBarVolume();

		return result_vol;
	}
	
	@Override
	public SpreadSize getSpreadSize(BarData barData, String timeFrameDesc) throws BaseServiceException {
		// czy jest już wystarczająca ilość elementów w CACHE:
		if (!dataCache.isReadyIndyDataCache()) {
			LOGGER.info("   [SPREAD] Not ready yet indy DATA CACHE [" + dataCache.getIndicatorCacheSize() + "].");
			return null;
		}
		
		int i = 1;
		TreeSet<IndicatorData> spr_sort_set = dataCache.getSortedCacheBySpread();
		
		for (IndicatorData vol_set : spr_sort_set) {
			
			if (vol_set.getId().intValue() == barData.getId().intValue())
				return getSpreadSizeByPosition(i);
			
			i++;
		}
		
		LOGGER.info("::getSpreadSize:: brak barData na liscie [" + barData.getId() + "].");
		return SpreadSize.N;
	}

	@Override
	public VolumeSize getVolumeSize(BarData barData, String timeFrameDesc) throws BaseServiceException {
		// czy jest już wystarczająca ilość elementów w CACHE:
		if (!dataCache.isReadyIndyDataCache()) {
			LOGGER.info("   [VOL] Not ready yet indy DATA CACHE [" + dataCache.getIndicatorCacheSize() + "].");
			return null;
		}
		
		int i = 1;
		TreeSet<IndicatorData> vol_sort_set = dataCache.getSortedCacheByVolume();
		
		for (IndicatorData vol_set : vol_sort_set) {
			
			if (vol_set.getId().intValue() == barData.getId().intValue())
				return getVolumeSizeByPosition(i);
			
			i++;
		}
		
		LOGGER.info("::getVolumeSize:: brak barData na liscie [" + barData.getId() + "].");
		return VolumeSize.N;
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

	private VolumeSize getVolumeSizeByPosition(int pos) {
		if (pos == 1 || pos == 2)
			return VolumeSize.VH;
		else if (pos == 3 || pos == 4)
			return VolumeSize.Hi;
		else if (pos == 5 || pos == 6 || pos == 7 || pos == 8)
			return VolumeSize.Av;
		else if (pos == 9 || pos == 10)
			return VolumeSize.Lo;
		else if (pos == 11 || pos == 12)
			return VolumeSize.VL;
		else {
			LOGGER.info("::getVolumeSizeByPosition:: bledna pozycja na liscie [" + pos + "].");
			return VolumeSize.N;
		}
	}
	
	private SpreadSize getSpreadSizeByPosition(int pos) {
		if (pos == 1 || pos == 2)
			return SpreadSize.VH;
		else if (pos == 3 || pos == 4)
			return SpreadSize.Hi;
		else if (pos == 5 || pos == 6 || pos == 7 || pos == 8)
			return SpreadSize.Av;
		else if (pos == 9 || pos == 10)
			return SpreadSize.Lo;
		else if (pos == 11 || pos == 12)
			return SpreadSize.VL;
		else {
			LOGGER.info("::getSpreadSizeByPosition:: bledna pozycja na liscie [" + pos + "].");
			return SpreadSize.N;
		}
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
