package pl.com.vsadga.service.process.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.VolumeType;
import pl.com.vsadga.dto.process.VolumeThermometer;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.config.ConfigDataService;
import pl.com.vsadga.service.process.VolumeProcessor;
import pl.com.vsadga.utils.DateConverter;

public class VolumeProcessorImpl implements VolumeProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);

	private ConfigDataService configDataService;

	private VolumeThermometer downBarVolume;

	/**
	 * cena zamknięcia w poprzednim barze
	 */
	private BigDecimal prevBarClose;

	private VolumeThermometer upBarVolume;

	@Override
	public int addVolumeThermometerData(BarData actualBar) throws BaseServiceException {
		if (prevBarClose == null) {
			prevBarClose = actualBar.getBarClose();
			LOGGER.info("   [VOL] Brak zamkniecia poprzedniego bara [" + prevBarClose + "] wg bara ["
					+ DateConverter.dateToString(actualBar.getBarTime(), "yy/MM/dd HH:mm") + "].");
			return 1;
		}

		int comp_val = actualBar.getBarClose().compareTo(prevBarClose);
		// LOGGER.info("   comp_val=" + comp_val);

		// weryfikacja UP/DOWN bara:
		if (comp_val < 0) {
			downBarVolume.writeVolumeThermometer(actualBar.getBarTime(), actualBar.getBarVolume());
			// LOGGER.info("   DOWN:" + downBarVolume.getPrevBarVolume() + "," +
			// downBarVolume.getBarVolume() + ","
			// + downBarVolume.getActualVolumeType());
		} else if (comp_val > 0) {
			upBarVolume.writeVolumeThermometer(actualBar.getBarTime(), actualBar.getBarVolume());
			// LOGGER.info("   UP:" + upBarVolume.getPrevBarVolume() + "," +
			// upBarVolume.getBarVolume() + ","
			// + upBarVolume.getActualVolumeType());
		} else {
			// LOGGER.info("   LEVEL");
			return 2;
		}

		// jeśli którykolwiek nieokreślony - zakończ z nieokreślonym:
		if (downBarVolume.getActualVolumeType() == VolumeType.UNDEF_VOLUME
				|| upBarVolume.getActualVolumeType() == VolumeType.UNDEF_VOLUME) {

			LOGGER.info("   NOT READY YET: DOWN=" + downBarVolume.getPrevBarVolume() + ","
					+ downBarVolume.getBarVolume() + "," + downBarVolume.getActualVolumeType() + ", UP="
					+ upBarVolume.getPrevBarVolume() + "," + upBarVolume.getBarVolume() + ","
					+ upBarVolume.getActualVolumeType());
			return 1;
		}

		return 0;
	}

	@Override
	public String checkVolumeThermometer(BarData actualBar) throws BaseServiceException {
		if (!isProcessVolume()) {
			LOGGER.info("   [TREND] Usluga przetwarzania trendu wolumenu jest wylaczona.");
			return null;
		}

		int add_vol = addVolumeThermometerData(actualBar);

		if (add_vol == 1)
			return "N";
		else if (add_vol == 2)
			return "L";

		// weryfikacja trendu wolumenu:
		if (downBarVolume.getActualVolumeType() == VolumeType.INCR_VOLUME) {
			if (upBarVolume.getActualVolumeType() == VolumeType.DECR_VOLUME)
				return "D";
			else if (upBarVolume.getActualVolumeType() == VolumeType.EQUAL_VOLUME) {
				int d_v = downBarVolume.getBarVolume().intValue();
				int u_v = upBarVolume.getBarVolume().intValue();
				if (d_v > u_v)
					return "D";
				else if (d_v < u_v)
					return "U";
				else
					return "S";
			} else {
				BigDecimal da_v = downBarVolume.getAverage();
				BigDecimal ua_v = upBarVolume.getAverage();

				if (da_v.compareTo(ua_v) < 0)
					return "U";
				else if (da_v.compareTo(ua_v) > 0)
					return "D";
				else
					return "S";
			}
		} else if (downBarVolume.getActualVolumeType() == VolumeType.DECR_VOLUME) {
			if (upBarVolume.getActualVolumeType() == VolumeType.INCR_VOLUME)
				return "U";
			else if (upBarVolume.getActualVolumeType() == VolumeType.EQUAL_VOLUME) {
				int d_v = downBarVolume.getBarVolume().intValue();
				int u_v = upBarVolume.getBarVolume().intValue();
				if (d_v > u_v)
					return "D";
				else if (d_v < u_v)
					return "U";
				else
					return "S";
			} else {
				BigDecimal da_v = downBarVolume.getAverage();
				BigDecimal ua_v = upBarVolume.getAverage();

				if (da_v.compareTo(ua_v) < 0)
					return "U";
				else if (da_v.compareTo(ua_v) > 0)
					return "D";
				else
					return "S";
			}
		} else {
			// *** DOWN bary EQUAL ***
			int d_v = downBarVolume.getBarVolume().intValue();
			int u_v = upBarVolume.getBarVolume().intValue();
			if (d_v > u_v)
				return "D";
			else if (d_v < u_v)
				return "U";
			else
				return "S";
		}
	}

	@Override
	public void clearProcessData() {
		this.prevBarClose = null;
		this.downBarVolume = new VolumeThermometer(VolumeType.UNDEF_VOLUME);
		this.upBarVolume = new VolumeThermometer(VolumeType.UNDEF_VOLUME);
	}

	/**
	 * @param configDataService
	 *            the configDataService to set
	 */
	public void setConfigDataService(ConfigDataService configDataService) {
		this.configDataService = configDataService;
	}

	private boolean isProcessVolume() throws BaseServiceException {
		String param_value = configDataService.getParam("IS_PROCESS_VOLUME");

		if (param_value == null || StringUtils.isBlank(param_value)) {
			LOGGER.info("   [TREND] Brak parametru IS_PROCESS_VOLUME [" + param_value + "] w tabeli CONFIG_DATA.");
			return false;
		}

		if (!StringUtils.isNumeric(param_value)) {
			LOGGER.info("   [TREND] Parametr IS_PROCESS_VOLUME [" + param_value + "] nie jest numeryczny.");
			return false;
		}

		int is_proc = Integer.valueOf(param_value);

		if (is_proc == 1)
			return true;
		else
			return false;
	}

}
