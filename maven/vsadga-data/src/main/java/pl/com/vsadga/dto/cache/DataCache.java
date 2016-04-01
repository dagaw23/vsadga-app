package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.BarType;

public class DataCache {

	private class SpreadDescComparator implements Comparator<IndicatorData> {
		@Override
		public int compare(IndicatorData data1, IndicatorData data2) {
			if (data1.getBarSpread().compareTo(data2.getBarSpread()) < 0)
				return 1;
			else if (data1.getBarSpread().compareTo(data2.getBarSpread()) > 0)
				return -1;
			else
				return 1;
		}
	}

	private class VolumeDescComparator implements Comparator<IndicatorData> {

		@Override
		public int compare(IndicatorData data1, IndicatorData data2) {
			if (data1.getBarVolume().intValue() < data2.getBarVolume().intValue())
				return 1;
			else if (data1.getBarVolume().intValue() > data2.getBarVolume().intValue())
				return -1;
			else
				return 1;
		}

	}

	public static void main(String[] args) {
		DataCache cache = new DataCache(14, 10);

		cache.addIndicatorData(100, BigDecimal.valueOf(1.12), BigDecimal.valueOf(1.11));
		cache.addIndicatorData(10, BigDecimal.valueOf(1.15), BigDecimal.valueOf(1.11));
		cache.addIndicatorData(500, BigDecimal.valueOf(1.20), BigDecimal.valueOf(1.18));
		cache.addIndicatorData(1000, BigDecimal.valueOf(1.30), BigDecimal.valueOf(1.01));
		cache.addIndicatorData(212, BigDecimal.valueOf(1.40), BigDecimal.valueOf(1.18));
		cache.addIndicatorData(212, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.21));
		cache.addIndicatorData(213, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(312, BigDecimal.valueOf(1.90), BigDecimal.valueOf(1.52));
		cache.addIndicatorData(3000, BigDecimal.valueOf(1.86), BigDecimal.valueOf(1.61));
		cache.addIndicatorData(3400, BigDecimal.valueOf(1.99), BigDecimal.valueOf(1.88));
		cache.addIndicatorData(3401, BigDecimal.valueOf(2.10), BigDecimal.valueOf(1.90));
		cache.addIndicatorData(9890, BigDecimal.valueOf(2.22), BigDecimal.valueOf(2.10));
		cache.addIndicatorData(890, BigDecimal.valueOf(2.44), BigDecimal.valueOf(2.02));
		cache.addIndicatorData(21, BigDecimal.valueOf(2.55), BigDecimal.valueOf(2.01));

		// System.out.println(cache.getVolumeSize(9));
		// System.out.println(cache.getSpreadSize(BigDecimal.valueOf(1.52),
		// BigDecimal.valueOf(1.02)));
	}

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
		// utwórz obiekt krótkoterminowy:
		BarStatsData bar_stats_data = getBarStatsData(barData);
		addBarDataToCache(bar_stats_data);
	}
	
	public void addBarDataWithIndy(BarData barData) {
		// utwórz obiekt krótkoterminowy:
		BarStatsData bar_stats_data = getBarStatsData(barData);
		addBarDataToCache(bar_stats_data);
		
		addIndicatorData(barData);
	}

	public void addIndicatorData(BarData barData) {
		addIndicatorData(barData.getBarVolume(), barData.getBarHigh(), barData.getBarLow());
	}

	public void addIndicatorData(Integer barVolume, BigDecimal barHigh, BigDecimal barLow) {
		// przesuń aktualny wskaźnik w mapach:
		moveIndicatorPositions();

		indyDataMap.put(indyDataPos, new IndicatorData(barVolume, barHigh, barLow));
	}

	/**
	 * Czyści wykorzystywane obiekty CACHE oraz wskaźniki pozycji w mapach.
	 */
	public void cleanDataCache() {
		this.barDataCacheMap = new HashMap<Integer, BarStatsData>();
		this.indyDataMap = new HashMap<Integer, IndicatorData>();

		this.barDataCachePos = 0;
		this.indyDataPos = 0;
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

		if (barDataCachePos <= prevNr)
			return barDataCacheMap.get(barDataCacheLength + barDataCachePos - prevNr);
		else
			return barDataCacheMap.get(barDataCachePos - prevNr);
	}

	public String getSpreadSize(BigDecimal barHigh, BigDecimal barLow) {
		// czy mapa jest już wypełniona w pełni:
		if (indyDataMap.size() < indyDataLength)
			return "N";

		// posortuj wg wolumenu:
		Set<IndicatorData> spr_set = new TreeSet<IndicatorData>(new SpreadDescComparator());
		spr_set.addAll(indyDataMap.values());

		// spread aktualnego bara:
		BigDecimal act_spread = barHigh.subtract(barLow);

		int i = 0;
		BigDecimal hi_sum = new BigDecimal(0);
		BigDecimal hi_spr = null;
		BigDecimal avg_sum = new BigDecimal(0);
		BigDecimal avg_spr = null;
		BigDecimal lo_sum = new BigDecimal(0);
		BigDecimal lo_spr = null;
		BigDecimal vl_sum = new BigDecimal(0);
		BigDecimal vl_spr = null;

		for (IndicatorData data : spr_set) {

			if (i < 4)
				hi_sum = hi_sum.add(data.getBarSpread());

			if (i > 2 && i < 7)
				avg_sum = avg_sum.add(data.getBarSpread());

			if (i > 6 && i < 11)
				lo_sum = lo_sum.add(data.getBarSpread());

			if (i > 9)
				vl_sum = vl_sum.add(data.getBarSpread());

			i++;
		}

		hi_spr = hi_sum.divide(new BigDecimal(4), 6, RoundingMode.HALF_UP);
		avg_spr = avg_sum.divide(new BigDecimal(4), 6, RoundingMode.HALF_UP);
		lo_spr = lo_sum.divide(new BigDecimal(4), 6, RoundingMode.HALF_UP);
		vl_spr = vl_sum.divide(new BigDecimal(4), 6, RoundingMode.HALF_UP);

		// for (IndicatorData data : spr_set)
		// System.out.println("   >" + data.getBarSpread());
		// System.out.println(hi_spr);
		// System.out.println(avg_spr);
		// System.out.println(lo_spr);
		// System.out.println(vl_spr);

		if (act_spread.compareTo(hi_spr) > 0)
			return "VH";
		else if (act_spread.compareTo(avg_spr) > 0)
			return "Hi";
		else if (act_spread.compareTo(lo_spr) > 0)
			return "AV";
		else if (act_spread.compareTo(vl_spr) > 0)
			return "Lo";
		else
			return "VL";
	}

	public String getVolumeSize(Integer barVolume) {
		// czy mapa jest już wypełniona w pełni:
		if (indyDataMap.size() < indyDataLength)
			return "N";

		// posortuj wg wolumenu:
		Set<IndicatorData> vol_set = new TreeSet<IndicatorData>(new VolumeDescComparator());
		vol_set.addAll(indyDataMap.values());

		int i = 0;
		long uh_sum = 0;
		BigDecimal uh_vol = null;
		long hi_sum = 0;
		BigDecimal hi_vol = null;
		long avg_sum = 0;
		BigDecimal avg_vol = null;
		long lo_sum = 0;
		BigDecimal lo_vol = null;
		long vl_sum = 0;
		BigDecimal vl_vol = null;

		for (IndicatorData data : vol_set) {

			if (i < 4)
				uh_sum += data.getBarVolume();

			if (i > 0 && i < 5)
				hi_sum += data.getBarVolume();

			if (i > 3 && i < 8)
				avg_sum += data.getBarVolume();

			if (i > 5 && i < 10)
				lo_sum += data.getBarVolume();

			if (i > 9)
				vl_sum += data.getBarVolume();

			i++;
		}

		uh_vol = BigDecimal.valueOf(uh_sum).divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		hi_vol = BigDecimal.valueOf(hi_sum).divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		avg_vol = BigDecimal.valueOf(avg_sum).divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		lo_vol = BigDecimal.valueOf(lo_sum).divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		vl_vol = BigDecimal.valueOf(vl_sum).divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);

		// for (IndicatorData data : vol_set)
		// System.out.println("   >" + data.getBarVolume());
		// System.out.println(uh_vol);
		// System.out.println(hi_vol);
		// System.out.println(avg_vol);
		// System.out.println(lo_vol);
		// System.out.println(vl_vol);

		if (barVolume.intValue() > uh_vol.intValue())
			return "UH";
		else if (barVolume.intValue() > hi_vol.intValue())
			return "VH";
		else if (barVolume.intValue() > avg_vol.intValue())
			return "Hi";
		else if (barVolume.intValue() > lo_vol.intValue())
			return "AV";
		else if (barVolume.intValue() > vl_vol.intValue())
			return "Lo";
		else
			return "VL";
	}

	public boolean isReadyBarDataCache() {
		if (barDataCacheMap.size() < barDataCacheLength)
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
		stats.setVolumeAbsorb(barData.getVolumeAbsorb());
		stats.setVolumeThermometer(barData.getVolumeThermometer());
		stats.setId(barData.getId());

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
	private void moveIndicatorPositions() {
		if (indyDataPos == indyDataLength)
			indyDataPos = 1;
		else
			indyDataPos += 1;
	}

}
