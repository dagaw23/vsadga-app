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
	 * spread bara
	 */
	private BigDecimal barSpread;

	/**
	 * wolumen bara
	 */
	private Integer barVolume;

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
		if (barSpread == null) {
			if (other.barSpread != null)
				return false;
		} else if (!barSpread.equals(other.barSpread))
			return false;
		if (barVolume == null) {
			if (other.barVolume != null)
				return false;
		} else if (!barVolume.equals(other.barVolume))
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
	 * @return the barSpread
	 */
	public BigDecimal getBarSpread() {
		return barSpread;
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
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
		result = prime * result + ((barSpread == null) ? 0 : barSpread.hashCode());
		result = prime * result + ((barVolume == null) ? 0 : barVolume.hashCode());
		result = prime * result + ((imaCount == null) ? 0 : imaCount.hashCode());
		result = prime * result + ((trendIndicator == null) ? 0 : trendIndicator.hashCode());
		result = prime * result + ((trendWeight == null) ? 0 : trendWeight.hashCode());
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
	 * @param barSpread
	 *            the barSpread to set
	 */
	public void setBarSpread(BigDecimal barSpread) {
		this.barSpread = barSpread;
	}

	/**
	 * @param barVolume
	 *            the barVolume to set
	 */
	public void setBarVolume(Integer barVolume) {
		this.barVolume = barVolume;
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
		return "BarStatsData [barClose=" + barClose + ", barSpread=" + barSpread + ", barVolume=" + barVolume
				+ ", imaCount=" + imaCount + ", trendIndicator=" + trendIndicator + ", trendWeight=" + trendWeight
				+ ", volumeThermometer=" + volumeThermometer + "]";
	}

}
