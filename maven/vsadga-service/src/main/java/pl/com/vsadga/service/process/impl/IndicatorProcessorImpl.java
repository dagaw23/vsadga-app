package pl.com.vsadga.service.process.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.dto.process.IndicatorData;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.IndicatorProcessor;
import pl.com.vsadga.utils.DateConverter;

public class IndicatorProcessorImpl implements IndicatorProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorProcessorImpl.class);

	private ConfigDataService configDataService;

	private IndicatorData indicatorData;
	
	/**
	 * minimalna ilość barów - potrzebna do wyliczenia wskaźników
	 */
	private Integer indicatorDataSize;

	public IndicatorProcessorImpl() {
		super();
	}
	
	@Override
	public void addIndicatorData(BarData barData) throws BaseServiceException {
		indicatorData.addBarData(barData);
	}



	@Override
	public void addIndicatorData(BarData barData, Boolean isBarToConfirmation) throws BaseServiceException {
		indicatorData.addBarData(barData, isBarToConfirmation);
	}

	@Override
	public void clearIndicatorData() {
		this.indicatorData = new IndicatorData(indicatorDataSize);
	}

	@Override
	public IndicatorInfo getDataIndicator(BarData barData, String frameDesc) throws BaseServiceException {
		int indy_nr = 0;
		
		if (!isProcessIndicator()) {
			LOGGER.info("   [INDY] Usluga przetwarzania wskaznika jest wylaczona.");
			return null;
		}

		// jeśli mapa dla wskaźnika nie została wyliczona - tylko wpisz Bar do kolekcji:
		if (!indicatorData.isReadyIndicatorMap()) {
			indicatorData.addBarData(barData);

			return new IndicatorInfo(false);
		}
		
		// TODO 1: Dodać sprawdzenie czy poprzedni bar do potwierdzenia

		// zapisz informację o barze do mapy:
		indy_nr = getActualBarIndicator(barData);
		if (indy_nr > 0) {
			indicatorData.addBarData(barData);
			return new IndicatorInfo(indy_nr, true);
		}
		
		indicatorData.addBarData(barData);
		return new IndicatorInfo(0, true);
	}

	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	/**
	 * @param indicatorDataSize the indicatorDataSize to set
	 */
	public void setIndicatorDataSize(Integer indicatorDataSize) {
		this.indicatorDataSize = indicatorDataSize;
	}

	private Integer getActualBarIndicator(BarData barData) {
		// TODO 1: dodac wpisywanie czasu bara do BarStatsData
		// TODO 2: parametry konfigurayjne do pobierania liczby barów do przetworzenia oraz liczby
		// barów w mapie.
		// pobierz 2 poprzednie bary:
		BarStatsData prev_bar = indicatorData.getPrevBar();
		BarStatsData prev_prev_bar = indicatorData.getPrevPrevBar();
		
		// no-demand/no-supply
		if (isLessThenLast2(prev_prev_bar, prev_bar, barData)) {
			if (isUpBar(barData, prev_bar)) {
				LOGGER.info("SPREAD AVG:" + indicatorData.getSpreadAvg() + ",ACT SPREAD:"
						+ barData.getBarHigh().subtract(barData.getBarLow()) + ": UP BAR.");
				return 6;
			}
			
			if (isDownBar(barData, prev_bar)) {
				LOGGER.info("SPREAD AVG:" + indicatorData.getSpreadAvg() + ",ACT SPREAD:"
						+ barData.getBarHigh().subtract(barData.getBarLow()) + ": DOWN BAR.");
				
				return 81;
			}
		}

		return 0;
	}
	
	/**
	 * Sprawdza, czy wolumen jest mniejszy od dwóch poprzednich barów.
	 * 
	 * @param prevPrevBar wartości bara 2 wstecz
	 * @param prevBar wartości poprzedniego bara
	 * @param actualBar wartości aktualnego bara
	 * @return
	 */
	private boolean isLessThenLast2(BarStatsData prevPrevBar, BarStatsData prevBar, BarData actualBar) {
		// czy wolumen jest mniejszy od dwóch poprzednich:
		if (prevPrevBar.getBarVolume().intValue() > prevBar.getBarVolume().intValue()
				&& prevBar.getBarVolume().intValue() > actualBar.getBarVolume().intValue()) {
			LOGGER.info("   [VOLUME] " + prevPrevBar.getBarVolume() + "," + prevBar.getBarVolume() + ","
					+ actualBar.getBarVolume() + ", data aktualnego="
					+ DateConverter.dateToString(actualBar.getBarTime(), "yy/MM/dd HH:mm") + ".");
			
			return true;
		}
		
		return false;
	}

	private boolean isDownBar(BarData actualBar, BarStatsData prevBar) {
		if (actualBar.getBarClose().compareTo(prevBar.getBarClose()) < 0)
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

	private boolean isUpBar(BarData actualBar, BarStatsData prevBar) {
		if (actualBar.getBarClose().compareTo(prevBar.getBarClose()) > 0)
			return true;
		else
			return false;
	}

}
