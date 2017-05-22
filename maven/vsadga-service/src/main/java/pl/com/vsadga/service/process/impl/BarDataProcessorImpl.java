package pl.com.vsadga.service.process.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.dto.process.TrendData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.process.IndicatorProcessor;
import pl.com.vsadga.service.process.TrendProcessor;
import pl.com.vsadga.service.process.VolumeProcessor;

public class BarDataProcessorImpl implements BarDataProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BarDataProcessorImpl.class);
	
	private BarDataDao barDataDao;
	
	/**
	 * CACHE z danymi z pewnego zakresu
	 */
	private DataCache dataCache;
	
	private void fillDataCache(BarData barData, String timeFrameDesc) {
		// dane główne do przetwarzania barów:
		List<BarData> bar_list = barDataDao.getPartialDataNext(barData.getSymbolId(), timeFrameDesc, 2,
				barData.getBarTime());
		dataCache.fillBarDataCache(bar_list);

		// dane wolumenowe i spreadowe:
		bar_list = barDataDao.getPartialDataNext(barData.getSymbolId(), timeFrameDesc, 11, barData.getBarTime());
		dataCache.fillIndyDataCache(bar_list, barData);
	}

	private IndicatorProcessor indicatorProcessor;

	private TrendProcessor trendProcessor;

	private VolumeProcessor volumeProcessor;

	/**
	 * czy poprzedni bar - jest do potwierdzenia
	 */
	private boolean isPrevBarToConfirm;
	
	@Override
	public void processBarDataByPhase(List<BarData> barDataList, String timeFrameDesc) throws BaseServiceException {
		if (barDataList == null || barDataList.isEmpty()) {
			LOGGER.info("   [EMPTY] Lista barow [" + barDataList + "].");
			return;
		}
		BarData bar_data = null;

		// wyczyszczenie danych do analizy:
		dataCache.cleanDataCache();

		for (int i = 0; i < barDataList.size(); i++) {
			// pobierz bar:
			bar_data = barDataList.get(i);

			// przetworzenie bara wg statusu:
			processByPhase(bar_data, timeFrameDesc);
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
	 * Przetwarza pojedynczy bar - w porównaniu z poprzednim barem.
	 * 
	 * status 0: niekompletny, nie sprawdzamy trendu, status 1: wylicz trend status 2,3: trend już
	 * wyliczony (0 - jeszcze nie zakończony, 2 - czeka na potwierdzenie, 3 - już zakończony)
	 * 
	 * @param barData aktualnie przetwarzany bar (ze statusem 1 lub 2)
	 * @param frameDesc
	 * @throws BaseServiceException
	 */
	private void processByPhase(BarData barData, String timeFrameDesc) throws BaseServiceException {
		// pobierz status bara:
		int bar_phase = barData.getProcessPhase().intValue();
		
		BarType bar_typ = null;
		TrendData trend_data = null;
		int vol_absorb = 0;

		IndicatorInfo ind_info = null;

		if (bar_phase == 1) {
			// *** status BAR: 1 ***
			
			// pobierz dokładną informację o 2 poprzednich barach oraz wolumenowy CACHE z 11 barów:
			fillDataCache(barData, timeFrameDesc);
			
			// czy poprzedni bar czeka na potwierdzenie:
			//checkPrevBarToConfirm(barData, timeFrame.getTimeFrameDesc());

			// sprawdzenie typu przetwarzanego bara:
			bar_typ = dataCache.getActualBarType(barData);
			barData.setBarType(bar_typ);

			// sprawdzenie trendu cenowego:
			trend_data = trendProcessor.getActualTrend(barData);
			
			// sprawdzenie poziomu wolumenu:
			barData.setVolumeSize(volumeProcessor.getVolumeSize(barData, timeFrameDesc));
			
			// sprawdzenie poziomu spreadu:
			barData.setSpreadSize(volumeProcessor.getSpreadSize(barData, timeFrameDesc));

			// wolumen obsorbcyjny:
			vol_absorb = volumeProcessor.getAbsorptionVolume(barData, timeFrameDesc);
			barData.setVolumeAbsorb(vol_absorb);

			// sprawdzenie wskaźnika:
			ind_info = indicatorProcessor.getDataIndicator(barData);

			// wpisanie informacji o barze - do tabeli oraz do CACHE:
			updateBarData(trend_data, ind_info, timeFrameDesc, barData);
			
		} else if (bar_phase == 2) {
			// *** status BAR: 2 ***
		
			// wpisanie bara do CACHE:
			//dataCache.addBarDataWithIndy(barData, getIndyData(barData));
			//TODO dodać przetwarzanie statusu 2
			
		} else {
			// przetwarzane są tylko statusy 1 i 2 z barów:
			LOGGER.error("   [PHASE] Brak obslugi dla fazy [" + bar_phase + "] bara o ID=[" + barData.getId() + "].");
		}
	}

	/*private void checkPrevBarToConfirm(BarData barData, String frameDesc) {
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
	}*/

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

		barDataDao.updateIndicatorData(barData, process_phase, frameDesc);
	}

}
