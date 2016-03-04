package pl.com.vsadga.dto.process;

import java.math.BigDecimal;
import java.math.BigInteger;
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
	 * data ostatniego bara
	 */
	private Date barTime;

	/**
	 * wolumen ostatniego bara
	 */
	private Integer barVolume;

	/**
	 * wolumen poprzedniego
	 */
	private Integer prevBarVolume;

	/**
	 * @return the actualVolumeType
	 */
	public VolumeType getActualVolumeType() {
		return actualVolumeType;
	}

	public VolumeThermometer(VolumeType actualVolumeType) {
		super();
		this.actualVolumeType = actualVolumeType;
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
	}
	
	public BigDecimal getAverage() {
		BigDecimal v_1 = BigDecimal.valueOf(getBarVolume());
		BigDecimal v_2 = BigDecimal.valueOf(getPrevBarVolume());
		
		return v_1.add(v_2).divide(BigDecimal.valueOf(2));
	}

	/**
	 * @return the prevBarVolume
	 */
	public Integer getPrevBarVolume() {
		return prevBarVolume;
	}

	public void writeVolumeThermometer(Date time, Integer volume) {
		VolumeType result = null;

		// czy już można wyliczyć trend wolumenu:
		if (!isReadyToCheck(time, volume))
			return;

		if (barVolume.intValue() < volume.intValue()) {
			result = VolumeType.INCR_VOLUME;
		} else if (barVolume.intValue() > volume.intValue()) {
			result = VolumeType.DECR_VOLUME;
		} else {
			result = VolumeType.EQUAL_VOLUME;
		}

		//LOGGER.info("   [VOL] Wolumeny [" + barVolume + "," + volume + "], result [" + result + "] dla ["
		//		+ getDateDesc(barTime) + "].");

		addNextBar(time, volume, result);
	}

	/**
	 * Dodaje informację o następnym barze - przepisując poprzedni bar z pozycji
	 * 2 do pozycji 1 - oraz wpisując nowy bar do pozycji nr 2.
	 * 
	 * @param barTime
	 *            czas bara
	 * @param barVolume
	 *            ilość ticków w barze
	 */
	private void addNextBar(Date time, Integer volume, VolumeType actVolType) {
		prevBarVolume = barVolume;
		actualVolumeType = actVolType;
		
		barTime = time;
		barVolume = volume;
	}

	private String getDateDesc(Date inputDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");

		return sdf.format(inputDate);
	}

	/**
	 * Sprawdza, czy informacja o dwóch poprzednich barach została już zapisana
	 * w zmiennych.
	 * 
	 * @return
	 */
	private boolean isReadyToCheck(Date time, Integer volume) {
		if (barVolume == null || barTime == null) {
			barVolume = volume;
			barTime = time;
			actualVolumeType = VolumeType.UNDEF_VOLUME;

			return false;
		}

		return true;
	}

}
