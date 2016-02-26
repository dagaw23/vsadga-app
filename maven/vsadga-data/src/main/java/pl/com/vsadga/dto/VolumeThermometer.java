package pl.com.vsadga.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VolumeThermometer {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeThermometer.class);

	/**
	 * czas 1-go bara w kolejności
	 */
	private Date barTime1;

	/**
	 * czas 2-go bara w kolejności
	 */
	private Date barTime2;

	/**
	 * ilość ticków w trakcie 1-go bara w kolejności
	 */
	private Integer barVolume1;

	/**
	 * ilość ticków w trakcie 2-go bara w kolejności
	 */
	private Integer barVolume2;
	
	/**
	 * ostatnio zwrócony trend wolumenu
	 */
	private VolumeType lastVolumeType;

	public VolumeType getVolumeThermometer(Date barTime, Integer barVolume) {
		VolumeType result = null;
		
		// czy już można wyliczyć trend wolumenu:
		if (!isReadyToCheck(barTime, barVolume)) {
			this.lastVolumeType = VolumeType.UNDEF_VOLUME;
			return VolumeType.UNDEF_VOLUME;
		}
		
		// UP poprzednich:
		if (barVolume1.intValue() < barVolume2.intValue()) {
			// aktualny bar:
			if (barVolume2.intValue() <= barVolume.intValue()) {
				result = VolumeType.INCR_VOLUME;
			} else {
				// jeszcze porównanie z poprzednim:
				if (barVolume1.intValue() <= barVolume.intValue())
					result = VolumeType.INCR_VOLUME;
				else
					result = VolumeType.SIDE_VOLUME;
			}
		}
		// DOWN poprzednich:
		else if (barVolume1.intValue() > barVolume2.intValue()) {
			// aktualny bar:
			if (barVolume2.intValue() >= barVolume.intValue()) {
				result = VolumeType.DECR_VOLUME;
			} else {
				// jeszcze porównanie z poprzednim:
				if (barVolume1.intValue() >= barVolume.intValue())
					result = VolumeType.DECR_VOLUME;
				else
					result = VolumeType.SIDE_VOLUME;
			}
		}
		// wolumen taki sam w poprzednich:
		else {
			// aktualny bar:
			if (barVolume2.intValue() < barVolume.intValue())
				result = VolumeType.INCR_VOLUME;
			else if (barVolume2.intValue() > barVolume.intValue())
				result = VolumeType.DECR_VOLUME;
			else
				result = VolumeType.SIDE_VOLUME;
		}

		LOGGER.info("   [VOL] Wolumeny [" + barVolume1 + "," + barVolume2 + "," + barVolume
				+ "], result [" + result + "] dla [" + getDateDesc(barTime) + "].");
		this.lastVolumeType = result;
		addNextBar(barTime, barVolume);

		return result;
	}
	
	/**
	 * @return the lastVolumeType
	 */
	public VolumeType getLastVolumeType() {
		return lastVolumeType;
	}

	/**
	 * Dodaje informację o następnym barze - przepisując poprzedni bar z pozycji 2 do pozycji 1 -
	 * oraz wpisując nowy bar do pozycji nr 2.
	 * 
	 * @param barTime
	 *            czas bara
	 * @param barVolume
	 *            ilość ticków w barze
	 */
	private void addNextBar(Date barTime, Integer barVolume) {
		this.barTime1 = this.barTime2;
		this.barTime2 = barTime;

		this.barVolume1 = this.barVolume2;
		this.barVolume2 = barVolume;
	}
	
	/**
	 * Sprawdza, czy informacja o dwóch poprzednich barach została już zapisana w zmiennych.
	 * 
	 * @return
	 */
	private boolean isReadyToCheck(Date barTime, Integer barVolume) {
		if (this.barVolume1 == null) {
			this.barVolume1 = barVolume;
			this.barTime1 = barTime;
			
			return false;
		}
		
		if (this.barVolume2 == null) {
			this.barVolume2 = barVolume;
			this.barTime2 = barTime;
			
			return false;
		}
		
		return true;
	}

	private String getDateDesc(Date inputDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");

		return sdf.format(inputDate);
	}
}
