package pl.com.vsadga.dto.process;

import java.util.HashMap;
import java.util.Map;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;

public class IndicatorData {

	/**
	 * ilość danych dla długiego terminu
	 */
	private int longTermLength;

	/**
	 * mapa zawiera N ostatnich barów - tylko wolumen
	 */
	private Map<Integer, Integer> longTermMap;

	private int longTermPos;

	/**
	 * ilość danych dla średniego terminu
	 */
	private int mediumTermLength;

	/**
	 * mapa zawiera N ostatnich barów - tylko wolumen
	 */
	private Map<Integer, Integer> mediumTermMap;

	private int mediumTermPos;

	/**
	 * ilość danych dla krótkiego terminu
	 */
	private int shortTermLength;

	/**
	 * mapa zawiera N ostatnich barów - z pełną informacją
	 */
	private Map<Integer, BarStatsData> shortTermMap;

	private int shortTermPos;

	public IndicatorData(int shortTermLength, int mediumTermLength, int longTermLength) {
		super();
		this.shortTermLength = shortTermLength;
		this.mediumTermLength = mediumTermLength;
		this.longTermLength = longTermLength;

		this.shortTermMap = new HashMap<Integer, BarStatsData>();
		this.mediumTermMap = new HashMap<Integer, Integer>();
		this.longTermMap = new HashMap<Integer, Integer>();

		this.shortTermPos = 1;
		this.mediumTermPos = 1;
		this.longTermPos = 1;
	}

	public void addBarData(BarData barData) {
		// dodaj bar na aktualną pozycję:
		shortTermMap.put(shortTermPos, getBarStatsData(barData));
		mediumTermMap.put(mediumTermPos, barData.getBarVolume());
		longTermMap.put(longTermPos, barData.getBarVolume());

		// czy osiągnięty został ostatni element w mapie:
		if (shortTermPos == shortTermLength)
			shortTermPos = 1;
		else
			shortTermPos += 1;
		if (mediumTermPos == mediumTermLength)
			mediumTermPos = 1;
		else
			mediumTermPos += 1;
		if (longTermPos == longTermLength)
			longTermPos = 1;
		else
			longTermPos += 1;
	}

	public BarStatsData getPrevBar() {
		// jeśli mapa jest pusta - zwróć NULL:
		if (shortTermMap.isEmpty())
			return null;

		if (shortTermPos == 1)
			return shortTermMap.get(shortTermLength);
		else
			return shortTermMap.get(shortTermPos - 1);
	}

	public BarStatsData getPrevPrevBar() {
		if (shortTermPos == 1)
			return shortTermMap.get(shortTermLength - 1);
		else if (shortTermPos == 2)
			return shortTermMap.get(shortTermLength);
		else
			return shortTermMap.get(shortTermPos - 1);
	}

	public boolean isReadyShortTermData() {
		if (shortTermMap.size() < shortTermLength)
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
