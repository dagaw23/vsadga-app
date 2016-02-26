package pl.com.vsadga.service.process.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.TrendParams;
import pl.com.vsadga.dto.VolumeThermometer;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.BarDataProcessor;
import pl.com.vsadga.service.process.IndicatorProcessor;
import pl.com.vsadga.service.process.TrendProcessor;

public class BarDataProcessorImpl implements BarDataProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BarDataProcessorImpl.class);

	private BarDataDao barDataDao;

	private IndicatorProcessor indicatorProcessor;

	private TrendProcessor trendProcessor;
	
	@Override
	public void processBarsData(List<BarData> barDataList, TimeFrame timeFrame) throws BaseServiceException {
		if (barDataList == null || barDataList.isEmpty()) {
			LOGGER.info("   [EMPTY] Lista barow [" + barDataList + "].");
			return;
		}

		int bar_count = barDataList.size();
		BarData bar_data = null;
		trendProcessor.clearVolumeData();

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
	 * Przetwarza pojedynczy bar - w porównaniu z poprzednim barem.
	 * 
	 * @param barData
	 * @param frameDesc
	 * @throws BaseServiceException
	 */
	private void processByPhase(BarData barData, String frameDesc) throws BaseServiceException {
		int bar_phase = barData.getProcessPhase().intValue();
		TrendParams trend_pars = null;
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
			trend_pars = trendProcessor.getActualTrend(barData);

			// sprawdzenie wskaźnika:
			ind_info = indicatorProcessor.getDataIndicator(barData, frameDesc);
			
			// nie został przetworzony wskaźnik: od razu do statusu 3
			if (!ind_info.isProcessIndy()) {
				LOGGER.info("   [BAR] Nie przetworzony jeszcze.");

				barDataDao.updateProcessPhaseWithTrend(barData.getId(), 3, trend_pars.getTrendIndicator(),
						trend_pars.getTrendWeight(), frameDesc);
			}

			// aktualizacja bara w tabeli:
			// TODO w tej chwili do statusu 3 - ale oprzeć to na wskaźniku wyliczonym
			barDataDao.updateProcessPhaseWithTrend(barData.getId(), 3, trend_pars.getTrendIndicator(),
					trend_pars.getTrendWeight(), frameDesc);
		}

		// *** status BAR: 2 ***
		if (bar_phase == 2) {
			// LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() +
			// "] do POTWIERDZENIA.");

			indicatorProcessor.addIndicatorData(barData, true);
		}

		// *** status BAR: 3 ***
		if (bar_phase == 3) {
			// LOGGER.info("   [STATS] Bar wg statusu [" + bar_phase + "] juz ZAKONCZONY.");
			indicatorProcessor.addIndicatorData(barData);
		}

	}
	
	private BarType getBarType(BarData barData) {
		
	}

}
