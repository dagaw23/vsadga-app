package pl.com.vsadga.service.process.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.process.DataCache;
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
	private DataCache indicatorData;

	private IndicatorProcessor indicatorProcessor;

	private TrendProcessor trendProcessor;

	private VolumeProcessor volumeProcessor;

	@Override
	public void processBarsData(List<BarData> barDataList, TimeFrame timeFrame) throws BaseServiceException {
		if (barDataList == null || barDataList.isEmpty()) {
			LOGGER.info("   [EMPTY] Lista barow [" + barDataList + "].");
			return;
		}

		int bar_count = barDataList.size();
		BarData bar_data = null;
		
		// wyczyszczenie danych do analizy:
		indicatorData.cleanDataCache();

		for (int i = 0; i < bar_count; i++) {
			// pobierz bar:
			bar_data = barDataList.get(i);

			// przetworzenie bara wg statusu:
			processByPhase(bar_data, timeFrame.getTimeFrameDesc());
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
	 * @param indicatorData
	 *            the indicatorData to set
	 */
	public void setIndicatorData(DataCache indicatorData) {
		this.indicatorData = indicatorData;
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
	 * @param barData
	 * @param frameDesc
	 * @throws BaseServiceException
	 */
	private void processByPhase(BarData barData, String frameDesc) throws BaseServiceException {
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
			// LOGGER.info("   [STATS] Bar ze statusem [" + bar_phase +
			// "] - KOMPLETNE wyliczanie.");
			// status 0: niekompletny, nie sprawdzamy trendu
			// status 1: wylicz trend
			// status 2,3: trend już wyliczony
			// sprawdzenie trendu - tylko dla statusu 1
			// (0 - jeszcze nie zakończony, 2 - czeka na potwierdzenie, 3 - już zakończony)
			
			// sprawdzenie typu przetwarzanego bara:
			bar_typ = indicatorData.getActualBarType(barData);
			barData.setBarType(bar_typ);
			
			// wpisanie wolumenu do wyliczenia średnich:
			indicatorData.addVolumeData(barData.getBarVolume());
			barData.setVolumeAvgShort(indicatorData.getShortVolumeAvg());
			barData.setVolumeAvgMedium(indicatorData.getMediumVolumeAvg());
			barData.setVolumeAvgLong(indicatorData.getLongVolumeAvg());

			// sprawdzenie trendu cenowego:
			trend_data = trendProcessor.getActualTrend(barData);
			
			// oraz trendu wolumenowego:
			vol_therm = volumeProcessor.getVolumeThermometer(barData);
			barData.setVolumeThermometer(vol_therm);
			vol_absorb = volumeProcessor.getAbsorptionVolume(barData, frameDesc);
			barData.setVolumeAbsorb(vol_absorb);
			
			// sprawdzenie wskaźnika:
			ind_info = indicatorProcessor.getDataIndicator(barData, frameDesc);

			// wpisanie informacji o barze - do tabeli oraz do CACHE:
			updateBarData(trend_data, ind_info, frameDesc, barData);
			
		}

		// *** status BAR: 2 ***
		if (bar_phase == 2) {
			// LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() +
			// "] do POTWIERDZENIA.");

			// wpisanie bara do CACHE:
			indicatorData.addBarData(barData);
		}

		// *** status BAR: 3 ***
		if (bar_phase == 3) {
			// LOGGER.info("   [STATS] Bar wg statusu [" + bar_phase + "] juz ZAKONCZONY.");
			indicatorData.addBarData(barData);
		}

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
			int indy_nr = indyInfo.getIndicatorNr();
			barData.setIndicatorNr(indy_nr);

			// jeśli został przetworzony i jest większy od zera
			// - dla części sygnałów jest potrzebne potwierdzenie:
			// TODO && isIndicatorToConfirm(indy_nr)
			if (indyInfo.isProcessIndy() && indy_nr > 0) {
				process_phase = 2;
				barData.setIsConfirm(false);
			}
		}

		// wpisanie bara do CACHE:
		indicatorData.addBarData(barData);
		
		// wpisanie dla bara - średniej wolumenu:
		barDataDao.updateIndicatorData(barData, process_phase, frameDesc);
	}

}
