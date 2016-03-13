package pl.com.vsadga.service.process.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.process.IndicatorData;
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
	private IndicatorData indicatorData;

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
	public void setIndicatorData(IndicatorData indicatorData) {
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
		TrendData trend_data = null;
		IndicatorInfo ind_info = null;
		String vol_therm = null;

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
			trend_data = trendProcessor.getActualTrend(barData);

			// sprawdzenie wskaźnika:
			ind_info = indicatorProcessor.getDataIndicator(barData, frameDesc);

			// sprawdzenie trendu wolumenu:
			vol_therm = volumeProcessor.getVolumeThermometer(barData);

			// wpisanie informacji o barze - do tabeli oraz do CACHE:
			updateBarData(trend_data, ind_info, vol_therm, frameDesc, barData);
			
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

	private void updateBarData(TrendData trendData, IndicatorInfo indyInfo, String volTherm, String frameDesc, BarData barData) {
		String trend_indy = null;
		Integer trend_weight = null;
		Integer indy_nr = null;
		int process_phase = 3;

		// jaki jest trend:
		if (trendData != null) {
			trend_indy = trendData.getTrendIndicator();
			trend_weight = trendData.getTrendWeight();
		}

		// jaki jest sygnał:
		if (indyInfo != null) {
			indy_nr = indyInfo.getIndicatorNr();

			// jeśli został przetworzony i jest większy od zera
			// - dla części sygnałów jest potrzebne potwierdzenie:
			// TODO && isIndicatorToConfirm(indy_nr)
			if (indyInfo.isProcessIndy() && indy_nr.intValue() > 0) {
				// process_phase = 2;
				barDataDao.updateIndicatorWithTrend(barData.getId(), frameDesc, 2, 
						trend_indy, trend_weight, volTherm,
						indy_nr, false);
				return;
			}
		}

		barDataDao.updateProcessPhaseWithTrend(barData.getId(), frameDesc, process_phase,
				trend_indy, trend_weight, volTherm);
		
		// wpisanie bara do CACHE:
		indicatorData.addBarData(barData, trend_indy, trend_weight, volTherm);
		
		// wpisanie dla bara - średniej wolumenu:
		barDataDao.updateVolumeAvg(barData.getId(),, frameDesc,
				indicatorData.getShortVolumeAvg(),
				indicatorData.getMediumVolumeAvg(),
				indicatorData.getLongVolumeAvg())
		
	}

}
