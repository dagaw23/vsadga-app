package pl.com.vsadga.dto.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.BarType;

public class IndicatorData {
	/**
	 * ilość danych dla DOWN barów
	 */
	private int downBarVolLength;

	/**
	 * wolumen dla DOWN barów
	 */
	private Map<Integer, Integer> downBarVolMap;

	/**
	 * aktualna pozycja dla wolumenu DOWN bar
	 */
	private int downBarVolPos;

	/**
	 * ilość danych dla długiego terminu
	 */
	private int longTermLength;

	/**
	 * wolumen - dla N ostatnich barów (długi termin)
	 */
	private Map<Integer, Integer> longTermMap;

	/**
	 * aktualna pozycja w mapie średniego terminu
	 */
	private int longTermPos;

	/**
	 * ilość danych dla średniego terminu
	 */
	private int mediumTermLength;

	/**
	 * wolumen - dla N ostatnich barów (średni termin)
	 */
	private Map<Integer, Integer> mediumTermMap;

	/**
	 * aktualna pozycja w mapie średniego terminu
	 */
	private int mediumTermPos;

	/**
	 * ilość danych dla krótkiego terminu
	 */
	private int shortTermLength;

	/**
	 * pełna informacja - dla N ostatnich barów (krótki termin)
	 */
	private Map<Integer, BarStatsData> shortTermMap;

	/**
	 * aktualna pozycja w mapie krótkiego terminu
	 */
	private int shortTermPos;

	/**
	 * ilość danych dla UP barów
	 */
	private int upBarVolLength;

	/**
	 * wolumen dla UP barów
	 */
	private Map<Integer, Integer> upBarVolMap;

	/**
	 * aktualna pozycja dla wolumenu UP bar
	 */
	private int upBarVolPos;

	public IndicatorData(int shortTermLength, int mediumTermLength, int longTermLength) {
		super();
		this.shortTermLength = shortTermLength;
		this.mediumTermLength = mediumTermLength;
		this.longTermLength = longTermLength;
		this.upBarVolLength = 3;
		this.downBarVolLength = 3;

		cleanDataCache();
	}

	public void addBarData(BarData barData) {
		// utwórz obiekt krótkoterminowy:
		BarStatsData bar_stats_data = getBarStatsData(barData);

		addBarDataToCache(bar_stats_data);
	}

	public void addBarData(BarData barData, String trendIndy, Integer trendWeight, String volumeThermo) {
		// utwórz obiekt krótkoterminowy:
		BarStatsData bar_stats_data = getBarStatsData(barData, trendIndy, trendWeight, volumeThermo);

		addBarDataToCache(bar_stats_data);
	}

	/**
	 * Czyści wykorzystywane obiekty CACHE oraz wskaźniki pozycji w mapach.
	 */
	public void cleanDataCache() {
		this.shortTermMap = new HashMap<Integer, BarStatsData>();
		this.mediumTermMap = new HashMap<Integer, Integer>();
		this.longTermMap = new HashMap<Integer, Integer>();

		this.downBarVolMap = new HashMap<Integer, Integer>();
		this.upBarVolMap = new HashMap<Integer, Integer>();

		this.shortTermPos = 0;
		this.mediumTermPos = 0;
		this.longTermPos = 0;
		this.downBarVolPos = 0;
		this.upBarVolPos = 0;
	}
	
	public BigDecimal getShortVolumeAvg() {
		
		return;
	}
	
public BigDecimal getMediumVolumeAvg() {
		
		return;
	}

public BigDecimal getLongVolumeAvg() {
	
	return;
}

	public BigDecimal getDownBarAvgVolume(BarData barData) {
		int counter = 0;
		BigDecimal sum_val = new BigDecimal(0);

		for (Integer key : downBarVolMap.keySet()) {
			sum_val = sum_val.add(new BigDecimal(downBarVolMap.get(key)));
			counter++;
		}

		// jeśli aktualny jest DOWN bar: dodaj go jeszcze
		if (getActualBarType(barData) == BarType.DOWN_BAR) {
			sum_val.add(new BigDecimal(barData.getBarVolume()));
			counter++;
		}

		return sum_val.divide(new BigDecimal(counter), 4, RoundingMode.HALF_UP);
	}

	/**
	 * Pobiera ostatni bar, jaki został wpisany do mapy krótkoterminowej.
	 * 
	 * @return
	 */
	public BarStatsData getLastBarData() {
		// jeśli mapa jest pusta - zwróć NULL:
		if (shortTermMap.isEmpty())
			return null;

		return shortTermMap.get(shortTermPos);
	}

	/**
	 * Pobiera przedostatni bar, jaki został wpisany do mapy krótkoterminowej.
	 * 
	 * @return
	 */
	public BarStatsData getPreviousBar() {
		// jeśli mapa jeszcze nie ma 2 elementów - zwróć NULL:
		if (shortTermMap.size() < 2)
			return null;

		if (shortTermPos == 1)
			return shortTermMap.get(shortTermLength);
		else
			return shortTermMap.get(shortTermPos - 1);
	}

	public BigDecimal getUpBarAvgVolume(BarData barData) {
		int counter = 0;
		BigDecimal sum_val = new BigDecimal(0);

		for (Integer key : upBarVolMap.keySet()) {
			sum_val = sum_val.add(new BigDecimal(upBarVolMap.get(key)));
			counter++;
		}

		// jeśli aktualny jest UP bar: dodaj go jeszcze
		if (getActualBarType(barData) == BarType.UP_BAR) {
			sum_val = sum_val.add(new BigDecimal(barData.getBarVolume()));
			counter++;
		}

		return sum_val.divide(new BigDecimal(counter), 4, RoundingMode.HALF_UP);
	}

	public boolean isReadyShortTermData() {
		if (shortTermMap.size() < shortTermLength)
			return false;
		else
			return true;
	}

	public boolean isReadyVolumeThermoData() {
		if (upBarVolMap.size() == 0 || downBarVolMap.size() == 0)
			return false;
		else
			return true;
	}

	private void addBarDataToCache(BarStatsData barStatsData) {
		// przesuń aktualny wskaźnik w mapach:
		moveMapPositions();

		// dodaj bar na aktualną pozycję:
		shortTermMap.put(shortTermPos, barStatsData);
		mediumTermMap.put(mediumTermPos, barStatsData.getBarVolume());
		longTermMap.put(longTermPos, barStatsData.getBarVolume());

		// dane dla UP/DOWN barów:
		if (barStatsData.getBarType() == BarType.UP_BAR)
			upBarVolMap.put(upBarVolPos, barStatsData.getBarVolume());
		else if (barStatsData.getBarType() == BarType.DOWN_BAR)
			downBarVolMap.put(downBarVolPos, barStatsData.getBarVolume());
		// LEVEL bar jest pomijany
	}

	/**
	 * Pobiera ostatni bar zapisany do mapy i sprawdza aktualnie przetwarzany bar {@link BarData} -
	 * czy jest UP, DOWN czy LEVEL - w porównaniu z ostatnio zapisanym barem.
	 * 
	 * @param barData
	 * @return
	 */
	private BarType getActualBarType(BarData barData) {
		BarStatsData prev_bar = getLastBarData();

		// brak zapisanych jeszcze barów:
		if (prev_bar == null)
			return BarType.LEVEL_BAR;

		int comp_val = barData.getBarClose().compareTo(prev_bar.getBarClose());
		if (comp_val > 0)
			return BarType.UP_BAR;
		else if (comp_val < 0)
			return BarType.DOWN_BAR;
		else
			return BarType.LEVEL_BAR;
	}

	private BarStatsData getBarStatsData(BarData barData) {
		BarStatsData stats = getBaseBarStatsData(barData);

		stats.setTrendIndicator(barData.getTrendIndicator());
		stats.setTrendWeight(barData.getTrendWeight());
		stats.setVolumeThermometer(barData.getVolumeThermometer());

		return stats;
	}

	/**
	 * 
	 * @param barData
	 * @return
	 */
	private BarStatsData getBarStatsData(BarData barData, String trendIndicator, Integer trendWeight,
			String volumeThermo) {
		BarStatsData stats = getBaseBarStatsData(barData);

		stats.setTrendIndicator(trendIndicator);
		stats.setTrendWeight(trendWeight);
		stats.setVolumeThermometer(volumeThermo);

		return stats;
	}

	private BarStatsData getBaseBarStatsData(BarData barData) {
		BarStatsData stats = new BarStatsData();

		stats.setBarClose(barData.getBarClose());
		stats.setBarSpread(barData.getBarHigh().subtract(barData.getBarLow()));
		stats.setBarVolume(barData.getBarVolume());
		stats.setImaCount(barData.getImaCount());
		stats.setBarType(getActualBarType(barData));

		return stats;
	}

	/**
	 * Sprawdza, czy poszczególne wskaźniki pozycji w mapach osiągnęły maksymalną pozycję. Jeśli tak
	 * - przesuwa wskaźnik na początek kolekcji.
	 */
	private void moveMapPositions() {
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

		if (upBarVolPos == upBarVolLength)
			upBarVolPos = 1;
		else
			upBarVolPos += 1;

		if (downBarVolPos == downBarVolLength)
			downBarVolPos = 1;
		else
			downBarVolPos += 1;
	}

}
