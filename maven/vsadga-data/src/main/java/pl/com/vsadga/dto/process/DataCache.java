package pl.com.vsadga.dto.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.cache.IndicatorData;

public class DataCache {
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

	public String getVolumeSize() {
		// czy mapa jest już wypełniona w pełni:
		if (indyDataMap.size() < indyDataLength)
			return "N";

		Set<IndicatorData> vol_set = new TreeSet<IndicatorData>(new Comparator<IndicatorData>() {

			@Override
			public int compare(IndicatorData data1, IndicatorData data2) {

				if (data1.getBarVolume().intValue() < data2.getBarVolume().intValue())
					return 1;
				else if (data1.getBarVolume().intValue() > data2.getBarVolume().intValue())
					return -1;
				else
					return 1;
			}
		});

		vol_set.addAll(indyDataMap.values());
		
		
		// max
		
		for (IndicatorData data : vol_set)
			System.out.println("   >" + data.getBarVolume());
		
		System.out.println(getUltraHigh(vol_set));
		System.out.println(getVeryHigh(vol_set));
		System.out.println(getAvg(vol_set));

		return "S";
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
		cache.addIndicatorData(312, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(3000, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(3400, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(3401, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(9890, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(890, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		cache.addIndicatorData(21, BigDecimal.valueOf(1.70), BigDecimal.valueOf(1.22));
		
		
		cache.getVolumeSize();
	}
	
	private BigDecimal getUltraHigh(Set<IndicatorData> indySet) {
		int i = 0;
		long sum = 0; 
		
		for (IndicatorData data : indySet) {
			sum += data.getBarVolume();
			i++;
			
			if (i >= 4)
				break;
		}
		
		return BigDecimal.valueOf(sum).divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getVeryHigh(Set<IndicatorData> indySet) {
		int i = 0;
		long sum = 0; 
		
		for (IndicatorData data : indySet) {
			// pierwszy bar pomiń:
			if (i != 0)
				sum += data.getBarVolume();
			
			i++;
			
			if (i >= 5)
				break;
		}
		
		return BigDecimal.valueOf(sum).divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
	}
	
	private BigDecimal getAvg(Set<IndicatorData> indySet) {
		int i = 0;
		long sum = 0;
		
		for (IndicatorData data : indySet) {
			// pierwszy bar pomiń:
			if (i > 3)
				sum += data.getBarVolume();
			
			i++;
			
			if (i >= 8)
				break;
		}
		
		return BigDecimal.valueOf(sum).divide(new BigDecimal(4), 2, RoundingMode.HALF_UP);
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
