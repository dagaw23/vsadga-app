package pl.com.vsadga.service.process.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.dto.cache.IndicatorData;
import pl.com.vsadga.dto.process.TrendData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.process.IndicatorProcessor;
import pl.com.vsadga.service.process.TrendProcessor;
import pl.com.vsadga.service.process.VolumeProcessor;
import pl.com.vsadga.utils.DateConverter;

public class BarDataProcessorImpl implements BarDataProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BarDataProcessorImpl.class);

	private BarDataDao barDataDao;

	/**
	 * CACHE z danymi z pewnego zakresu
	 */
	private DataCache dataCache;

	private IndicatorProcessor indicatorProcessor;

	private TrendProcessor trendProcessor;

	private VolumeProcessor volumeProcessor;

	/**
	 * czy poprzedni bar - jest do potwierdzenia
	 */
	private boolean isPrevBarToConfirm;
	
	/**
	 * wartość współczynnika dla wyliczanego wskaźnika wolumenu
	 */
	private int volumeSpreadRatio;

	@Override
	public void processBarsData(List<BarData> barDataList, TimeFrame timeFrame) throws BaseServiceException {
		if (barDataList == null || barDataList.isEmpty()) {
			LOGGER.info("   [EMPTY] Lista barow [" + barDataList + "].");
			return;
		}

		BarData bar_data = null;

		// wyczyszczenie danych do analizy:
		dataCache.cleanDataCache();
		setPrevBarToConfirm(false);

		for (int i = 0; i < barDataList.size(); i++) {
			// pobierz bar:
			bar_data = barDataList.get(i);

			// przetworzenie bara wg statusu:
			processByPhase(bar_data, timeFrame);
		}
	}

	/**
	 * @param barDataDao
	 *            the barDataDao to set
	 */
	public void setBarDataDao(BarDataDao barDataDao) {
		this.barDataDao = barDataDao;
	}

	/**
	 * @param dataCache
	 */
	public void setDataCache(DataCache dataCache) {
		this.dataCache = dataCache;
	}

	/**
	 * @param indicatorProcessor
	 *            the indicatorProcessor to set
	 */
	public void setIndicatorProcessor(IndicatorProcessor indicatorProcessor) {
		this.indicatorProcessor = indicatorProcessor;
	}

	/**
	 * @param trendProcessor
	 *            the trendProcessor to set
	 */
	public void setTrendProcessor(TrendProcessor trendProcessor) {
		this.trendProcessor = trendProcessor;
	}

	/**
	 * @param volumeProcessor
	 *            the volumeProcessor to set
	 */
	public void setVolumeProcessor(VolumeProcessor volumeProcessor) {
		this.volumeProcessor = volumeProcessor;
	}
	
	/**
	 * @param volumeSpreadRatio the volumeSpreadRatio to set
	 */
	public void setVolumeSpreadRatio(int volumeSpreadRatio) {
		this.volumeSpreadRatio = volumeSpreadRatio;
	}

	/**
	 * Przetwarza pojedynczy bar - w porównaniu z poprzednim barem.
	 * 
	 * status 0: niekompletny, nie sprawdzamy trendu, status 1: wylicz trend status 2,3: trend już
	 * wyliczony (0 - jeszcze nie zakończony, 2 - czeka na potwierdzenie, 3 - już zakończony)
	 * 
	 * @param barData
	 * @param frameDesc
	 * @throws BaseServiceException
	 */
	private void processByPhase(BarData barData, TimeFrame timeFrame) throws BaseServiceException {
		int bar_phase = barData.getProcessPhase().intValue();

		BarType bar_typ = null;
		TrendData trend_data = null;
		String vol_therm = null;
		int vol_absorb = 0;

		IndicatorInfo ind_info = null;

		// *** status BAR: 0 ***
		if (bar_phase == 0) {
			// LOGGER.info("   [STATS] Bar wg statusu [" + bar_phase +
			// "] jeszcze NIE JEST ZAKONCZONY.");
			return;
		}

		// *** status BAR: 1 ***
		if (bar_phase == 1) {
			// czy poprzedni bar czeka na potwierdzenie:
			checkPrevBarToConfirm(barData, timeFrame.getTimeFrameDesc());
			
			// dodanie do CACHE z wolumenem i spread - aktualnego bara:
			dataCache.addIndicatorData(getIndyData(barData));

			// sprawdzenie typu przetwarzanego bara:
			bar_typ = dataCache.getActualBarType(barData);
			barData.setBarType(bar_typ);

			// sprawdzenie trendu cenowego:
			trend_data = trendProcessor.getActualTrend(barData);
			// oraz trendu wolumenowego:
			vol_therm = volumeProcessor.getVolumeThermometer(barData);
			barData.setVolumeThermometer(vol_therm);
			
			// sprawdzenie poziomu wolumenu:
			barData.setIndicatorWeight(volumeProcessor.getVolumeSize(barData, timeFrame));

			// wolumen obsorbcyjny:
			vol_absorb = volumeProcessor.getAbsorptionVolume(barData, timeFrame.getTimeFrameDesc());
			barData.setVolumeAbsorb(vol_absorb);

			// wielkość wolumenu:
			barData.setVolumeSize(dataCache.getVolumeSize(barData));
			// oraz spreadu:
			barData.setSpreadSize(dataCache.getSpreadSize(barData.getBarHigh(), barData.getBarLow()));

			// sprawdzenie wskaźnika:
			ind_info = indicatorProcessor.getDataIndicator(barData);

			// wpisanie informacji o barze - do tabeli oraz do CACHE:
			updateBarData(trend_data, ind_info, timeFrame.getTimeFrameDesc(), barData);
		}

		// *** status BAR: 2 ***
		if (bar_phase == 2) {
			//LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() + "] do POTWIERDZENIA.");
			setPrevBarToConfirm(true);

			// wpisanie bara do CACHE:
			dataCache.addBarDataWithIndy(barData, getIndyData(barData));
		}

		// *** status BAR: 3 ***
		if (bar_phase == 3) {
			// LOGGER.info("   [STATS] Bar wg statusu [" + bar_phase + "] juz ZAKONCZONY.");
			// czy poprzedni bar czeka na potwierdzenie:
			checkPrevBarToConfirm(barData, timeFrame.getTimeFrameDesc());
			
			dataCache.addBarDataWithIndy(barData, getIndyData(barData));
		}

	}
	
	private IndicatorData getIndyData(BarData barData) {
		BigDecimal bar_spread = barData.getBarHigh().subtract(barData.getBarLow());
		BigDecimal bar_spr_vol = bar_spread.multiply(
				new BigDecimal(volumeSpreadRatio)).multiply(
				new BigDecimal(barData.getBarVolume()));
		bar_spr_vol = bar_spr_vol.add(new BigDecimal(barData.getBarVolume()));
		bar_spr_vol.setScale(4);
		
		return new IndicatorData(barData.getBarVolume(), bar_spread, bar_spr_vol);
	}

	private void checkPrevBarToConfirm(BarData barData, String frameDesc) {
		// czy poprzedni bar wymaga potwierdzenia:
		if (isPrevBarToConfirm == false)
			return;
		
		// pobierz poprzedni bar:
		BarData prev_bar = dataCache.getLastBarData();
		Integer indy_nr = prev_bar.getIndicatorNr();

		if (indy_nr == null || indy_nr.intValue() == 0) {
			LOGGER.info("   [BAR] Poprzedni do potwierdzenia [" + isPrevBarToConfirm + "], ale poprzedni ["
					+ DateConverter.dateToString(prev_bar.getBarTime(), "yy/MM/dd HH:mm")
					+ "] zawiera pusty wskaznik [" + indy_nr + "].");
			return;
		}

		switch (indy_nr.intValue()) {
		case 1:
		case 6:
		case 7:
		case 8:
		case 40: 
		case 58: {
			if (barData.getBarType() == BarType.DOWN_BAR)
				barDataDao.updateIndicatorConfirmation(prev_bar.getId(), 3, true, frameDesc);
			else
				barDataDao.updateProcessPhase(prev_bar.getId(), 3, frameDesc);

			break;
		}
		case 94:
		case 116:
		case 123:
		case 198: {
			if (barData.getBarType() == BarType.UP_BAR)
				barDataDao.updateIndicatorConfirmation(prev_bar.getId(), 3, true, frameDesc);
			else
				barDataDao.updateProcessPhase(prev_bar.getId(), 3, frameDesc);

			break;
		}

		default:
			break;
		}
		
		// odznacz aktualny bar do potwierdzenia:
		setPrevBarToConfirm(false);
	}

	private void updateBarData(TrendData trendData, IndicatorInfo indyInfo, String frameDesc, BarData barData) {
		int process_phase = 3;

		// trend cenowy:
		if (trendData != null) {
			barData.setTrendIndicator(trendData.getTrendIndicator());
			barData.setTrendWeight(trendData.getTrendWeight());
		} else {
			barData.setTrendIndicator(null);
			barData.setTrendWeight(null);
		}

		// jaki jest sygnał:
		if (indyInfo != null) {
			barData.setIndicatorNr(indyInfo.getIndicatorNr());
			barData.setIsConfirm(indyInfo.isConfirm());

			if (!indyInfo.isConfirm())
				process_phase = 2;
		}

		// wpisanie bara do CACHE:
		dataCache.addBarData(barData);

		// wpisanie dla bara - średniej wolumenu:
		barDataDao.updateIndicatorData(barData, process_phase, frameDesc);
	}

	/**
	 * @param isPrevBarToConfirm
	 *            the isPrevBarToConfirm to set
	 */
	private void setPrevBarToConfirm(boolean isPrevBarToConfirm) {
		this.isPrevBarToConfirm = isPrevBarToConfirm;
	}

}
