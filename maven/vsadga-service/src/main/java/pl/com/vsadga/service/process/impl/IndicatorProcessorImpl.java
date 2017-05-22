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

	/*
	 * public static void main(String[] args) { IndicatorProcessorImpl obj = new
	 * IndicatorProcessorImpl();
	 * 
	 * BarData bar = new BarData(); bar.setBarLow(new BigDecimal(1262.48)); bar.setBarHigh(new
	 * BigDecimal(1263.21)); bar.setBarClose(new BigDecimal(1262.944));
	 * 
	 * System.out.println(obj.isClosedAboveHalf(bar)); }
	 */

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
	
	
	/**
	 * Sprawdza, czy Wolumen przesłanego barData - jest większy od Wolumenu w podanych barach.
	 * 
	 * @param barCount
	 *            liczba barów pobieranych z CACHE
	 * @param barData
	 *            bar aktualny, z którego pobierany jest Wolumen
	 * @return true: Wolumen przesłanego barData - jest większy od Wolumenu sprawdzanego zakresu barów, <br/>
	 *         false: Wolumen przesłanego barData - nie jest większy od Wolumenu sprawdzanego zakresu barów
	 */
	private boolean isHigherVolumeThenNbars(int barCount, BarData barData) {
		
		for (int i=0; i<barCount; i++) {
			if (!isHigherVolume(i, barData))
				return false;
		}
		
		return true;
	}
	
	
	//private boolean isPotentialStoppingVolume(BarData actualBar) {
		// DOWN bar powienie zamknąć się - z dala od minimum bara:
	//	if (isClosedDownPart(actualBar))
	//		return false;
		
		// spraed musi być średni lub mały:
	//	if (actualBar.getSpreadSize().getWeight() > 3)
	//		return false;
		
		// wolumen większy od poprzednich 8 barów:
	//	return isHigherVolumeThenNbars(8, actualBar);
	//}
	
	

	private IndicatorInfo getDownBarIndy(BarData barData) {
		BarData bar_prev = dataCache.getPrevBarData(1);
		//VolumeSize vol_size = barData.getVolumeSize();
		//SpreadSize spr_size = barData.getSpreadSize();
		
		// :: Hidden Upthrust ::
		if (isHiddenUpthrust(barData, bar_prev)) {
			LOGGER.info("   [INDY] DOWN bar Upthrust.");
			return new IndicatorInfo(false, 18);
		}
		
		// :: Down Bar No Demand ::
		if (isNoDemandVer1(barData, bar_prev)) {
			LOGGER.info("   [INDY] DOWN bar No Demand 1.");
			return new IndicatorInfo(false, 16);
		}
		if (isNoDemandVer2(barData, bar_prev)) {
			LOGGER.info("   [INDY] DOWN bar No Demand 2.");
			return new IndicatorInfo(false, 17);
		}
		
		// :: Some reversal action bars ::
		if (isReversalBarsAction(barData, bar_prev)) {
			LOGGER.info("   [INDY] DOWN bar Reversal Bars.");
			return getReversalBarIndicator(barData, bar_prev);
		}
		
		
		
		
		
		// kierunek: Bag Holding
		//if (isBagHolding(barData)) {
		//	LOGGER.info("   [INDY] DOWN bar Bag Holding.");
		//	return new IndicatorInfo(false, 36);
		//}
		
		// kierunek: Selling Climax
		//if (isSellingClimax(barData)) {
		//	LOGGER.info("   [INDY] DOWN bar Selling Climax.");
		//	return new IndicatorInfo(false, 33);
		//}
		
		// kierunek: Shakeout
		//indy_nr = isShakeout(barData);
		//if (indy_nr != null) {
		//	LOGGER.info("   [INDY] DOWN bar Shakeout.");
		//	return new IndicatorInfo(false, indy_nr);
		//}
		
		// kierunek: No Supply
		//if (isNoSupply(barData)) {
		//	LOGGER.info("   [INDY] DOWN bar No Supply.");
		//	return new IndicatorInfo(false, 198);
		//}
		
		//if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi) {
		//	BarData bar_last = dataCache.getPreviousBar(0);

			// kierunek: Trap Up Move
		//	if (bar_last != null && bar_last.getBarType() == BarType.UP_BAR
		//			&& isClosedUpPart(bar_last.getBarHigh(), bar_last.getBarLow(), bar_last.getBarClose())
		//			&& isClosedDownPart(barData) && barData.getBarHigh().compareTo(bar_last.getBarHigh()) > 0
		//			&& isClosedBelowHalf(bar_last.getBarHigh(), bar_last.getBarLow(), barData.getBarClose())) {
		//		LOGGER.info("   [INDY] DOWN bar Trap Up Move.");
		//		return new IndicatorInfo(true, 58);
		//	}
		//}

		//if (barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.Hi
		//		|| barData.getSpreadSize() == SpreadSize.AV) {
		//	BarData bar_last = dataCache.getPreviousBar(0);

		//	if (bar_last != null && (barData.getBarHigh().compareTo(bar_last.getBarHigh()) > 0)
		//			&& isClosedDownPart(barData)) {
				// kierunek: Hidden Upthrust
		//		LOGGER.info("   [INDY] DOWN bar Hidden Upthrust.");
		//		return new IndicatorInfo(false, 40);
		//	}
		//}

		// kierunek: Potential Stopping Volume
		//if (isPotentialStoppingVolume(barData)) {
		//	LOGGER.info("   [INDY] DOWN bar Potential Stopping Volume.");
		//	return new IndicatorInfo(false, 123);
		//}
		
		// kierunek: Test
		//if (isTest(barData)) {
		//	LOGGER.info("   [INDY] DOWN bar Test.");
		//	return new IndicatorInfo(false, 116);
		//}

		return null;
	}
	
	//private boolean isBagHolding(BarData actualBar) {
		// wymagany wolumen: UH
	//	if (actualBar.getVolumeSize().getWeight() < 6)
	//		return false;
		
		// wymagany spread: AV, Lo, VL
	//	if (actualBar.getSpreadSize().getWeight() > 2)
	//		return false;
		
		// wymagane zamknięcie: pośrodku lub w górnej części
	//	if (isClosedDownPart(actualBar))
	//		return false;
		
	//	return true;
	//}
	
	//private Integer isShakeout(BarData actualBar) {
		// wymagany spread: Hi, VH
	//	if (actualBar.getSpreadSize().getWeight() < 4)
	//		return null;
		
		// wymagane zamknięcie: w górnej części
	//	if (!isClosedUpPart(actualBar))
	//		return null;
		
		// wolumen: mały lub duży
	//	if (actualBar.getVolumeSize().getWeight() < 4)
	//		return 87;
	//	else
	//		return 34;
	//}
	
	//private boolean isNoSupply(BarData actualBar) {
		// wymagany spread: VL, Lo, AV
	//	if (actualBar.getSpreadSize().getWeight() > 3)
	//		return false;
		
	//	BarData bar_last = dataCache.getPreviousBar(0);
	//	BarData bar_prev = dataCache.getPreviousBar(1);

		// czy poprzednie bary są gotowe:
	//	if (bar_last == null || bar_prev == null)
	//		return false;
		
	//	return dataCache.isVolumeLessThen2(actualBar, bar_last, bar_prev);
	//}
	
	//private boolean isSellingClimax(BarData actualBar) {
		// wymagany wolumen: UH
	//	if (actualBar.getVolumeSize().getWeight() < 6)
	//		return false;
		
		// wymagany spread: Hi, VH
	//	if (actualBar.getSpreadSize().getWeight() < 4)
	//		return false;
		
		// wymagane zamknięcie: pośrodku lub w górnej części
	//	if (isClosedDownPart(actualBar))
	//		return false;
		
	//	return true;
	//}
	
	//private boolean isTest(BarData actualBar) {
		// DOWN bar powienie zamknąć się w górnej części:
	//	if (isClosedDownPart(actualBar))
	//		return false;
		
		// spraed musi być średni lub mały:
	//	if (actualBar.getSpreadSize().getWeight() > 3)
	//		return false;
		
		// wolumen musi być średni lub mały:
	//	if (actualBar.getVolumeSize().getWeight() > 3)
	//		return false;
		
	//	return true;
	//}

	private boolean isBottomReversal(BarData actualBar, BarData prevBar) {
		// poprzedni DOWN bar powinien zamknąć się w dolnej części:
		if (!isClosedDownPart(prevBar))
			return false;
		
		// poprzedni DOWN bar - spread powinien być średni lub duży:
		if (!isHighSpread(prevBar))
			return false;
		
		// Low, Close - w porównaniu z 2-ma poprzednimi:
		if (!isLowerLowAndLowerClose(2, prevBar) || !isLowerLowAndLowerClose(3, prevBar)
				|| !isLowerLowAndLowerClose(4, prevBar) || !isLowerLowAndLowerClose(5, prevBar))
			return false;
			
		// UP bar powinien zamknąć się w górnej części bara:
		if (!isClosedUpPart(actualBar))
			return false;
		
		// wolumeny na obydwu barach: Hi lub większe
		if (!isHighOrAvgVolume(actualBar) || !isHighOrAvgVolume(prevBar))
			return false;
		
		// aktualny bar - spread powinien być średni lub duży:
		if (!isHighOrAvgSpread(actualBar))
			return false;
			
		return true;
	}

	/**
	 * Sprawdza, czy Low i Close przesłanego barData - jest mniejszy od Low i Close bara o podanym
	 * numerze. Jeśli bar o podanym numerze jeszcze nie istnieje w CACHE - zwracana jest wartość
	 * false.
	 * 
	 * @param barNr
	 *            numer bara pobieranego z CACHE
	 * @param barData
	 *            bar aktualny, z którego pobierany jest Low
	 * @return true: Low i Close przesłanego barData - jest mniejszy od Low i Close bara o podanym
	 *         numerze, <br/>
	 *         false: Low lub Close przesłanego barData - nie jest mniejszy od Low bara o podanym
	 *         numerze lub też bar o podanym numerze jeszcze nie istnieje w CACHE
	 */
	private boolean isLowerLowAndLowerClose(int barNr, BarData barData) {
		BarData prev_bar = dataCache.getPrevBarData(barNr);

		if (prev_bar == null)
			return false;

		if ((barData.getBarLow().compareTo(prev_bar.getBarLow()) < 0)
				&& (barData.getBarClose().compareTo(prev_bar.getBarClose()) < 0))
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy Wolumen przesłanego barData - jest większy od Wolumenu bara o podanym
	 * numerze. Jeśli bar o podanym numerze jeszcze nie istnieje w CACHE - zwracana jest wartość
	 * false.
	 * 
	 * @param barNr
	 *            numer bara pobieranego z CACHE
	 * @param barData
	 *            bar aktualny, z którego pobierany jest Wolumen
	 * @return true: Wolumen przesłanego barData - jest większy od Wolumenu bara o podanym
	 *         numerze, <br/>
	 *         false: Wolumen przesłanego barData - nie jest większy od Wolumenu bara o podanym
	 *         numerze lub też bar o podanym numerze jeszcze nie istnieje w CACHE
	 */
	private boolean isHigherVolume(int barNr, BarData barData) {
		BarData prev_bar = dataCache.getPrevBarData(barNr);

		if (prev_bar == null)
			return false;

		if (barData.getBarVolume().intValue() > prev_bar.getBarVolume().intValue())
			return true;
		else
			return false;
	}

	
	private IndicatorInfo getUpBarIndy(BarData barData) {
		BarData bar_prev = dataCache.getPrevBarData(1);
	//	VolumeSize vol_size = barData.getVolumeSize();
	//	SpreadSize spr_size = barData.getSpreadSize();
		
		// :: Buying Climax ::
		if (isBuyingClimax(barData)) {
			LOGGER.info("   [INDY] UP bar Buying Climax.");
			return new IndicatorInfo(false, 1);
		}
		
		// :: Upthrust ::
		if (isUpthrust(barData, bar_prev)) {
			LOGGER.info("   [INDY] UP bar Upthrust.");
			return new IndicatorInfo(false, 8);
		}
		
		// :: No Demand ::
		if (isNoDemandVer1(barData, bar_prev)) {
			LOGGER.info("   [INDY] UP bar No Demand 1.");
			return new IndicatorInfo(false, 6);
		}
		if (isNoDemandVer2(barData, bar_prev)) {
			LOGGER.info("   [INDY] UP bar No Demand 2.");
			return new IndicatorInfo(false, 7);
		}
		
		// :: Supply Coming In ::
		if (isSupplyComingIn(barData, bar_prev)) {
			LOGGER.info("   [INDY] UP bar Supply Coming In.");
			return new IndicatorInfo(false, 3);
		}
		
		
		
		
		
		
		
		
		

		// możliwa akcja na dwóch barach:
		//if (bar_prev.getBarType() == BarType.DOWN_BAR) {
		//	if (isBottomReversal(barData, bar_prev)) {
		//		LOGGER.info("   [INDY] UP bar Bottom Reversal.");
		//		return new IndicatorInfo(true, 2);
		//	}
		//}
		
		// :: Stopping volume ::
		//if (isStoppingVolume(barData)) {
		//	LOGGER.info("   [INDY] Stopping Volume.");
		//	return new IndicatorInfo(false, 5);
		//}
		
		

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
	 * Sprawdza, czy w podanym barze - występuje spread: średni (Av), wysoki (Hi), bardzo wysoki (VH) lub ultra wysoki (UH).
	 * 
	 * @param barData analizowany bar
	 * @return true: spread w barze jest średni lub wysoki, false: spread w barze jest mały
	 */
	private boolean isHighOrAvgSpread(BarData barData) {
		if (barData.getSpreadSize() == SpreadSize.Av || barData.getSpreadSize() == SpreadSize.Hi
				|| barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.UH)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy w podanym barze - występuje spread: mały (Lo) lub bardzo mały (VL).
	 * 
	 * @param barData analizowany bar
	 * @return true: spread w barze jest mały, false: spread w barze jest duży lub średni
	 */
	private boolean isLowSpread(BarData barData) {
		if (barData.getSpreadSize() == SpreadSize.Lo || barData.getSpreadSize() == SpreadSize.VL)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy w podanym barze - występuje spread: wysoki (Hi), bardzo wysoki (VH) lub ultra wysoki (UH).
	 * 
	 * @param barData analizowany bar
	 * @return true: spread w barze jest wysoki, bardzo wysoki lub ultra wysoki, false: spread w barze jest mały lub średni
	 */
	private boolean isHighSpread(BarData barData) {
		if (barData.getSpreadSize() == SpreadSize.Hi || barData.getSpreadSize() == SpreadSize.VH || barData.getSpreadSize() == SpreadSize.UH)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy w podanym barze - występuje wolumen średni (Av), wysoki (Hi), bardzo wysoki (VH) lub ultra wysoki (UH).
	 * 
	 * @param barData analizowany bar
	 * @return true: wolumen w barze jest średni lub wysoki, false: wolumen w barze jest mały
	 */
	private boolean isHighOrAvgVolume(BarData barData) {
		if (barData.getVolumeSize() == VolumeSize.Av || barData.getVolumeSize() == VolumeSize.Hi
				|| barData.getVolumeSize() == VolumeSize.VH || barData.getVolumeSize() == VolumeSize.UH)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy w podanym barze - występuje wolumen: wysoki (Hi), bardzo wysoki (VH) lub ultra wysoki (UH).
	 * 
	 * @param barData analizowany bar
	 * @return true: wolumen w barze jest wysoki, false: wolumen w barze jest średni lub mały
	 */
	private boolean isHighVolume(BarData barData) {
		if (barData.getVolumeSize() == VolumeSize.Hi || barData.getVolumeSize() == VolumeSize.VH || barData.getVolumeSize() == VolumeSize.UH)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy w podanym barze - występuje wolumen: mały (Lo) lub bardzo mały (VL).
	 * 
	 * @param barData analizowany bar
	 * @return true: wolumen w barze jest mały lub bardzo mały, false: wolumen w barze jest średni lub duży
	 */
	private boolean isLowVolume(BarData barData) {
		if (barData.getVolumeSize() == VolumeSize.Lo || barData.getVolumeSize() == VolumeSize.VL)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy w podanym barze - występuje wolumen: bardzo wysoki (VH) lub ultra wysoki (UH).
	 * 
	 * @param barData analizowany bar
	 * @return true: wolumen w barze jest bardzo wysoki, false: wolumen w barze jest duży, średni lub mały
	 */
	private boolean isVeryHighVolume(BarData barData) {
		if (barData.getVolumeSize() == VolumeSize.VH || barData.getVolumeSize() == VolumeSize.UH)
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
	private boolean isClosedDownPart(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		
		// 30% od minimum bara:
		BigDecimal spr_add = b_spread.multiply(new BigDecimal(0.30)).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_limit = barLow.add(spr_add).setScale(6, RoundingMode.HALF_UP);
		
		return isLower(barClose, price_limit);
	}
	
	private boolean isBuyingClimax(BarData barData) {
		// wolumen powinien być wysoki:
		if (!isHighVolume(barData))
			return false;
		
		// spread powinien być średni lub wysoki:
		if (!isHighOrAvgSpread(barData))
			return false;
		
		return isClosedInMiddle(barData);
	}
	
	private boolean isStoppingVolume(BarData barData) {
		if (!isVeryHighVolume(barData))
			return false;
		
		if (!isLowSpread(barData))
			return false;
		
		return true;
	}
	
	private boolean isSupplyComingIn(BarData barData, BarData prevBarData) {
		// wolumen powinien być duży:
		if (!isHighVolume(barData))
			return false;
		
		// wolumen powinien być większy niż na poprzednim barze:
		if (isLower(barData.getBarVolume(), prevBarData.getBarVolume()))
			return false;
		
		// spread powinien być duży:
		if (!isHighSpread(barData))
			return false;
		
		if (isClosedUpPart(barData))
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy value1 jest mniejsze od value2.
	 * 
	 * @param value1
	 * @param value2
	 * @return true: value1 jest mniejsze od value2, false: value1 jest większe lub równe value2
	 */
	private boolean isLower(BigDecimal value1, BigDecimal value2) {
		int comp_val = value1.compareTo(value2);
		
		if (comp_val == -1)
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy value1 jest mniejsze od value2.
	 * 
	 * @param value1
	 * @param value2
	 * @return true: value1 jest mniejsze od value2, false: value1 jest większe lub równe value2
	 */
	private boolean isLower(Integer value1, Integer value2) {
		if (value1.intValue() < value2.intValue())
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy value1 jest większe od value2.
	 * 
	 * @param value1
	 * @param value2
	 * @return true: value1 jest większe od value2, false: value1 jest mniejsze lub równe value2
	 */
	private boolean isHigher(BigDecimal value1, BigDecimal value2) {
		int comp_val = value1.compareTo(value2);
		
		if (comp_val == 1)
			return true;
		else
			return false;
	}

	/**
	 * Sprawdza, czy value1 jest większe od value2.
	 * 
	 * @param value1
	 * @param value2
	 * @return true: value1 jest większe od value2, false: value1 jest mniejsze lub równe value2
	 */
	private boolean isHigher(Integer value1, Integer value2) {
		if (value1.intValue() > value2.intValue())
			return true;
		else
			return false;
	}
	
	/**
	 * Sprawdza, czy value1 jest mniejsze od value2 lub są równe.
	 * 
	 * @param value1
	 * @param value2
	 * @return true: value1 jest mniejsze od value2 lub są równe, false: value1 jest większe od value2
	 */
	private boolean isLowerOrEqual(BigDecimal value1, BigDecimal value2) {
		int comp_val = value1.compareTo(value2);
		
		if (comp_val <= 0)
			return true;
		else
			return false;
	}
	
	private boolean isUpthrust(BarData barData, BarData prevBarData) {
		// maksimum Upthrust bar - powyżej maksimum poprzedniego bara:
		if (isLowerOrEqual(barData.getBarHigh(), prevBarData.getBarHigh()))
			return false;
		
		// minimum Upthrust bar - powyżej zamknięcia poprzedniego bara:
		if (isLower(barData.getBarLow(), prevBarData.getBarClose()))
			return false;
		
		// spread musi być średni lub duży:
		if (isLowSpread(barData))
			return false;
		
		// wolumen może być mały, duży, bardzo duży
		
		// zamknięcie powinno być w dolnej części bara:
		return isClosedDownPart(barData);
	}
	
	private boolean isNoDemandVer1(BarData barData, BarData prevBarData) {
		BarData prev_prev = dataCache.getPrevBarData(2);
		
		// maksimum aktualny bar - powyżej maksimum poprzedniego bara:
		if (isLower(barData.getBarHigh(), prevBarData.getBarHigh()))
			return false;
		
		// spread może być tylko mały:
		if (isHighOrAvgSpread(barData))
			return false;
		
		// zamknięcie pośrodku lub w górnej części bara:
		if (!isClosedInMiddle(barData) && !isClosedUpPart(barData))
			return false;
		
		// czy bar jest gotowy:
		if (prev_prev == null)
			return false;
		
		return dataCache.isVolumeLessThen2(barData, prevBarData, prev_prev);
	}
	
	private boolean isNoDemandVer2(BarData barData, BarData prevBarData) {
		// maksimum aktualny bar - powyżej maksimum poprzedniego bara:
		if (isLower(barData.getBarHigh(), prevBarData.getBarHigh()))
			return false;
		
		// spread może być tylko mały:
		if (isHighOrAvgSpread(barData))
			return false;
		
		// zamknięcie pośrodku lub w górnej części bara:
		if (!isClosedInMiddle(barData) && !isClosedUpPart(barData))
			return false;
		
		return isLowVolume(barData);
	}
	
	private boolean isReversalBarsAction(BarData barData, BarData prevBarData) {
		// obydwa bary - z dużym spreadem:
		if (!isHighSpread(prevBarData) || isHighSpread(barData))
			return false;
		
		// zamknięcie 1: w górnej części, 2: w dolnej części
		if (!isClosedUpPart(prevBarData) || !isClosedDownPart(barData))
			return false;
		
		if (!isHighVolume(prevBarData) && !isHighVolume(barData))
			return false;
		
		return true;
	}
	
	private IndicatorInfo getReversalBarIndicator(BarData barData, BarData prevBarData) {
		int indy_nr = 21;
		
		// jeśli High 2-go bara > od High 1-szego bara:
		if (isHigher(barData.getBarHigh(), prevBarData.getBarHigh()))
			indy_nr = indy_nr + 1;
		
		// jeśli Close 2-go bara < od Low 1-szego bara:
		if (isLower(barData.getBarClose(), prevBarData.getBarLow()))
			indy_nr = indy_nr + 2;
		
		// jeśli wolumen 1-go bara > wolumen 2-go bara:
		if (isHigher(prevBarData.getBarVolume(), barData.getBarVolume()))
			indy_nr = indy_nr + 4;
		
		return new IndicatorInfo(true, indy_nr);
	}
	
	private boolean isHiddenUpthrust(BarData barData, BarData prevBarData) {
		// maksimum Upthrust bar - powyżej maksimum poprzedniego bara:
		if (isLowerOrEqual(barData.getBarHigh(), prevBarData.getBarHigh()))
			return false;
		
		// minimum Upthrust bar - powyżej minimum poprzedniego bara:
		if (isLowerOrEqual(barData.getBarLow(), prevBarData.getBarLow()))
			return false;
				
		// spread musi być średni lub duży:
		if (isLowSpread(barData))
			return false;
				
		// wolumen może być mały, duży, bardzo duży
				
		// zamknięcie powinno być w dolnej części bara:
		return isClosedDownPart(barData);
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
	 * Sprawdza, czy cena jest zamknięta w środku bara (z 25% zakresem od środka bara).
	 * 
	 * @param barData
	 * @return
	 */
	private boolean isClosedInMiddle(BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose) {
		BigDecimal b_spread = barHigh.subtract(barLow).setScale(6, RoundingMode.HALF_UP);
		BigDecimal spr_half = b_spread.divide(new BigDecimal(2), 5, RoundingMode.HALF_UP);
		BigDecimal spr_add = spr_half.multiply(new BigDecimal(0.25)).setScale(6, RoundingMode.HALF_UP);

		BigDecimal price_half = barLow.add(spr_half).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_up = price_half.add(spr_add).setScale(6, RoundingMode.HALF_UP);
		BigDecimal price_down = price_half.add(spr_add.multiply(new BigDecimal(-1))).setScale(6,
				RoundingMode.HALF_UP);
		
		return (isLower(barClose, price_up) && isHigher(barClose, price_down));
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
		BigDecimal price_limit = barHigh.add(spr_add.multiply(new BigDecimal(-1))).setScale(6, RoundingMode.HALF_UP);

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
