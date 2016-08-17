package pl.com.vsadga.service.process.impl;

import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.VolumeSize;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.VolumeProcessor;

public class VolumeProcessorImpl implements VolumeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);

	private BarDataDao barDataDao;

	private ConfigDataService configDataService;

	private DataCache dataCache;

	private int h1PositionLong;

	private int h1PositionMedium;

	private int h1PositionShort;

	private int h1PositionUltraLong;

	private int h4PositionLong;

	private int h4PositionMedium;

	private int h4PositionShort;

	private int h4PositionUltraLong;

	private int m15PositionLong;

	private int m15PositionMedium;

	private int m15PositionShort;

	private int m15PositionUltraLong;

	private int m5PositionLong;

	private int m5PositionMedium;

	private int m5PositionShort;

	private int m5PositionUltraLong;

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
		BarData prev_bar = dataCache.getLastBarData();
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
	public VolumeSize getVolumeSize(BarData actualBar, TimeFrame timeFrame) throws BaseServiceException {
		// aktualna data:
		GregorianCalendar act_cal = new GregorianCalendar();
		act_cal.setTime(actualBar.getBarTime());

		// liczba barow wstecz:
		int row_count = barDataDao.getRowNumber(actualBar.getSymbolId(), timeFrame.getTimeFrameDesc(), act_cal.getTime());

		// *** 5 minut ***
		if (timeFrame.getTimeFrame().intValue() == 5) {
			// Ultra Long:
			if (row_count >= m5PositionUltraLong && isGreaterVolume(actualBar, timeFrame, m5PositionUltraLong))
				return VolumeSize.VH;

			// Long:
			if (row_count >= m5PositionLong && isGreaterVolume(actualBar, timeFrame, m5PositionLong))
				return VolumeSize.Hi;

			// Medium:
			if (row_count >= m5PositionMedium && isGreaterVolume(actualBar, timeFrame, m5PositionMedium))
				return VolumeSize.AV;

			// Short:
			if (row_count >= m5PositionShort && isGreaterVolume(actualBar, timeFrame, m5PositionShort))
				return VolumeSize.Lo;
			
			return VolumeSize.N;
		}

		// *** 15 minut ***
		if (timeFrame.getTimeFrame().intValue() == 15) {
			// Ultra Long:
			if (row_count >= m15PositionUltraLong && isGreaterVolume(actualBar, timeFrame, m15PositionUltraLong))
				return VolumeSize.VH;
			
			// Long:
			if (row_count >= m15PositionLong && isGreaterVolume(actualBar, timeFrame, m15PositionLong))
				return VolumeSize.Hi;
			
			// Medium:
			if (row_count >= m15PositionMedium && isGreaterVolume(actualBar, timeFrame, m15PositionMedium))
				return VolumeSize.AV;
			
			// Short:
			if (row_count >= m15PositionShort && isGreaterVolume(actualBar, timeFrame, m15PositionShort))
				return VolumeSize.Lo;

			return VolumeSize.N;
		}

		// *** 1 godzina ***
		if (timeFrame.getTimeFrame().intValue() == 60) {
			// Ultra Long:
			if (row_count >= h1PositionUltraLong && isGreaterVolume(actualBar, timeFrame, h1PositionUltraLong))
				return VolumeSize.VH;
			
			// Long:
			if (row_count >= h1PositionLong && isGreaterVolume(actualBar, timeFrame, h1PositionLong))
				return VolumeSize.Hi;
			
			// Medium:
			if (row_count >= h1PositionMedium && isGreaterVolume(actualBar, timeFrame, h1PositionMedium))
				return VolumeSize.AV;
			
			// Short:
			if (row_count >= h1PositionShort && isGreaterVolume(actualBar, timeFrame, h1PositionShort))
				return VolumeSize.Lo;

			return VolumeSize.N;
		}

		// *** 4 godziny ***
		if (timeFrame.getTimeFrame().intValue() == 240) {
			// Ultra Long:
			if (row_count >= h4PositionUltraLong && isGreaterVolume(actualBar, timeFrame, h4PositionUltraLong))
				return VolumeSize.VH;
			
			// Long:
			if (row_count >= h4PositionLong && isGreaterVolume(actualBar, timeFrame, h4PositionLong))
				return VolumeSize.Hi;
			
			// Medium:
			if (row_count >= h4PositionMedium && isGreaterVolume(actualBar, timeFrame, h4PositionMedium))
				return VolumeSize.AV;
			
			// Short:
			if (row_count >= h4PositionShort && isGreaterVolume(actualBar, timeFrame, h4PositionShort))
				return VolumeSize.Lo;

			return VolumeSize.N;
		}

		return null;
	}

	@Override
	public void initLevelPositions(String[] h4PositionTab, String[] h1PositionTab, String[] m15PositionTab,
			String[] m5PositionTab) throws BaseServiceException {
		if (h4PositionTab.length != 4)
			throw new BaseServiceException("Bledna ilosc parametrow [" + h4PositionTab.length + "] dla H4.");
		if (h1PositionTab.length != 4)
			throw new BaseServiceException("Bledna ilosc parametrow [" + h1PositionTab.length + "] dla H1.");
		if (m15PositionTab.length != 4)
			throw new BaseServiceException("Bledna ilosc parametrow [" + m15PositionTab.length + "] dla M15.");
		if (m5PositionTab.length != 4)
			throw new BaseServiceException("Bledna ilosc parametrow [" + m5PositionTab.length + "] dla M5.");

		initH4Positions(h4PositionTab[0], h4PositionTab[1], h4PositionTab[2], h4PositionTab[3]);
		initH1Positions(h1PositionTab[0], h1PositionTab[1], h1PositionTab[2], h1PositionTab[3]);
		initM15Positions(m15PositionTab[0], m15PositionTab[1], m15PositionTab[2], m15PositionTab[3]);
		initM5Positions(m5PositionTab[0], m5PositionTab[1], m5PositionTab[2], m5PositionTab[3]);
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

	private void initH1Positions(String shortPos, String mediumPos, String longPos, String ultraPos) {
		this.h1PositionShort = Integer.valueOf(shortPos);
		this.h1PositionMedium = Integer.valueOf(mediumPos);
		this.h1PositionLong = Integer.valueOf(longPos);
		this.h1PositionUltraLong = Integer.valueOf(ultraPos);
	}

	private void initH4Positions(String shortPos, String mediumPos, String longPos, String ultraPos) {
		this.h4PositionShort = Integer.valueOf(shortPos);
		this.h4PositionMedium = Integer.valueOf(mediumPos);
		this.h4PositionLong = Integer.valueOf(longPos);
		this.h4PositionUltraLong = Integer.valueOf(ultraPos);
	}

	private void initM15Positions(String shortPos, String mediumPos, String longPos, String ultraPos) {
		this.m15PositionShort = Integer.valueOf(shortPos);
		this.m15PositionMedium = Integer.valueOf(mediumPos);
		this.m15PositionLong = Integer.valueOf(longPos);
		this.m15PositionUltraLong = Integer.valueOf(ultraPos);
	}

	private void initM5Positions(String shortPos, String mediumPos, String longPos, String ultraPos) {
		this.m5PositionShort = Integer.valueOf(shortPos);
		this.m5PositionMedium = Integer.valueOf(mediumPos);
		this.m5PositionLong = Integer.valueOf(longPos);
		this.m5PositionUltraLong = Integer.valueOf(ultraPos);
	}

	private boolean isGreaterVolume(BarData actualBar, TimeFrame timeFrame, int limit) {
		Integer max_vol = barDataDao.getMaxVolume(actualBar.getSymbolId(), timeFrame.getTimeFrameDesc(),
				actualBar.getBarTime(), limit);

		if (actualBar.getBarVolume().intValue() > max_vol.intValue())
			return true;
		else
			return false;
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
