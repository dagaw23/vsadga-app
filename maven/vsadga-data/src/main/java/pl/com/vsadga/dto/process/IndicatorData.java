package pl.com.vsadga.dto.process;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.BarType;

public class IndicatorData {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorData.class);

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

		cleanDataCache();
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

	public void cleanDataCache() {
		this.shortTermMap = new HashMap<Integer, BarStatsData>();
		this.mediumTermMap = new HashMap<Integer, Integer>();
		this.longTermMap = new HashMap<Integer, Integer>();

		this.shortTermPos = 1;
		this.mediumTermPos = 1;
		this.longTermPos = 1;
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
	
	private BarType getActualBarType(BarData barData) {
		BarStatsData prev_bar = getPrevBar();
		int comp_val = barData.getBarClose().compareTo(prev_bar.getBarClose());
		
		if (comp_val > 0)
			return BarType.UP_BAR;
		else if (comp_val < 0)
			return BarType.DOWN_BAR;
		else
			return BarType.LEVEL_BAR;
	}
	
	public String getVolumeThermometer(BarData barData) {
		
		BarStatsData prev_bar = null;
		int up_bar_count = 0;
		int down_bar_count = 0;
		BigDecimal up_bar_sum = new BigDecimal(0);
		BigDecimal down_bar_sum = new BigDecimal(0);
		
		for (Integer key : shortTermMap.keySet()) {
			prev_bar = shortTermMap.get(key);
			
			if (prev_bar.getBarType() == BarType.UP_BAR) {
				up_bar_count++;
				up_bar_sum.add(new BigDecimal(prev_bar.getBarVolume()));
				
			} else if (prev_bar.getBarType() == BarType.DOWN_BAR) {
				down_bar_count++;
				down_bar_sum.add(new BigDecimal(prev_bar.getBarVolume()));
				
			}
		}
		
		// dodanie jeszcze aktualnego bara:
		BarType act_bar_typ = getActualBarType(barData);
		
		if (act_bar_typ == BarType.UP_BAR) {
			up_bar_count++;
			up_bar_sum.add(new BigDecimal(barData.getBarVolume()));
		} else if (act_bar_typ == BarType.DOWN_BAR) {
			down_bar_count++;
			down_bar_sum.add(new BigDecimal(barData.getBarVolume()));
		}
		
		if (up_bar_count == 0) {
			LOGGER.info("   [INDY] brak UP barow [" + up_bar_count + "], ilosc DOWN [" + down_bar_count + "].");
			return "D";
		}
		if (down_bar_count == 0) {
			LOGGER.info("   [INDY] brak DOWN barow [" + down_bar_count + "], ilosc UP [" + up_bar_count + "].");
			return "U";
		}
		
		// wyliczenie srednich:
		BigDecimal up_avg = up_bar_sum.divide(new BigDecimal(up_bar_count));
		BigDecimal down_avg = down_bar_sum.divide(new BigDecimal(down_bar_count));
		int comp_avg = up_avg.compareTo(down_avg);
		LOGGER.info("   [INDY] UP avg [" + up_avg + "], DOWN avg [" + down_avg + "] = [" + comp_avg + "].");
		
		if (comp_avg > 0)
			return "U";
		else if (comp_avg < 0)
			return "D";
		else
			return"L";
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
		
		// wyliczenie UP/DOWN bara:
		BarStatsData prev_bar = getPrevBar();
		if (prev_bar == null)
			stats.setBarType(BarType.LEVEL_BAR);
		else {
			int comp_val = barData.getBarClose().compareTo(prev_bar.getBarClose());
			if (comp_val > 0)
				stats.setBarType(BarType.UP_BAR);
			else if (comp_val < 0)
				stats.setBarType(BarType.DOWN_BAR);
			else
				stats.setBarType(BarType.LEVEL_BAR);
		}

		return stats;
	}

}
