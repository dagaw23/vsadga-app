package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarType;

public class DataCache {

	/**
	 * ilość barów wpisywanych do CACHE
	 */
	private int barDataCacheLength;

	/**
	 * pełna informacja - dla N ostatnich barów
	 */
	private Map<Integer, BarData> barDataCacheMap;

	/**
	 * aktualna pozycja w mapie z barami
	 */
	private int barDataCachePos;

	/**
	 * ilość danych do wyliczenia wskaźników
	 */
	private int indyDataLength;

	/**
	 * mapa z danymi do wyliczenia wskaźników
	 */
	private Map<Integer, IndicatorData> indyDataMap;

	/**
	 * aktualna pozycja w mapie z danymi dla wskaźników
	 */
	private int indyDataPos;

	public DataCache(int indyDataLength, int barDataCacheLength) {
		super();
		this.barDataCacheLength = barDataCacheLength;
		this.indyDataLength = indyDataLength;
		cleanDataCache();
	}

	public void addBarData(BarData barData) {
		addBarDataToCache(barData);
	}

	public void addBarDataWithIndy(BarData barData, IndicatorData indyData) {
		addBarDataToCache(barData);

		addIndicatorData(indyData);

	}

	public void addIndicatorData(IndicatorData indyData) {
		// przesuń aktualny wskaźnik w mapach:
		moveIndicatorPositions();

		indyDataMap.put(indyDataPos, indyData);
	}

	/**
	 * Zwraca średnią z wolumenu i spreadu - z podanego zakresu.
	 * 
	 * @param size
	 * @return
	 */
	public BigDecimal getVolumeSpreadAvg(int size) {
		if (size > indyDataLength)
			return null;
		
		BigDecimal temp = new BigDecimal(0);
		int pos = indyDataPos;
		
		for (int i=0; i < size; i++) {
			temp = temp.add(indyDataMap.get(pos).getBarSpreadVolume());
			
			if (pos == 1)
				pos = indyDataLength;
			else
				pos--;
		}
		
		return temp.divide(new BigDecimal(size), 5, RoundingMode.HALF_UP);
	}

	/**
	 * Czyści wykorzystywane obiekty CACHE oraz wskaźniki pozycji w mapach.
	 */
	public void cleanDataCache() {
		this.barDataCacheMap = new HashMap<Integer, BarData>();
		this.indyDataMap = new HashMap<Integer, IndicatorData>();

		this.barDataCachePos = 0;
		this.indyDataPos = 0;
	}

	public int getIndicatorCacheSize() {
		return indyDataMap.size();
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
		BarData proc_bar = null;
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
		BarData prev_bar = getLastBarData();

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
	public BarData getLastBarData() {
		// jeśli mapa jest pusta - zwróć NULL:
		if (barDataCacheMap.isEmpty())
			return null;

		return barDataCacheMap.get(barDataCachePos);
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
	public BarData getPreviousBar(int prevNr) {
		// czy mapa ma już elementy do pobrania:
		if (barDataCacheMap.size() <= prevNr)
			return null;

		if (barDataCachePos <= prevNr)
			return barDataCacheMap.get(barDataCacheLength + barDataCachePos - prevNr);
		else
			return barDataCacheMap.get(barDataCachePos - prevNr);
	}

	public boolean isVolumeLessThen2(BarData barData, BarData lastBar, BarData prevBar) {
		int bar_vol = barData.getBarVolume();

		if (bar_vol < lastBar.getBarVolume().intValue()
				&& lastBar.getBarVolume().intValue() < prevBar.getBarVolume().intValue())
			return true;
		else
			return false;
	}

	public boolean isReadyBarDataCache() {
		if (barDataCacheMap.size() < barDataCacheLength)
			return false;
		else
			return true;
	}

	public boolean isReadyIndyDataCache() {
		if (indyDataMap.size() < indyDataLength)
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
		if (barDataCacheMap.size() < barCount)
			return false;
		else
			return true;
	}

	private void addBarDataToCache(BarData barData) {
		// przesuń aktualny wskaźnik w mapach:
		moveCachePositions();

		// dodaj bar na aktualną pozycję:
		barDataCacheMap.put(barDataCachePos, barData);
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
	private void moveIndicatorPositions() {
		if (indyDataPos == indyDataLength)
			indyDataPos = 1;
		else
			indyDataPos += 1;
	}

}
