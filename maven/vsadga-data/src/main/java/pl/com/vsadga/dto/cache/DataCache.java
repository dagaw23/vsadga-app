package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.SpreadSize;
import pl.com.vsadga.data.VolumeSize;
import pl.com.vsadga.dto.BarType;

public class DataCache {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataCache.class);

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
			if (data1.getBarSpreadVolume().intValue() < data2.getBarSpreadVolume().intValue())
				return 1;
			else if (data1.getBarSpreadVolume().intValue() > data2.getBarSpreadVolume().intValue())
				return -1;
			else
				return 1;
		}

	}

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
	 * Czyści wykorzystywane obiekty CACHE oraz wskaźniki pozycji w mapach.
	 */
	public void cleanDataCache() {
		this.barDataCacheMap = new HashMap<Integer, BarData>();
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

	public SpreadSize getSpreadSize(BigDecimal barHigh, BigDecimal barLow) {
		// czy mapa jest już wypełniona w pełni:
		if (indyDataMap.size() < indyDataLength)
			return SpreadSize.N;

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
			return SpreadSize.VH;
		else if (act_spread.compareTo(avg_spr) > 0)
			return SpreadSize.Hi;
		else if (act_spread.compareTo(lo_spr) > 0)
			return SpreadSize.AV;
		else if (act_spread.compareTo(vl_spr) > 0)
			return SpreadSize.Lo;
		else
			return SpreadSize.VL;
	}

	public VolumeSize getVolumeSize(BarData barData) {
		VolumeSize result = null;
		
		// czy mapa jest już wypełniona w pełni:
		if (indyDataMap.size() < indyDataLength)
			return VolumeSize.N;

		// posortuj wg wolumenu:
		Set<IndicatorData> vol_set = new TreeSet<IndicatorData>(new VolumeDescComparator());
		vol_set.addAll(indyDataMap.values());

		int i = 0;
		BigDecimal uh_sum = new BigDecimal(0);
		BigDecimal uh_vol = null;
		BigDecimal hi_sum = new BigDecimal(0);
		BigDecimal hi_vol = null;
		BigDecimal avg_sum = new BigDecimal(0);
		BigDecimal avg_vol = null;
		BigDecimal lo_sum = new BigDecimal(0);
		BigDecimal lo_vol = null;
		BigDecimal vl_sum = new BigDecimal(0);
		BigDecimal vl_vol = null;

		for (IndicatorData data : vol_set) {

			if (i < 4)
				uh_sum = uh_sum.add(data.getBarSpreadVolume());

			if (i > 0 && i < 5)
				hi_sum = hi_sum.add(data.getBarSpreadVolume());

			if (i > 3 && i < 8)
				avg_sum = avg_sum.add(data.getBarSpreadVolume());

			if (i > 5 && i < 10)
				lo_sum = lo_sum.add(data.getBarSpreadVolume());

			if (i > 9)
				vl_sum = vl_sum.add(data.getBarSpreadVolume());

			i++;
		}

		uh_vol = uh_sum.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		hi_vol = hi_sum.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		avg_vol = avg_sum.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		lo_vol = lo_sum.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		vl_vol = vl_sum.divide(new BigDecimal(4), 0, RoundingMode.HALF_UP);
		
		// aktualny wolumen:
		BigDecimal bar_vol = new BigDecimal(barData.getBarVolume());
		bar_vol.setScale(2);

		if (bar_vol.compareTo(uh_vol) > 0)
			result = VolumeSize.UH;
		else if (bar_vol.compareTo(hi_vol) > 0)
			result = VolumeSize.VH;
		else if (bar_vol.compareTo(avg_vol) > 0)
			result = VolumeSize.Hi;
		else if (bar_vol.compareTo(lo_vol) > 0)
			result = VolumeSize.AV;
		else if (bar_vol.compareTo(vl_vol) > 0)
			result = VolumeSize.Lo;
		else
			result = VolumeSize.VL;
		
		// for (IndicatorData data : vol_set)
		// System.out.println("   >" + data.getBarVolume());
		StringBuffer sb = new StringBuffer();
		sb.append("   > Time:" + getDate(barData.getBarTime()));
		sb.append(", VOL:").append(bar_vol).append(", UH=");
		sb.append(uh_vol).append(", HI=");
		sb.append(hi_vol).append(", AVG=");
		sb.append(avg_vol).append(", LO=");
		sb.append(lo_vol).append(", VL=");
		sb.append(vl_vol).append(" - RETURN=").append(result).append(".");
		LOGGER.info(sb.toString());
		
		return result;
	}
	
	private String getDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
		
		return sdf.format(date);
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
