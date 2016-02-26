package pl.com.vsadga.service.process.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.BarStatsData;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.dto.IndicatorData;
import pl.com.vsadga.dto.TrendParams;
import pl.com.vsadga.dto.VolumeThermometer;
import pl.com.vsadga.dto.VolumeType;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.TrendProcessor;

public class TrendProcessorImpl implements TrendProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrendProcessorImpl.class);

	private IndicatorData indicatorData;
	
	private VolumeThermometer upBarVolThrm;
	
	private VolumeThermometer downBarVolThrm;
	
	@Override
	public TrendParams getActualTrend(BarData barData) throws BaseServiceException {

		// pobierz informację o poprzednim barze:
		BarStatsData prev_bar = indicatorData.getPrevBar();

		// brak jeszcze barów do wyliczenia:
		if (prev_bar == null)
			return new TrendParams("S", 0, "S");
		
		// pobierz
		String vol_trend = getVolumeTrend(prev_bar, barData);

		// porównaj AKTUALNY bar - z poprzednim (średnia krocząca):
		int bar_compare = barData.getImaCount().compareTo(prev_bar.getImaCount());
		// LOGGER.info("   [TREND] ACT [" + barData.getImaCount() + "]:"
		// + DateConverter.dateToString(barData.getBarTime(), "yy/MM/dd HH:mm") + ", PREV ["
		// + prev_bar.getImaCount() + "], wynik=" + bar_compare + ".");

		// trend w poprzednim barze:
		String trend = prev_bar.getTrendIndicator();
		int weight = prev_bar.getTrendWeight().intValue();

		// *** poprzedni bar to UPTREND ***
		if (trend.equals("U")) {
			if (bar_compare > 0) {
				// kontynuacja UP
				if (weight == 6 || weight == 7)
					return new TrendParams("U", (weight + 1), vol_trend);
				else
					return new TrendParams("U", weight, vol_trend);
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return new TrendParams(trend, weight, vol_trend);
			} else {
				// zmiana na DOWN
				if (weight == 6)
					return new TrendParams("S", 1, vol_trend);
				else
					return new TrendParams("U", 6, vol_trend);
			}
		}
		// *** poprzedni bar to DOWNTREND ***
		else if (trend.equals("D")) {
			if (bar_compare < 0) {
				// kontynuacja DOWN
				if (weight == 1 || weight == 2)
					return new TrendParams("D", (weight + 1), vol_trend);
				else
					return new TrendParams("D", weight, vol_trend);
			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return new TrendParams(trend, weight, vol_trend);
			} else {
				// zmiana na UP
				if (weight == 1)
					return new TrendParams("S", 6, vol_trend);
				else
					return new TrendParams("D", 1, vol_trend);
			}
		}
		// *** poprzedni bar jest BOCZNY ***
		else if (trend.equals("S")) {
			if (bar_compare > 0) {
				// bar UP
				if (weight == 6)
					return new TrendParams("S", 7, vol_trend);
				else if (weight == 7)
					return new TrendParams("U", 8, vol_trend);
				else if (weight == 0 || weight == 1 || weight == 2)
					return new TrendParams("S", 6, vol_trend);
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					return new TrendParams("S", 0, vol_trend);
				}

			} else if (bar_compare == 0) {
				// bez zmiany w średniej:
				return new TrendParams(trend, weight, vol_trend);
			} else {
				// bar DOWN
				if (weight == 1)
					return new TrendParams("S", 2, vol_trend);
				else if (weight == 2)
					return new TrendParams("D", 3, vol_trend);
				else if (weight == 0 || weight == 6 || weight == 7)
					return new TrendParams("S", 1, vol_trend);
				else {
					LOGGER.info("   [ERROR] Unexpected PREV [" + trend + "," + weight + "," + bar_compare
							+ "], aktualny bar [" + barData + "].");
					return new TrendParams("S", 0, vol_trend);
				}
			}
		} else {
			LOGGER.info("   [ERROR] Unexpected situation [" + trend + "," + weight + "," + bar_compare
					+ "], aktualny bar [" + barData + "].");
			return new TrendParams("S", 0, vol_trend);
		}
	}

	/**
	 * @param indicatorData
	 *            the indicatorData to set
	 */
	public void setIndicatorData(IndicatorData indicatorData) {
		this.indicatorData = indicatorData;
	}

	@Override
	public void clearVolumeData() {
		downBarVolThrm = new VolumeThermometer();
		upBarVolThrm = new VolumeThermometer();
	}
	
	private BarType getBarType(BarStatsData prevData, BarData barData) {
		int bar_comp = barData.getBarClose().compareTo(prevData.getBarClose());
		
		if (bar_comp < 0)
			return BarType.DOWN_BAR;
		else if (bar_comp > 0)
			return BarType.UP_BAR;
		else
			return BarType.LEVEL_BAR;
	}
	
	private String getVolumeTrend(BarStatsData prevData, BarData barData) {
		VolumeType up_vol = null;
		VolumeType down_vol = null;
		
		// pobierz typ bara:
		BarType bar_typ = getBarType(prevData, barData);
		
		if (bar_typ == BarType.UP_BAR) {
			up_vol = upBarVolThrm.getVolumeThermometer(barData.getBarTime(), barData.getBarVolume());
			down_vol = downBarVolThrm.getLastVolumeType();
			
		} else if (bar_typ == BarType.DOWN_BAR) {
			down_vol = downBarVolThrm.getVolumeThermometer(barData.getBarTime(), barData.getBarVolume());
			up_vol = upBarVolThrm.getLastVolumeType();
			
		} else {
			// nie zapisujemy LEVEL bara - do parametrów UP/DOWN bara:
			return "L";
		}
		
		// jeśli którykolwiek UP/DOWN nie jest określony:
		if (up_vol == VolumeType.UNDEF_VOLUME || down_vol == VolumeType.UNDEF_VOLUME)
			return "N";
		
		if (up_vol == VolumeType.INCR_VOLUME && down_vol == VolumeType.DECR_VOLUME)
			return "U";
		else if (up_vol == VolumeType.DECR_VOLUME && down_vol == VolumeType.INCR_VOLUME)
			return "D";
		else 
			return "S";
	}

}
