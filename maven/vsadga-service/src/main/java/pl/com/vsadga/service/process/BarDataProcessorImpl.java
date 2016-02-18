package pl.com.vsadga.service.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dao.BarDataDao;
import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.service.BaseServiceException;

public class BarDataProcessorImpl implements BarDataProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BarDataProcessorImpl.class);

	private int actualMapPosition;

	private BarDataDao barDataDao;

	private Map<Integer, BarStatsData> barDataMap;

	/**
	 * poprzedni bar - czy oczekuje na potwierdzenie
	 */
	private boolean isBarToConfirmation;

	/**
	 * minimalna ilość informacji potrzebna do wskaźników
	 */
	private int minimumStatsSize;

	@Override
	public void processBarsData(List<BarData> barDataList, TimeFrame timeFrame) throws BaseServiceException {
		if (barDataList == null || barDataList.isEmpty()) {
			LOGGER.info("   [EMPTY] Lista barow [" + barDataList + "].");
			return;
		}

		barDataMap = new HashMap<Integer, BarStatsData>();
		actualMapPosition = 1;
		isBarToConfirmation = false;

		int bar_count = barDataList.size();
		BarData bar_data = null;

		// 3
		// 5
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
	 * @param minimumStatsSize
	 *            the minimumStatsSize to set
	 */
	public void setMinimumStatsSize(int minimumStatsSize) {
		this.minimumStatsSize = minimumStatsSize;
	}

	/**
	 * 
	 * @param barData
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData) {
		BarStatsData stats = new BarStatsData();

		stats.setBarClose(barData.getBarClose());
		stats.setBarSpread(barData.getBarHigh().subtract(barData.getBarLow()));
		stats.setBarVolume(barData.getBarVolume());
		stats.setImaCount(barData.getImaCount());
		stats.setTrend(barData.getTrendIndicator());

		return stats;
	}

	/**
	 * 
	 * @param barData
	 * @param trend
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData, String trend) {
		BarStatsData stats = new BarStatsData();

		stats.setBarClose(barData.getBarClose());
		stats.setBarSpread(barData.getBarHigh().subtract(barData.getBarLow()));
		stats.setBarVolume(barData.getBarVolume());
		stats.setImaCount(barData.getImaCount());
		stats.setTrend(trend);

		return stats;
	}

	private BarStatsData getPrevBar() {
		int prev_nr = 0;

		if (actualMapPosition == 1)
			prev_nr = minimumStatsSize;
		else
			prev_nr = actualMapPosition - 1;

		return barDataMap.get(prev_nr);
	}

	private String getTrendIndicator(BarData barData) {
		BarStatsData prev_bar = null;

		// pobierz informację o poprzenim barze:
		prev_bar = getPrevBar();

		int bar_compare = barData.getImaCount().compareTo(prev_bar.getImaCount());
		LOGGER.info("   [TREND] ACT [" + barData.getImaCount() + "] - PREV [" + prev_bar.getImaCount() + "] = "
				+ bar_compare + ".");

		if (bar_compare > 0) {
			if (prev_bar.getTrend().equals("U") || prev_bar.getTrend().equals("S"))
				return "U";
			else
				return "S";
		} else if (bar_compare < 0) {
			if (prev_bar.getTrend().equals("D") || prev_bar.getTrend().equals("S"))
				return "D";
			else
				return "S";
		} else {
			// takie same - pozostaw trend bez mian:
			return prev_bar.getTrend();
		}
	}

	private void processByPhase(BarData barData, String frameDesc) {
		String trend_ind = null;

		// jeśli brak barów statystycznych: aktualizuj tylko status i trend
		if (barDataMap.size() < minimumStatsSize) {
			LOGGER.info("   [STATS] Zbyt malo barow do analizy [" + barDataMap.size() + "-" + minimumStatsSize
					+ "].");

			// status 0: niekompletny, nie sprawdzamy trendu
			// status 1: wylicz trend
			// status 2,3: trend już wyliczony
			if (barData.getProcessPhase().intValue() == 1) {
				if (barDataMap.size() == 0)
					trend_ind = "S";
				else
					trend_ind = getTrendIndicator(barData);

				barDataDao.updateProcessPhaseWithTrend(barData.getId(), 3, trend_ind, frameDesc);

				// zapisz informację o barze do mapy:
				barDataMap.put(actualMapPosition, getBarStatsData(barData, trend_ind));
				actualMapPosition += 1;
			}

			// status 0: niekompletny, nie aktualizujemy już jego statusu
			// status 1, 2: aktualizuj status na 3
			// status 3: bez zmian
			if (barData.getProcessPhase().intValue() == 2) {
				barDataDao.updateProcessPhase(barData.getId(), 3, frameDesc);

				// zapisz informację o barze do mapy:
				barDataMap.put(actualMapPosition, getBarStatsData(barData, trend_ind));
				actualMapPosition += 1;
			}

			return;
		}

		// są już bary statystyczne - wyliczenie statystyk:
		// count TODO

		// *** status BAR: 0 lub 3 ***
		if (barData.getProcessPhase().intValue() == 0 || barData.getProcessPhase().intValue() == 3) {
			LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() + "] jest POMIJANY do wyliczen.");

			// dla statusu 3 - zapisanie do statystyk:
			if (barData.getProcessPhase().intValue() == 3) {
				barDataMap.put(actualMapPosition, getBarStatsData(barData));
				actualMapPosition += 1;
			}
		}

		// *** status BAR: 1 ***
		if (barData.getProcessPhase().intValue() == 1) {
			LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() + "] do wyliczenia.");

			// sprawdzenie trendu - tylko dla statusu 1
			// (0 - pomijamy, 2 - czeka na potwierdzenie, 3 - pomijamy)
			trend_ind = getTrendIndicator(barData);
			LOGGER.info("   - trend: [" + trend_ind + "].");

			// sprawdzenie, czy jakiś wskaźnik jest
			// TODO

			// aktualizacja bara w tabeli:
			// TODO w tej chwili do statusu 3 - ale oprzeć to na wskaźniku wyliczonym
			barDataDao.updateProcessPhaseWithTrend(barData.getId(), 3, trend_ind, frameDesc);
		}

		// czy poprzedni bar czeka na potwierdzenie:
		if (isBarToConfirmation) {
			// pobierz poprzedni bar
			// TODO

			// sprawdź jaki jest jego wskaźnik:

			// zweryfikuj czy aktualny wskaźnik jest
			// potwierdzeniem lub zaprzeczeniem
		}

		// *** status BAR: 2 ***
		if (barData.getProcessPhase().intValue() == 2) {
			LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() + "] do POTWIERDZENIA.");
			isBarToConfirmation = true;

			// zapisz go do statystyk:
			barDataMap.put(actualMapPosition, getBarStatsData(barData));
			actualMapPosition += 1;
		} else {
			isBarToConfirmation = false;
		}

	}

}
