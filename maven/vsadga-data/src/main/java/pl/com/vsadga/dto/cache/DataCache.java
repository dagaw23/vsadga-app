package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarType;

public class DataCache {
	/**
	 * pełna informacja - dla 2 ostatnich barów
	 */
	private Map<Integer, BarData> barDataCacheMap;

	/**
	 * lista z danymi do wyliczenia wolumenu i spreadu
	 */
	private List<IndicatorData> indyDataCache;

	/**
	 * oczekiwany rozmiar CACHE dla wolumenu i spreadu
	 */
	private Integer indyDataCacheSize;

	public DataCache(int indyCacheSize) {
		super();
		this.indyDataCacheSize = indyCacheSize;
		cleanDataCache();
	}

	/**
	 * Czyści wykorzystywane obiekty CACHE oraz wskaźniki pozycji w mapach.
	 */
	public void cleanDataCache() {
		this.barDataCacheMap = new HashMap<Integer, BarData>();
		this.indyDataCache = new ArrayList<IndicatorData>();
	}

	public void fillBarDataCache(List<BarData> barDataList) {
		// brak barów wcześniejszych: pusty CACHE
		if (barDataList.isEmpty()) {
			cleanBarDataCache();
			return;
		}

		int i = 1;

		for (BarData bar_data : barDataList)
			barDataCacheMap.put(i++, bar_data);
	}

	public void fillIndyDataCache(List<BarData> barDataList, BarData barData) {
		// brak barów wcześniejszych: pusty CACHE
		if (barDataList.isEmpty() || barDataList.size() < 11) {
			cleanIndyDataCache();
			return;
		}

		// aktualny bar:
		indyDataCache.add(convert(barData));

		for (BarData bar_data : barDataList)
			indyDataCache.add(convert(bar_data));
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
		BarData prev_bar = getPrevBarData(1);

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

	public int getIndicatorCacheSize() {
		return indyDataCache.size();
	}

	/**
	 * Pobiera bar, jaki został wpisany do mapy krótkoterminowej.<br/>
	 * Jeśli mapa jeszcze jest pusta lub nie ma takiego elementu w CACHE - zwraca wartość
	 * <code>NULL</code>.
	 * 
	 * @param key
	 *            który element pobieramy z CACHE: 1 - pierwszy element z CACHE wg czasu, 2 - drugi
	 *            element z CACHE wg czasu
	 * @return żądany bar, jaki został wpisany do mapy krótkoterminowej <br/>
	 *         lub wartość <code>NULL</code> w przypadku pustej mapy
	 */
	public BarData getPrevBarData(int key) {
		// jeśli mapa jest pusta - zwróć NULL:
		if (barDataCacheMap.isEmpty())
			return null;

		return barDataCacheMap.get(key);
	}

	public TreeSet<IndicatorData> getSortedCacheBySpread() {
		return new TreeSet<IndicatorData>(new Comparator<IndicatorData>() {

			@Override
			public int compare(IndicatorData data1, IndicatorData data2) {
				return data1.getBarSpread().compareTo(data2.getBarSpread());
			}

		});
	}

	public TreeSet<IndicatorData> getSortedCacheByVolume() {
		return new TreeSet<IndicatorData>(new Comparator<IndicatorData>() {

			@Override
			public int compare(IndicatorData data1, IndicatorData data2) {
				if (data1.getBarVolume().intValue() < data2.getBarVolume().intValue())
					return -1;
				else if (data1.getBarVolume().intValue() > data2.getBarVolume().intValue())
					return 1;
				else
					return 0;
			}

		});
	}

	public boolean isReadyBarDataCache() {
		if (barDataCacheMap.size() < 2)
			return false;
		else
			return true;
	}

	public boolean isReadyIndyDataCache() {
		if (indyDataCache.size() < indyDataCacheSize)
			return false;
		else
			return true;
	}

	public boolean isVolumeLessThen2(BarData barData, BarData lastBar, BarData prevBar) {
		int bar_vol = barData.getBarVolume();

		if (bar_vol < lastBar.getBarVolume().intValue()
				&& lastBar.getBarVolume().intValue() < prevBar.getBarVolume().intValue())
			return true;
		else
			return false;
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

	private void cleanBarDataCache() {
		this.barDataCacheMap = new HashMap<Integer, BarData>();
	}

	private void cleanIndyDataCache() {
		this.indyDataCache = new ArrayList<IndicatorData>();
	}

	// return temp.divide(new BigDecimal(size), 5, RoundingMode.HALF_UP);
	private IndicatorData convert(BarData barData) {
		BigDecimal bar_spr = barData.getBarHigh().subtract(barData.getBarLow());
		bar_spr.setScale(5, RoundingMode.HALF_UP);

		return new IndicatorData(barData.getBarVolume(), bar_spr, barData.getId());
	}

}
