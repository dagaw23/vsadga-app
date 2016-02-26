package pl.com.vsadga.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pl.com.vsadga.data.BarData;

public class IndicatorData {

	private int actualMapPosition;

	private Map<Integer, BarStatsData> barDataMap;

	/**
	 * minimalna ilość informacji potrzebna do wskaźników
	 */
	private int minimumStatsSize;
	
	public IndicatorData(int minimumStatsSize) {
		super();
		this.minimumStatsSize = minimumStatsSize;
		this.barDataMap = new HashMap<Integer, BarStatsData>();
		this.actualMapPosition = 1;
	}
	
	public void addBarData2Map(BarData barData) {
		// dodaj bar na aktualną pozycję:
		barDataMap.put(actualMapPosition, getBarStatsData(barData));

		// czy osiągnięty został ostatni element w mapie:
		if (actualMapPosition == minimumStatsSize)
			actualMapPosition = 1;
		else
			actualMapPosition += 1;
	}
	
	public void addBarData2Map(BarData barData, Boolean isBarToConfirmation) {
		// dodaj bar na aktualną pozycję:
		barDataMap.put(actualMapPosition, getBarStatsData(barData, isBarToConfirmation));

		// czy osiągnięty został ostatni element w mapie:
		if (actualMapPosition == minimumStatsSize)
			actualMapPosition = 1;
		else
			actualMapPosition += 1;
	}
	
	public BigDecimal getSpreadAvg() {
		BigDecimal result = new BigDecimal(0);
		Set<Integer> keys = barDataMap.keySet();
		
		for (Integer key : keys)
			result = result.add(barDataMap.get(key).getBarSpread());
		
		return result.divide(new BigDecimal(minimumStatsSize));
	}

	public void addBarData2Map(BarData barData, TrendParams trendParams) {
		// dodaj bar na aktualną pozycję:
		barDataMap.put(actualMapPosition, getBarStatsData(barData, trendParams));

		// czy osiągnięty został ostatni element w mapie:
		if (actualMapPosition == minimumStatsSize)
			actualMapPosition = 1;
		else
			actualMapPosition += 1;
	}

	public BarStatsData getPrevBar() {
		int prev_nr = 0;
		
		// jeśli mapa jest pusta - zwróć NULL:
		if (barDataMap.isEmpty())
			return null;

		if (actualMapPosition == 1)
			prev_nr = minimumStatsSize;
		else
			prev_nr = actualMapPosition - 1;

		return barDataMap.get(prev_nr);
	}

	public BarStatsData getPrevPrevBar() {
		int prev_prev_nr = 0;

		if (actualMapPosition == 1)
			prev_prev_nr = minimumStatsSize - 1;
		else if (actualMapPosition == 2)
			prev_prev_nr = minimumStatsSize;
		else
			prev_prev_nr = actualMapPosition - 1;

		return barDataMap.get(prev_prev_nr);
	}

	public boolean isReadyIndicatorMap() {
		if (barDataMap.size() < minimumStatsSize)
			return false;
		else
			return true;
		
	}

	/**
	 * 
	 * @param barData
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData) {
		BarStatsData stats = getBaseBarStatsData(barData);

		stats.setTrendIndicator(barData.getTrendIndicator());
		stats.setTrendWeight(barData.getTrendWeight());

		return stats;
	}
	
	private BarStatsData getBarStatsData(BarData barData, Boolean barToConfirmation) {
		BarStatsData stats = getBaseBarStatsData(barData);

		stats.setBarToConfirmation(barToConfirmation);
		stats.setTrendIndicator(barData.getTrendIndicator());
		stats.setTrendWeight(barData.getTrendWeight());

		return stats;
	}
	
	private BarStatsData getBaseBarStatsData(BarData barData) {
		BarStatsData stats = new BarStatsData();

		stats.setBarClose(barData.getBarClose());
		stats.setBarSpread(barData.getBarHigh().subtract(barData.getBarLow()));
		stats.setBarVolume(barData.getBarVolume());
		stats.setImaCount(barData.getImaCount());

		return stats;
	}

	/**
	 * 
	 * @param barData
	 * @param trend
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData, TrendParams trendParams) {
		BarStatsData stats = getBaseBarStatsData(barData);

		stats.setTrendIndicator(trendParams.getTrendIndicator());
		stats.setTrendWeight(trendParams.getTrendWeight());

		return stats;
	}

}
