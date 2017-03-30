package pl.com.vsadga.dto.alert;

import java.io.Serializable;

public class VolumeAlert implements Serializable {

	/**
	 * liczba barów do weryfikacji wstecz
	 */
	private Integer limitSize;

	/**
	 * nazwa ramki czasowej
	 */
	private String timeFrameDesc;

	/**
	 * Konstruktor bezparametrowy
	 */
	public VolumeAlert() {
	}

	/**
	 * @param timeFrameDesc
	 * @param limitSize
	 */
	public VolumeAlert(String timeFrameDesc, Integer limitSize) {
		super();
		this.timeFrameDesc = timeFrameDesc;
		this.limitSize = limitSize;
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
		VolumeAlert other = (VolumeAlert) obj;
		if (limitSize == null) {
			if (other.limitSize != null)
				return false;
		} else if (!limitSize.equals(other.limitSize))
			return false;
		if (timeFrameDesc == null) {
			if (other.timeFrameDesc != null)
				return false;
		} else if (!timeFrameDesc.equals(other.timeFrameDesc))
			return false;
		return true;
	}

	/**
	 * @return the limitSize
	 */
	public Integer getLimitSize() {
		return limitSize;
	}

	/**
	 * @return the timeFrameDesc
	 */
	public String getTimeFrameDesc() {
		return timeFrameDesc;
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
		result = prime * result + ((limitSize == null) ? 0 : limitSize.hashCode());
		result = prime * result + ((timeFrameDesc == null) ? 0 : timeFrameDesc.hashCode());
		return result;
	}

	/**
	 * @param limitSize
	 *            the limitSize to set
	 */
	public void setLimitSize(Integer limitSize) {
		this.limitSize = limitSize;
	}

	/**
	 * @param timeFrameDesc
	 *            the timeFrameDesc to set
	 */
	public void setTimeFrameDesc(String timeFrameDesc) {
		this.timeFrameDesc = timeFrameDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VolumeAlert [limitSize=" + limitSize + ", timeFrameDesc=" + timeFrameDesc + "]";
	}

}
