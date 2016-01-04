package pl.com.vsadga.data;

import java.io.Serializable;

public class TimeFrame implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = 6211103169433445486L;

	private Integer id;

	private Boolean isActive;

	private Integer timeFrame;

	private String timeFrameDesc;

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
		TimeFrame other = (TimeFrame) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isActive == null) {
			if (other.isActive != null)
				return false;
		} else if (!isActive.equals(other.isActive))
			return false;
		if (timeFrame == null) {
			if (other.timeFrame != null)
				return false;
		} else if (!timeFrame.equals(other.timeFrame))
			return false;
		if (timeFrameDesc == null) {
			if (other.timeFrameDesc != null)
				return false;
		} else if (!timeFrameDesc.equals(other.timeFrameDesc))
			return false;
		return true;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @return the timeFrame
	 */
	public Integer getTimeFrame() {
		return timeFrame;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((timeFrame == null) ? 0 : timeFrame.hashCode());
		result = prime * result + ((timeFrameDesc == null) ? 0 : timeFrameDesc.hashCode());
		return result;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @param timeFrame
	 *            the timeFrame to set
	 */
	public void setTimeFrame(Integer timeFrame) {
		this.timeFrame = timeFrame;
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
		return "TimeFrameList [id=" + id + ", timeFrame=" + timeFrame + ", timeFrameDesc=" + timeFrameDesc
				+ ", isActive=" + isActive + "]";
	}

}
