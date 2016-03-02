package pl.com.vsadga.dto.process;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dto.VolumeType;

public class VolumeThermometer {

	private static final Logger LOGGER = LoggerFactory.getLogger(VolumeThermometer.class);

	/**
	 * ostatnio zwrócony trend wolumenu
	 */
	private VolumeType actualVolumeType;

	/**
	 * data 1-go bara w kolejności
	 */
	private Date barTime1;

	/**
	 * data 2-go bara w kolejności
	 */
	private Date barTime2;

	/**
	 * wolumen 1-go bara w kolejności
	 */
	private Integer barVolume1;

	/**
	 * wolumen 2-go bara w kolejności
	 */
	private Integer barVolume2;

	/**
	 * @return the actualVolumeType
	 */
	public VolumeType getActualVolumeType() {
		return actualVolumeType;
	}

	public void writeVolumeThermometer(Date barTime, Integer barVolume) {
		VolumeType result = null;

		// czy już można wyliczyć trend wolumenu:
		if (!isReadyToCheck(barTime, barVolume))
			return;

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

		LOGGER.info("   [VOL] Wolumeny [" + barVolume1 + "," + barVolume2 + "," + barVolume + "], result ["
				+ result + "] dla [" + getDateDesc(barTime) + "].");

		addNextBar(barTime, barVolume, result);
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
	private void addNextBar(Date barTime, Integer barVolume, VolumeType actVolType) {
		this.barTime1 = this.barTime2;
		this.barVolume1 = this.barVolume2;

		this.barTime2 = barTime;
		this.barVolume2 = barVolume;
		this.actualVolumeType = actVolType;
	}

	private String getDateDesc(Date inputDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");

		return sdf.format(inputDate);
	}

	/**
	 * Sprawdza, czy informacja o dwóch poprzednich barach została już zapisana w zmiennych.
	 * 
	 * @return
	 */
	private boolean isReadyToCheck(Date barTime, Integer barVolume) {
		if (barVolume1 == null || barTime1 == null) {
			barVolume1 = barVolume;
			barTime1 = barTime;
			actualVolumeType = VolumeType.UNDEF_VOLUME;

			return false;
		}

		if (barVolume2 == null || barTime2 == null) {
			barVolume2 = barVolume;
			barTime2 = barTime;
			actualVolumeType = VolumeType.UNDEF_VOLUME;

			return false;
		}

		return true;
	}

}
