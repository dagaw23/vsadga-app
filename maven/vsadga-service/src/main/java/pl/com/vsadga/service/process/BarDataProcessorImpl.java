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
import pl.com.vsadga.dto.TrendParams;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.utils.DateConverter;

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
		stats.setTrendIndicator(barData.getTrendIndicator());
		stats.setTrendWeight(barData.getTrendWeight());

		return stats;
	}

	/**
	 * 
	 * @param barData
	 * @param trend
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData, TrendParams trendParams) {
		BarStatsData stats = new BarStatsData();

		stats.setBarClose(barData.getBarClose());
		stats.setBarSpread(barData.getBarHigh().subtract(barData.getBarLow()));
		stats.setBarVolume(barData.getBarVolume());
		stats.setImaCount(barData.getImaCount());
		stats.setTrendIndicator(trendParams.getTrendIndicator());
		stats.setTrendWeight(trendParams.getTrendWeight());

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

	private TrendParams getTrendParams(BarData barData) {
		BarStatsData prev_bar = null;

		// pobierz informację o poprzednim barze:
		prev_bar = getPrevBar();

		// porównaj AKTUALNY bar - z poprzednim (średnia krocząca):
		int bar_compare = barData.getImaCount().compareTo(prev_bar.getImaCount());
		LOGGER.info("   [TREND] ACT [" + barData.getImaCount() + "]:"
				+ DateConverter.dateToString(barData.getBarTime(), "yy/MM/dd HH:mm") + ", PREV ["
				+ prev_bar.getImaCount() + "], wynik=" + bar_compare + ".");

		// trend w poprzednim barze:
		String trend = prev_bar.getTrendIndicator();
		int weight = prev_bar.getTrendWeight().intValue();

		// *** poprzedni bar to UPTREND ***
		if (trend.equals("U")) {
			if (bar_compare > 0) {
				// kontynuacja UP
				if (weight == 6 || weight == 7)
					return new TrendParams("U", (weight + 1));
				else
					return new TrendParams("U", weight);
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return new TrendParams(trend, weight);
			} else {
				// zmiana na DOWN
				if (weight == 6)
					return new TrendParams("S", 1);
				else
					return new TrendParams("U", 6);
			}
		}
		// *** poprzedni bar to DOWNTREND ***
		else if (trend.equals("D")) {
			if (bar_compare < 0) {
				// kontynuacja DOWN
				if (weight == 1 || weight == 2)
					return new TrendParams("D", (weight + 1));
				else
					return new TrendParams("D", weight);
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return new TrendParams(trend, weight);
			} else {
				// zmiana na UP
				if (weight == 1)
					return new TrendParams("S", 6);
				else
					return new TrendParams("D", 6);
			}
		}
		// *** poprzedni bar jest BOCZNY ***
		else if (trend.equals("S")) {
			if (bar_compare > 0) {
				// bar UP
				if (weight == 6)
					return new TrendParams("S", 7);
				else if (weight == 7)
					return new TrendParams("U", 8);
				else if (weight == 0 || weight == 1 || weight == 2)
					return new TrendParams("S", 6);
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					return new TrendParams("S", 0);
				}

			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return new TrendParams(trend, weight);
			} else {
				// bar DOWN
				if (weight == 1)
					return new TrendParams("S", 2);
				else if (weight == 2)
					return new TrendParams("D", 3);
				else if (weight == 0 || weight == 6 || weight == 7)
					return new TrendParams("S", 1);
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					return new TrendParams("S", 0);
				}
			}
		} else {
			LOGGER.info("   [ERROR] Unexpected situation [" + trend + "," + weight + "," + bar_compare
					+ "], aktualny bar [" + barData + "].");
			return new TrendParams("S", 0);
		}
	}

	/**
	 * Przetwarza pojedynczy bar - w porównaniu z poprzednim barem.
	 * 
	 * @param barData
	 * @param frameDesc
	 */
	private void processByPhase(BarData barData, String frameDesc) {
		int bar_phase = barData.getProcessPhase().intValue();

		// jeśli brak barów statystycznych: aktualizuj tylko status i trend
		if (barDataMap.size() < minimumStatsSize) {
			LOGGER.info("   [STATS] Zbyt malo barow do analizy [" + barDataMap.size() + "-" + minimumStatsSize
					+ "].");
			TrendParams params = null;

			// status 0: niekompletny, nie sprawdzamy trendu
			// status 1: wylicz trend
			// status 2,3: trend już wyliczony
			if (bar_phase == 1) {
				if (barDataMap.size() == 0)
					params = new TrendParams("S", 0);
				else
					params = getTrendParams(barData);

				barDataDao.updateProcessPhaseWithTrend(barData.getId(), 3, params.getTrendIndicator(),
						params.getTrendWeight(), frameDesc);

				// zapisz informację o barze do mapy:
				barDataMap.put(actualMapPosition, getBarStatsData(barData, params));
				actualMapPosition += 1;
			}

			// aktualizacja tylko statusu bara:
			if (bar_phase == 2)
				barDataDao.updateProcessPhase(barData.getId(), 3, frameDesc);

			// zapisz informację o barze do mapy:
			if (bar_phase == 2 || bar_phase == 3) {
				barDataMap.put(actualMapPosition, getBarStatsData(barData));
				actualMapPosition += 1;
			}

			return;
		}

		// TODO są już bary statystyczne - wyliczenie statystyk:

		// *** status BAR: 0 ***
		if (bar_phase == 0) {
			LOGGER.info("   [STATS] Bar wg statusu [" + bar_phase + "] jeszcze NIE JEST ZAKONCZONY.");
			return;
		}

		// *** status BAR: 3 ***
		if (bar_phase == 3) {
			LOGGER.info("   [STATS] Bar wg statusu [" + bar_phase + "] juz ZAKONCZONY.");

			barDataMap.put(actualMapPosition, getBarStatsData(barData));
			actualMapPosition += 1;
		}

		// *** status BAR: 1 ***
		if (bar_phase == 1) {
			LOGGER.info("   [STATS] Bar ze statusem [" + bar_phase + "] - KOMPLETNE wyliczanie.");

			// sprawdzenie trendu - tylko dla statusu 1
			// (0 - jeszcze nie zakończony, 2 - czeka na potwierdzenie, 3 - już zakończony)
			TrendParams params = getTrendParams(barData);
			LOGGER.info("   - trend: [" + params.getTrendIndicator() + "," + params.getTrendWeight() + "].");

			// sprawdzenie, czy jakiś wskaźnik jest
			// TODO

			// aktualizacja bara w tabeli:
			// TODO w tej chwili do statusu 3 - ale oprzeć to na wskaźniku wyliczonym
			barDataDao.updateProcessPhaseWithTrend(barData.getId(), 3, params.getTrendIndicator(),
					params.getTrendWeight(), frameDesc);

			// zapisz informację o barze do mapy:
			barDataMap.put(actualMapPosition, getBarStatsData(barData, params));
			actualMapPosition += 1;
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
		if (bar_phase == 2) {
			LOGGER.info("   [STATS] Bar wg statusu [" + barData.getProcessPhase() + "] do POTWIERDZENIA.");
			isBarToConfirmation = true;

			// zapisz informację o barze do mapy:
			barDataMap.put(actualMapPosition, getBarStatsData(barData));
			actualMapPosition += 1;
		} else {
			isBarToConfirmation = false;
		}

	}

}
