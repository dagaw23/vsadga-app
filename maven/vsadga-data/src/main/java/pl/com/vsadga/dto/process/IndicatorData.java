package pl.com.vsadga.dto.process;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.BarType;

public class IndicatorData {

	/**
	 * suma wolumenów na DOWN bar (ostatnie 4 bary)
	 */
	// private int downBarVolumeSum;

	/**
	 * ilość barów wpisywanych do CACHE
	 */
	private int barDataCacheLength;

	/**
	 * pełna informacja - dla N ostatnich barów
	 */
	private Map<Integer, BarStatsData> barDataCacheMap;

	/**
	 * aktualna pozycja w mapie z barami
	 */
	private int barDataCachePos;

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
	private Map<Integer, Integer> shortTermMap;

	/**
	 * aktualna pozycja w mapie krótkiego terminu
	 */
	private int shortTermPos;

	/**
	 * ilość danych dla UP barów
	 */
	// private int upBarVolLength;

	/**
	 * suma wolumenów na UP bar (ostatnie 4 bary)
	 */
	// private int upBarVolumeSum;

	public IndicatorData(int shortTermLength, int mediumTermLength, int longTermLength, int barDataCacheLength) {
		super();
		this.shortTermLength = shortTermLength;
		this.mediumTermLength = mediumTermLength;
		this.longTermLength = longTermLength;
		this.barDataCacheLength = barDataCacheLength;
		cleanDataCache();
	}

	public void addBarData(BarData barData) {
		// utwórz obiekt krótkoterminowy:
		BarStatsData bar_stats_data = getBarStatsData(barData);

		addBarDataToCache(bar_stats_data);
	}

//	public void addBarData(BarData barData, String trendIndy, Integer trendWeight, String volumeThermo) {
//		// utwórz obiekt krótkoterminowy:
//		BarStatsData bar_stats_data = getBarStatsData(barData, trendIndy, trendWeight, volumeThermo);
//
//		addBarDataToCache(bar_stats_data);
//	}

	public void addVolumeData(Integer barVolume) {
		// przesuń aktualny wskaźnik w mapach:
		moveVolumePositions();

		// dodaj bar na aktualną pozycję:
		shortTermMap.put(shortTermPos, barVolume);
		mediumTermMap.put(mediumTermPos, barVolume);
		longTermMap.put(longTermPos, barVolume);
	}

	/**
	 * Czyści wykorzystywane obiekty CACHE oraz wskaźniki pozycji w mapach.
	 */
	public void cleanDataCache() {
		// bar:
		this.barDataCacheMap = new HashMap<Integer, BarStatsData>();
		// wolumen:
		this.shortTermMap = new HashMap<Integer, Integer>();
		this.mediumTermMap = new HashMap<Integer, Integer>();
		this.longTermMap = new HashMap<Integer, Integer>();

		// bar:
		this.barDataCachePos = 0;
		// wolumen:
		this.shortTermPos = 0;
		this.mediumTermPos = 0;
		this.longTermPos = 0;
	}

	/**
	 * Wylicza wolumen dla 4 ostatnich barów: 3 już przetworzonych do statusu 3 oraz aktualnego bara
	 * przetwarzanego ze statusu 1.
	 * 
	 * @param actualBar
	 *            przetwarzany bar ze statusem 1
	 * @return 1: wolumen UP, 0: wolumen LEVEL, -1: wolumen DOWN
	 */
	public int compareLastVolumeData(BarData actualBar) {
		BarStatsData proc_bar = null;
		int up_vol = 0;
		int down_vol = 0;

		// przetworzenie 3 barów:
		for (int i = 2; i >= 0; i--) {
			proc_bar = getPreviousBar(i);

			if (proc_bar.getBarType() == BarType.UP_BAR)
				up_vol += proc_bar.getBarVolume();
			else if ((proc_bar.getBarType() == BarType.DOWN_BAR))
				down_vol += proc_bar.getBarVolume();
		}

		// dodanie jeszcze aktualnie przetwarzanego bara:
		if (actualBar.getBarType() == BarType.UP_BAR)
			up_vol += actualBar.getBarVolume();
		else if ((actualBar.getBarType() == BarType.DOWN_BAR))
			down_vol += actualBar.getBarVolume();

		if (up_vol > down_vol)
			return 1;
		else if (up_vol < down_vol)
			return -1;
		else
			return 0;
	}

	/**
	 * Pobiera ostatni bar zapisany do mapy i sprawdza aktualnie przetwarzany bar {@link BarData} -
	 * czy jest UP, DOWN czy LEVEL - w porównaniu z ostatnio zapisanym barem.<br/>
	 * Jeśli brak jest poprzedniego bara w mapie - zwracany jest LEVEL bar.
	 * 
	 * @param barData
	 *            aktualnie przetwarzany bar
	 * @return UP, DOWN lub LEVEL bar
	 */
	public BarType getActualBarType(BarData barData) {
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

	/**
	 * Pobiera ostatni bar, jaki został wpisany do mapy krótkoterminowej.<br/>
	 * Jeśli mapa jeszcze jest pusta - zwraca wartość <code>NULL</code>.
	 * 
	 * @return ostatni bar, jaki został wpisany do mapy krótkoterminowej <br/>
	 *         lub wartość <code>NULL</code> w przypadku pustej mapy
	 */
	public BarStatsData getLastBarData() {
		// jeśli mapa jest pusta - zwróć NULL:
		if (barDataCacheMap.isEmpty())
			return null;

		return barDataCacheMap.get(barDataCachePos);
	}

	public BigDecimal getLongVolumeAvg() {
		BigDecimal result = new BigDecimal(0);
		result.setScale(4);
		int vol_sum = 0;

		if (longTermMap.size() < longTermLength)
			return new BigDecimal(0);
		else {
			for (Integer key : longTermMap.keySet())
				vol_sum += longTermMap.get(key);
		}

		result = new BigDecimal(vol_sum);
		return result.divide(new BigDecimal(longTermLength));
	}

	public BigDecimal getMediumVolumeAvg() {
		BigDecimal result = new BigDecimal(0);
		result.setScale(4);
		int vol_sum = 0;

		if (mediumTermMap.size() < mediumTermLength)
			return new BigDecimal(0);
		else {
			for (Integer key : mediumTermMap.keySet())
				vol_sum += mediumTermMap.get(key);
		}

		result = new BigDecimal(vol_sum);
		return result.divide(new BigDecimal(mediumTermLength));
	}

	/**
	 * Pobiera pojedynczy bar z mapy krótkoterminowej, który jest wstecz N barów w porównaniu z
	 * ostatnim barem.<br/>
	 * 
	 * 
	 * @param prevNr
	 *            liczba barów wstecz, jaki jest pobierany, <br/>
	 *            np.: 0: ostatnio wpisany bar do mapy, 1: czyli 2 bary wstecz, 2: czyli 3 bary
	 *            wstecz, itd
	 * @return pojedynczy bar z mapy krótkoterminowej <br/>
	 *         lub wartość <code>NULL</code> w przypadku mapy nie posiadającej odpowiednią liczbę
	 *         elementów
	 */
	public BarStatsData getPreviousBar(int prevNr) {
		// czy mapa ma już elementy do pobrania:
		if (barDataCacheMap.size() <= prevNr)
			return null;

		if (shortTermPos <= prevNr)
			return barDataCacheMap.get(barDataCacheLength + barDataCachePos - prevNr);
		else
			return barDataCacheMap.get(barDataCachePos - prevNr);
	}

	public BigDecimal getShortVolumeAvg() {
		BigDecimal result = new BigDecimal(0);
		result.setScale(4);
		int vol_sum = 0;

		if (shortTermMap.size() < shortTermLength)
			return new BigDecimal(0);
		else {
			for (Integer key : shortTermMap.keySet())
				vol_sum += shortTermMap.get(key);
		}

		result = new BigDecimal(vol_sum);
		return result.divide(new BigDecimal(shortTermLength));
	}

	public boolean isReadyShortTermData() {
		if (shortTermMap.size() < shortTermLength)
			return false;
		else
			return true;
	}

	/**
	 * Sprawdza, czy do mapy barów w krótkim terminie - już zostało wpisanych N barów, jaka została
	 * podana w parametrze metody.
	 * 
	 * @param barCount
	 *            ile barów wymagamy, aby zostało już wpisane
	 * @return
	 */
	public boolean isWritedShortTermBars(int barCount) {
		if (shortTermMap.size() < barCount)
			return false;
		else
			return true;
	}

	private void addBarDataToCache(BarStatsData barStatsData) {
		// przesuń aktualny wskaźnik w mapach:
		moveCachePositions();

		// dodaj bar na aktualną pozycję:
		barDataCacheMap.put(barDataCachePos, barStatsData);
	}

	private BarStatsData getBarStatsData(BarData barData) {
		BarStatsData stats = new BarStatsData();

		stats.setBarClose(barData.getBarClose());
		stats.setBarSpread(barData.getBarHigh().subtract(barData.getBarLow()));
		stats.setBarVolume(barData.getBarVolume());
		stats.setImaCount(barData.getImaCount());
		stats.setBarType(barData.getBarType());

		stats.setTrendIndicator(barData.getTrendIndicator());
		stats.setTrendWeight(barData.getTrendWeight());
		stats.setVolumeThermometer(barData.getVolumeThermometer());

		return stats;
	}


	/**
	 * Sprawdza, czy poszczególne wskaźniki pozycji w mapach osiągnęły maksymalną pozycję. Jeśli tak
	 * - przesuwa wskaźnik na początek kolekcji.
	 */
	private void moveCachePositions() {
		if (barDataCachePos == barDataCacheLength)
			barDataCachePos = 1;
		else
			barDataCachePos += 1;
	}

	/**
	 * Sprawdza, czy poszczególne wskaźniki pozycji w mapach osiągnęły maksymalną pozycję. Jeśli tak
	 * - przesuwa wskaźnik na początek kolekcji.
	 */
	private void moveVolumePositions() {
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

}
