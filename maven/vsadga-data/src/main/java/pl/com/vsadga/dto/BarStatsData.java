package pl.com.vsadga.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class BarStatsData implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = 6553224810742540559L;

	/**
	 * cena zamknięcia
	 */
	private BigDecimal barClose;

	/**
	 * cena maksymalna w trakcie bara
	 */
	private BigDecimal barHigh;

	/**
	 * cena minimalna w trakcie bara
	 */
	private BigDecimal barLow;

	/**
	 * spread bara
	 */
	// private BigDecimal barSpread;

	/**
	 * UP, DOWN, LEVEL bar
	 */
	private BarType barType;

	/**
	 * wolumen bara
	 */
	private Integer barVolume;

	/**
	 * ID rekordu
	 */
	private Integer id;

	/**
	 * wyliczona średnia krocząca
	 */
	private BigDecimal imaCount;

	/**
	 * wskaźnik trendu
	 */
	private String trendIndicator;

	/**
	 * waga trendu
	 */
	private Integer trendWeight;

	/**
	 * wolumen absorbcyjny - dla ciągu barów
	 */
	private Integer volumeAbsorb;

	/**
	 * wskaźnik wolumenu
	 */
	private String volumeThermometer;

	public BarStatsData() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BarStatsData other = (BarStatsData) obj;
		if (barClose == null) {
			if (other.barClose != null)
				return false;
		} else if (!barClose.equals(other.barClose))
			return false;
		if (barHigh == null) {
			if (other.barHigh != null)
				return false;
		} else if (!barHigh.equals(other.barHigh))
			return false;
		if (barLow == null) {
			if (other.barLow != null)
				return false;
		} else if (!barLow.equals(other.barLow))
			return false;
		if (barType != other.barType)
			return false;
		if (barVolume == null) {
			if (other.barVolume != null)
				return false;
		} else if (!barVolume.equals(other.barVolume))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (imaCount == null) {
			if (other.imaCount != null)
				return false;
		} else if (!imaCount.equals(other.imaCount))
			return false;
		if (trendIndicator == null) {
			if (other.trendIndicator != null)
				return false;
		} else if (!trendIndicator.equals(other.trendIndicator))
			return false;
		if (trendWeight == null) {
			if (other.trendWeight != null)
				return false;
		} else if (!trendWeight.equals(other.trendWeight))
			return false;
		if (volumeAbsorb == null) {
			if (other.volumeAbsorb != null)
				return false;
		} else if (!volumeAbsorb.equals(other.volumeAbsorb))
			return false;
		if (volumeThermometer == null) {
			if (other.volumeThermometer != null)
				return false;
		} else if (!volumeThermometer.equals(other.volumeThermometer))
			return false;
		return true;
	}

	/**
	 * @return the barClose
	 */
	public BigDecimal getBarClose() {
		return barClose;
	}

	/**
	 * @return the barHigh
	 */
	public BigDecimal getBarHigh() {
		return barHigh;
	}

	/**
	 * @return the barLow
	 */
	public BigDecimal getBarLow() {
		return barLow;
	}

	/**
	 * @return the barType
	 */
	public BarType getBarType() {
		return barType;
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the imaCount
	 */
	public BigDecimal getImaCount() {
		return imaCount;
	}

	/**
	 * @return the trendIndicator
	 */
	public String getTrendIndicator() {
		return trendIndicator;
	}

	/**
	 * @return the trendWeight
	 */
	public Integer getTrendWeight() {
		return trendWeight;
	}

	/**
	 * @return the volumeAbsorb
	 */
	public Integer getVolumeAbsorb() {
		return volumeAbsorb;
	}

	/**
	 * @return the volumeThermometer
	 */
	public String getVolumeThermometer() {
		return volumeThermometer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barClose == null) ? 0 : barClose.hashCode());
		result = prime * result + ((barHigh == null) ? 0 : barHigh.hashCode());
		result = prime * result + ((barLow == null) ? 0 : barLow.hashCode());
		result = prime * result + ((barType == null) ? 0 : barType.hashCode());
		result = prime * result + ((barVolume == null) ? 0 : barVolume.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imaCount == null) ? 0 : imaCount.hashCode());
		result = prime * result + ((trendIndicator == null) ? 0 : trendIndicator.hashCode());
		result = prime * result + ((trendWeight == null) ? 0 : trendWeight.hashCode());
		result = prime * result + ((volumeAbsorb == null) ? 0 : volumeAbsorb.hashCode());
		result = prime * result + ((volumeThermometer == null) ? 0 : volumeThermometer.hashCode());
		return result;
	}

	/**
	 * @param barClose
	 *            the barClose to set
	 */
	public void setBarClose(BigDecimal barClose) {
		this.barClose = barClose;
	}

	/**
	 * @param barHigh
	 *            the barHigh to set
	 */
	public void setBarHigh(BigDecimal barHigh) {
		this.barHigh = barHigh;
	}

	/**
	 * @param barLow
	 *            the barLow to set
	 */
	public void setBarLow(BigDecimal barLow) {
		this.barLow = barLow;
	}

	/**
	 * @param barType
	 *            the barType to set
	 */
	public void setBarType(BarType barType) {
		this.barType = barType;
	}

	/**
	 * @param barVolume
	 *            the barVolume to set
	 */
	public void setBarVolume(Integer barVolume) {
		this.barVolume = barVolume;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param imaCount
	 *            the imaCount to set
	 */
	public void setImaCount(BigDecimal imaCount) {
		this.imaCount = imaCount;
	}

	/**
	 * @param trendIndicator
	 *            the trendIndicator to set
	 */
	public void setTrendIndicator(String trendIndicator) {
		this.trendIndicator = trendIndicator;
	}

	/**
	 * @param trendWeight
	 *            the trendWeight to set
	 */
	public void setTrendWeight(Integer trendWeight) {
		this.trendWeight = trendWeight;
	}

	/**
	 * @param volumeAbsorb
	 *            the volumeAbsorb to set
	 */
	public void setVolumeAbsorb(Integer volumeAbsorb) {
		this.volumeAbsorb = volumeAbsorb;
	}

	/**
	 * @param volumeThermometer
	 *            the volumeThermometer to set
	 */
	public void setVolumeThermometer(String volumeThermometer) {
		this.volumeThermometer = volumeThermometer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BarStatsData [barClose=" + barClose + ", barHigh=" + barHigh + ", barLow=" + barLow + ", barType="
				+ barType + ", barVolume=" + barVolume + ", id=" + id + ", imaCount=" + imaCount
				+ ", trendIndicator=" + trendIndicator + ", trendWeight=" + trendWeight + ", volumeAbsorb="
				+ volumeAbsorb + ", volumeThermometer=" + volumeThermometer + "]";
	}

}
