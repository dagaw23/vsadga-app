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

	public IndicatorProcessorImpl() {
		super();
	}

	@Override
	public IndicatorInfo getDataIndicator(BarData barData, String frameDesc) throws BaseServiceException {
		if (!isProcessIndicator()) {
			LOGGER.info("   [INDY] Usluga przetwarzania wskaznika jest wylaczona.");
			return null;
		}

		// zapisz informację o barze do mapy:
		return getActualBarIndicator(barData);
	}

	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	/**
	 * @param indicatorData
	 *            the indicatorData to set
	 */
	public void setIndicatorData(IndicatorData indicatorData) {
		this.indicatorData = indicatorData;
	}

	private IndicatorInfo getActualBarIndicator(BarData barData) {
		// TODO 1: dodac wpisywanie czasu bara do BarStatsData
		// TODO 2: parametry konfigurayjne do pobierania liczby barów do przetworzenia oraz liczby
		// barów w mapie.
		// pobierz 2 poprzednie bary:
		BarStatsData last_bar = indicatorData.getLastBarData();
		BarStatsData prev_bar = indicatorData.getPreviousBar(1);
		
		if (last_bar == null || prev_bar == null) {
			LOGGER.info("   [INDY] Not ready yet.");
			return new IndicatorInfo(0, false);
		}
			

		// no-demand/no-supply
		if (isLessThenLast2(prev_bar, last_bar, barData)) {
			if (isUpBar(barData, last_bar)) {
				return new IndicatorInfo(6, true);
			}

			if (isDownBar(barData, last_bar)) {
				return new IndicatorInfo(81, true);
			}
		}

		return new IndicatorInfo(0, true);
	}

	private boolean isDownBar(BarData actualBar, BarStatsData prevBar) {
		if (actualBar.getBarClose().compareTo(prevBar.getBarClose()) < 0)
			return true;
		else
			return false;
	}

	/**
	 * Sprawdza, czy wolumen jest mniejszy od dwóch poprzednich barów.
	 * 
	 * @param prevPrevBar
	 *            wartości bara 2 wstecz
	 * @param prevBar
	 *            wartości poprzedniego bara
	 * @param actualBar
	 *            wartości aktualnego bara
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
