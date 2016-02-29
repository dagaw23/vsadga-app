package pl.com.vsadga.service.process.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.VolumeThermometer;
import pl.com.vsadga.dto.VolumeType;
import pl.com.vsadga.service.BaseServiceException;
import pl.com.vsadga.service.process.VolumeProcessor;
import pl.com.vsadga.utils.DateConverter;

public class VolumeProcessorImpl implements VolumeProcessor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeProcessorImpl.class);
	
	private BigDecimal prevBarClose;
	
	private VolumeThermometer upBarVolume;
	
	private VolumeThermometer downBarVolume;
	
	@Override
	public String checkVolumeThermometer(BarData actualBar) throws BaseServiceException {
		if (prevBarClose == null) {
			prevBarClose = actualBar.getBarClose();
			LOGGER.info("   [VOL] Brak zamkniecia poprzedniego bara [" + prevBarClose + "] wg bara [" + DateConverter.dateToString(actualBar.getBarTime(), "yy/MM/dd HH:mm") + "].");
			return "N";
		}
		
		// weryfikacja UP/DOWN bara:
		if (actualBar.getBarClose().compareTo(prevBarClose) < 0)
			downBarVolume.writeVolumeThermometer(actualBar.getBarTime(), actualBar.getBarVolume());
		else if (actualBar.getBarClose().compareTo(prevBarClose) > 0)
			upBarVolume.writeVolumeThermometer(actualBar.getBarTime(), actualBar.getBarVolume());
		else
			return "L";
		
		// jeśli którykolwiek nieokreślony - zakończ z nieokreślonym:
		if (downBarVolume.getActualVolumeType() == VolumeType.UNDEF_VOLUME || upBarVolume.getActualVolumeType() == VolumeType.UNDEF_VOLUME)
			return "N";
		
		// weryfikacja trendu wolumenu:
		if (downBarVolume.getActualVolumeType() == VolumeType.INCR_VOLUME) {
			if (upBarVolume.getActualVolumeType() == VolumeType.DECR_VOLUME)
				return "D";
			else if (upBarVolume.getActualVolumeType() == VolumeType.SIDE_VOLUME)
				return "S";
			else
				return "S";
		} else if (downBarVolume.getActualVolumeType() == VolumeType.DECR_VOLUME) {
			if (upBarVolume.getActualVolumeType() == VolumeType.INCR_VOLUME)
				return "U";
			else if (upBarVolume.getActualVolumeType() == VolumeType.SIDE_VOLUME)
				return "S";
			else
				return "S";
		} else
			return "S";
	}

}
