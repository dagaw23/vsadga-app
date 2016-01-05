package pl.com.vsadga.data;

import java.io.Serializable;
import java.math.BigDecimal;

public class BarData implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = -404888175506948188L;

	private BigDecimal barClose;

	private BigDecimal barHigh;

	private BigDecimal barLow;

	private Long barTime;

	private Integer barVolume;

	private Integer id;

	private BigDecimal imaCount;

	private Integer symbolId;

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
		BarData other = (BarData) obj;
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
		if (barTime == null) {
			if (other.barTime != null)
				return false;
		} else if (!barTime.equals(other.barTime))
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
		if (symbolId == null) {
			if (other.symbolId != null)
				return false;
		} else if (!symbolId.equals(other.symbolId))
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
	 * @return the barTime
	 */
	public Long getBarTime() {
		return barTime;
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
	 * @return the symbolId
	 */
	public Integer getSymbolId() {
		return symbolId;
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
		result = prime * result + ((barTime == null) ? 0 : barTime.hashCode());
		result = prime * result + ((barVolume == null) ? 0 : barVolume.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imaCount == null) ? 0 : imaCount.hashCode());
		result = prime * result + ((symbolId == null) ? 0 : symbolId.hashCode());
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
	 * @param barTime
	 *            the barTime to set
	 */
	public void setBarTime(Long barTime) {
		this.barTime = barTime;
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
	 * @param symbolId
	 *            the symbolId to set
	 */
	public void setSymbolId(Integer symbolId) {
		this.symbolId = symbolId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BarData [barClose=" + barClose + ", barHigh=" + barHigh + ", barLow=" + barLow + ", barTime="
				+ barTime + ", barVolume=" + barVolume + ", id=" + id + ", imaCount=" + imaCount + ", symbolId="
				+ symbolId + "]";
	}

}
