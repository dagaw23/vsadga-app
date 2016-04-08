package pl.com.vsadga.service.process.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.SpreadSize;
import pl.com.vsadga.data.VolumeSize;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.cache.DataCache;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.IndicatorProcessor;

public class IndicatorProcessorImpl implements IndicatorProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorProcessorImpl.class);

	/*public static void main(String[] args) {
		IndicatorProcessorImpl obj = new IndicatorProcessorImpl();

		BarData bar = new BarData();
		bar.setBarLow(new BigDecimal(1262.48));
		bar.setBarHigh(new BigDecimal(1263.21));
		bar.setBarClose(new BigDecimal(1262.944));

		System.out.println(obj.isClosedAboveHalf(bar));
	}*/

	private ConfigDataService configDataService;

	private DataCache dataCache;

	public IndicatorProcessorImpl() {
		super();
	}

	@Override
	public IndicatorInfo getDataIndicator(BarData barData) throws BaseServiceException {
		if (!isProcessIndicator()) {
			LOGGER.info("   [INDY] Usluga przetwarzania wskaznika jest wylaczona.");
			return null;
		}

		if (barData.getBarType() == BarType.UP_BAR) {
			// wskaźniki dla UP barów:
			return getUpBarIndy(barData);

		} else if (barData.getBarType() == BarType.DOWN_BAR) {
			// wskaźniki dla DOWN barów:
			return getDownBarIndy(barData);

		} else {
			LOGGER.info("   [INDY] Bar typu [" + barData.getBarType() + "] nie jest przetwarzany.");
			return null;
		}
	}

	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	/**
	 * @param dataCache
	 */
	public void setDataCache(DataCache dataCache) {
		this.dataCache = dataCache;
	}

	private IndicatorInfo getDownBarIndy(BarData barData) {

		if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi) {
			BarData bar_last = dataCache.getPreviousBar(0);

			// kierunek: Trap Up Move
			if (bar_last != null && bar_last.getBarType() == BarType.UP_BAR
					&& isClosedUpPart(bar_last.getBarHigh(), bar_last.getBarLow(), bar_last.getBarClose())
					&& isClosedDownPart(barData) 
					&& barData.getBarHigh().compareTo(bar_last.getBarHigh()) > 0
					&& isClosedBelowHalf(bar_last.getBarHigh(), bar_last.getBarLow(), barData.getBarClose())) {
				LOGGER.info("   [INDY] DOWN bar Trap Up Move.");
				return new IndicatorInfo(false, 58);
			}
		}

		if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi
				|| barData.getSpreadSize() == SpreadSize.AV) {
			BarData bar_last = dataCache.getPreviousBar(0);

			if (bar_last != null && (barData.getBarHigh().compareTo(bar_last.getBarHigh()) > 0)
					&& isClosedDownPart(barData)) {
				// kierunek: Hidden Upthrust
				LOGGER.info("   [INDY] DOWN bar Hidden Upthrust.");
				return new IndicatorInfo(false, 40);
			}
		}

		if (barData.getSpreadSize() == SpreadSize.VL || barData.getSpreadSize() == SpreadSize.Lo
				|| barData.getSpreadSize() == SpreadSize.AV) {
			BarData bar_last = dataCache.getPreviousBar(0);
			BarData bar_prev = dataCache.getPreviousBar(1);

			// czy poprzednie bary są gotowe:
			if (bar_last != null && bar_prev != null) {
				// kierunek: No Supply
				if (dataCache.isVolumeLessThen2(barData, bar_last, bar_prev))
					return new IndicatorInfo(false, 198);
			}
		}

		return null;
	}

	private IndicatorInfo getUpBarIndy(BarData barData) {
		if (barData.getVolumeSize() == VolumeSize.UH || barData.getVolumeSize() == VolumeSize.VH) {

			if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi) {
				// kierunek: Buying Climax
				if (isClosedInMiddle(barData)) {
					LOGGER.info("   [INDY] UP bar Buying Climax.");
					return new IndicatorInfo(false, 1);
				}
			}
		}

		if (barData.getVolumeSize() == VolumeSize.VH || barData.getVolumeSize() == VolumeSize.Hi) {

			if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi) {
				// kierunek: Supply Coming In
				if (isClosedUpPart(barData)) {
					LOGGER.info("   [INDY] UP bar Supply Coming In.");
					return new IndicatorInfo(false, 7);
				}
			}
		}

		if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi
				|| barData.getSpreadSize() == SpreadSize.AV) {

			// kierunek: Upthrust
			if (isClosedDownPart(barData)) {
				LOGGER.info("   [INDY] UP bar Upthrust.");
				return new IndicatorInfo(false, 8);
			}
		}

		if (barData.getSpreadSize() == SpreadSize.VL || barData.getSpreadSize() == SpreadSize.Lo
				|| barData.getSpreadSize() == SpreadSize.AV) {
			BarData bar_last = dataCache.getPreviousBar(0);
			BarData bar_prev = dataCache.getPreviousBar(1);

			// czy poprzednie bary są gotowe:
			if (bar_last != null && bar_prev != null) {
				// kierunek: No Demand
				if (dataCache.isVolumeLessThen2(barData, bar_last, bar_prev)) {
					LOGGER.info("   [INDY] UP bar No demand.");
					return new IndicatorInfo(false, 6);
				}
			}
		}

		return null;
	}

	/**
	 * Sprawdza, czy cena jest zamknięta powyżej połowy bara - włącznie z samą ceną będącą połową
	 * bara.
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedAboveHalf(BarData barData) {
		return isClosedAboveHalf(barData.getBarHigh(), barData.getBarLow(), barData.getBarClose());
	}

	/**
	 * Sprawdza, czy cena jest zamknięta powyżej połowy bara - włącznie z samą ceną będącą połową
	 * bara.
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedAboveHalf(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		BigDecimal spr_half = b_spread.divide(new BigDecimal(2), 5, RoundingMode.HALF_UP);
		BigDecimal price_half = barLow.add(spr_half).setScale(6, RoundingMode.HALF_UP);

		// System.out.println("   spread=" + b_spread + ", spr_half=" + spr_half + ", price_half=" +
		// price_half);

		if (barClose.compareTo(price_half) >= 0)
			return true;
		else
			return false;
	}

	/**
	 * Sprawdza, czy cena jest zamknięta poniżej połowy bara - włącznie z samą ceną będącą połową
	 * bara.
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedBelowHalf(BarData barData) {
		return isClosedBelowHalf(barData.getBarHigh(), barData.getBarLow(), barData.getBarClose());
	}

	/**
	 * Sprawdza, czy cena jest zamknięta poniżej połowy bara - włącznie z samą ceną będącą połową
	 * bara.
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedBelowHalf(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		BigDecimal spr_half = b_spread.divide(new BigDecimal(2), 5, RoundingMode.HALF_UP);
		BigDecimal price_half = barLow.add(spr_half).setScale(6, RoundingMode.HALF_UP);

		// System.out.println("   spread=" + b_spread + ", spr_half=" + spr_half + ", price_half=" +
		// price_half);

		if (barClose.compareTo(price_half) <= 0)
			return true;
		else
			return false;
	}

	/**
	 * Sprawdza, czy cena jest zamknięta w dolnej części bara (w 30% zakresie od minimalnej ceny
	 * bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedDownPart(BarData barData) {
		return isClosedDownPart(barData.getBarHigh(), barData.getBarLow(), barData.getBarClose());
	}

	/**
	 * Sprawdza, czy cena jest zamknięta w dolnej części bara (w 30% zakresie od minimalnej ceny
	 * bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedDownPart(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		BigDecimal spr_add = b_spread.multiply(new BigDecimal(0.30)).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_limit = barLow.add(spr_add).setScale(6, RoundingMode.HALF_UP);

		//System.out.println("   spread=" + b_spread + ", spr_add=" + spr_add + ", price_limit=" + price_limit);

		if (barClose.compareTo(price_limit) < 0)
			return true;
		else
			return false;
	}

	/**
	 * Sprawdza, czy cena jest zamknięta w środku bara (z 35% zakresem od środka bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedInMiddle(BarData barData) {
		return isClosedInMiddle(barData.getBarHigh(), barData.getBarLow(), barData.getBarClose());
	}

	/**
	 * Sprawdza, czy cena jest zamknięta w środku bara (z 35% zakresem od środka bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedInMiddle(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		BigDecimal spr_half = b_spread.divide(new BigDecimal(2), 5, RoundingMode.HALF_UP);
		BigDecimal spr_add = spr_half.multiply(new BigDecimal(0.35)).setScale(6, RoundingMode.HALF_UP);

		BigDecimal price_half = barLow.add(spr_half).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_up = price_half.add(spr_add).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_down = price_half.add(spr_add.multiply(new BigDecimal(-1))).setScale(6,
				RoundingMode.HALF_UP);

		// System.out.println("   spread=" + b_spread + ", spr_half=" + spr_half + ", spr_add=" +
		// spr_add + ", price_half=" + price_half + ", price_up=" + price_up +
		// ", price_down="+price_down);

		if ((barClose.compareTo(price_up) < 0) && (barClose.compareTo(price_down) > 0))
			return true;
		else
			return false;
	}

	/**
	 * Sprawdza, czy cena jest zamknięta w górnej części bara (w 30% zakresie od maksymalnej ceny
	 * bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedUpPart(BarData barData) {
		return isClosedUpPart(barData.getBarHigh(), barData.getBarLow(), barData.getBarClose());
	}

	/**
	 * Sprawdza, czy cena jest zamknięta w górnej części bara (w 30% zakresie od maksymalnej ceny
	 * bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedUpPart(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		BigDecimal spr_add = b_spread.multiply(new BigDecimal(0.30)).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_limit = barHigh.add(spr_add.multiply(new BigDecimal(-1))).setScale(6,
				RoundingMode.HALF_UP);

		// System.out.println("   spread=" + b_spread + ", spr_add=" + spr_add + ", price_limit=" +
		// price_limit);
		if (barClose.compareTo(price_limit) > 0)
			return true;
		else
			return false;
	}

	private boolean isProcessIndicator() throws BaseServiceException {

		String param_value = configDataService.getParam("IS_PROCESS_INDICATOR");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [TREND] Brak parametru IS_PROCESS_INDICATOR [" + param_value
					+ "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [TREND] Parametr IS_PROCESS_INDICATOR [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1)
			return true;
		else
			return false;
	}

}
