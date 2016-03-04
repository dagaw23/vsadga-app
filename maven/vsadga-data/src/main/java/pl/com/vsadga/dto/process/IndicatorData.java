package pl.com.vsadga.dto.process;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;

public class IndicatorData {

	private int actualMapPosition;

	/**
	 * mapa zawiera N ostatnich barów
	 */
	private Map<Integer, BarStatsData> barDataMap;

	private IndicatorBarData downBarIndicator;

	/**
	 * minimalna ilość informacji potrzebna do wskaźników
	 */
	private int minimumStatsSize;

	private IndicatorBarData upBarIndicator;

	public IndicatorData(int minimumStatsSize) {
		super();
		this.minimumStatsSize = minimumStatsSize;
		this.barDataMap = new HashMap<Integer, BarStatsData>();
		this.actualMapPosition = 1;
		this.downBarIndicator = new IndicatorBarData();
		this.upBarIndicator = new IndicatorBarData();
	}

	public void addBarData(BarData barData) {
		// dodaj bar na aktualną pozycję:
		barDataMap.put(actualMapPosition, getBarStatsData(barData));

		// czy osiągnięty został ostatni element w mapie:
		if (actualMapPosition == minimumStatsSize)
			actualMapPosition = 1;
		else
			actualMapPosition += 1;
	}

	public void addBarData(BarData barData, Boolean isBarToConfirmation) {
		// dodaj bar na aktualną pozycję:
		barDataMap.put(actualMapPosition, getBarStatsData(barData, isBarToConfirmation));

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

	public BigDecimal getSpreadAvg() {
		BigDecimal result = new BigDecimal(0);
		Set<Integer> keys = barDataMap.keySet();

		for (Integer key : keys)
			result = result.add(barDataMap.get(key).getBarSpread());

		return result.divide(new BigDecimal(minimumStatsSize));
	}

	public boolean isReadyIndicatorMap() {
		if (barDataMap.size() < minimumStatsSize)
			return false;
		else
			return true;

	}

	private void addUpOrDownBar(BarData barData) {
		BarStatsData prev_bar = getPrevBar();

		if (prev_bar == null)
			return;

		int comp_val = barData.getBarClose().compareTo(prev_bar.getBarClose());

		if (comp_val < 0)
			downBarIndicator.addVolume(barData.getBarVolume());
		else if (comp_val > 0)
			upBarIndicator.addVolume(barData.getBarVolume());

	}

	/**
	 * 
	 * @param barData
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData) {
		BarStatsData stats = getBaseBarStatsData(barData);

		return stats;
	}

	private BarStatsData getBarStatsData(BarData barData, Boolean barToConfirmation) {
		BarStatsData stats = getBaseBarStatsData(barData);

		stats.setBarToConfirmation(barToConfirmation);
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
}
