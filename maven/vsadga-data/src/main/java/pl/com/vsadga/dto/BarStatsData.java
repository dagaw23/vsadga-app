package pl.com.vsadga.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class BarStatsData implements Serializable {

	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = 6553224810742540559L;

	private BigDecimal barClose;

	private BigDecimal barSpread;

	private Integer barVolume;

	private String trend;

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
		if (trend == null) {
			if (other.trend != null)
				return false;
		} else if (!trend.equals(other.trend))
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
	 * @return the trend
	 */
	public String getTrend() {
		return trend;
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
		result = prime * result + ((trend == null) ? 0 : trend.hashCode());
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
	 * @param trend
	 *            the trend to set
	 */
	public void setTrend(String trend) {
		this.trend = trend;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BarStatsData [barClose=" + barClose + ", barSpread=" + barSpread + ", barVolume=" + barVolume
				+ ", trend=" + trend + "]";
	}

}
