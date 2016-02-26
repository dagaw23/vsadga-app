package pl.com.vsadga.service.process.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.IndicatorData;
import pl.com.vsadga.dto.IndicatorInfo;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.IndicatorProcessor;
import pl.com.vsadga.utils.DateConverter;

public class IndicatorProcessorImpl implements IndicatorProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorProcessorImpl.class);

	private IndicatorData indicatorData;

	public IndicatorProcessorImpl() {
		super();
	}

	@Override
	public void addIndicatorData(BarData barData) throws BaseServiceException {
		indicatorData.addBarData2Map(barData);
	}

	@Override
	public void addIndicatorData(BarData barData, Boolean isBarToConfirmation) throws BaseServiceException {
		indicatorData.addBarData2Map(barData, isBarToConfirmation);
	}

	@Override
	public IndicatorInfo getDataIndicator(BarData barData, String frameDesc) throws BaseServiceException {

		// jeśli mapa dla wskaźnika nie została wyliczona - tylko wpisz Bar do kolekcji:
		if (!indicatorData.isReadyIndicatorMap()) {
			indicatorData.addBarData2Map(barData);

			return new IndicatorInfo(false);
		}

		// wyliczenie wskaźnika:
		// TODO 1: Dodać sprawdzenie czy poprzedni bar do potwierdzenia

		// TODO

		// zapisz informację o barze do mapy:
		indicatorData.addBarData2Map(barData);

		return new IndicatorInfo(0, true);
	}

	/**
	 * @param indicatorData
	 *            the indicatorData to set
	 */
	public void setIndicatorData(IndicatorData indicatorData) {
		this.indicatorData = indicatorData;
	}

	private Integer getActualBarIndicator(BarData barData) {
		Integer result = null;

		// TODO 1: dodac wpisywanie czasu bara do BarStatsData
		// TODO 2: parametry konfigurayjne do pobierania liczby barów do przetworzenia oraz liczby
		// barów w mapie.
		// pobierz 2 poprzednie bary:
		BarStatsData prev_bar = indicatorData.getPrevBar();
		BarStatsData prev_prev_bar = indicatorData.getPrevPrevBar();

		// czy wolumen jest mniejszy od dwóch poprzednich:
		if (prev_prev_bar.getBarVolume().intValue() > prev_bar.getBarVolume().intValue()
				&& prev_bar.getBarVolume().intValue() > barData.getBarVolume().intValue()) {
			LOGGER.info("   [VOLUME] " + prev_prev_bar.getBarVolume() + "," + prev_bar.getBarVolume() + ","
					+ barData.getBarVolume() + ", data aktualnego="
					+ DateConverter.dateToString(barData.getBarTime(), "yy/MM/dd HH:mm") + ".");

			if (isUpBar(barData, prev_bar))
				LOGGER.info("SPREAD AVG:" + indicatorData.getSpreadAvg() + ",ACT SPREAD:"
						+ barData.getBarHigh().subtract(barData.getBarLow()) + ": UP BAR.");

			if (isDownBar(barData, prev_bar))
				LOGGER.info("SPREAD AVG:" + indicatorData.getSpreadAvg() + ",ACT SPREAD:"
						+ barData.getBarHigh().subtract(barData.getBarLow()) + ": DOWN BAR.");
		}

		// czy aktualny UP BAR:

		return result;
	}

	private boolean isDownBar(BarData actualBar, BarStatsData prevBar) {
		if (actualBar.getBarClose().compareTo(prevBar.getBarClose()) < 0)
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
