package pl.com.vsadga.web.dto;

public class FrameDto {

	private String timeFrameDesc;

	private Integer timeFrameId;

	public FrameDto() {
		super();
	}

	public FrameDto(Integer timeFrameId, String timeFrameDesc) {
		super();
		this.timeFrameId = timeFrameId;
		this.timeFrameDesc = timeFrameDesc;
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
		FrameDto other = (FrameDto) obj;
		if (timeFrameDesc == null) {
			if (other.timeFrameDesc != null)
				return false;
		} else if (!timeFrameDesc.equals(other.timeFrameDesc))
			return false;
		if (timeFrameId == null) {
			if (other.timeFrameId != null)
				return false;
		} else if (!timeFrameId.equals(other.timeFrameId))
			return false;
		return true;
	}

	/**
	 * @return the timeFrameDesc
	 */
	public String getTimeFrameDesc() {
		return timeFrameDesc;
	}

	/**
	 * @return the timeFrameId
	 */
	public Integer getTimeFrameId() {
		return timeFrameId;
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
		result = prime * result + ((timeFrameDesc == null) ? 0 : timeFrameDesc.hashCode());
		result = prime * result + ((timeFrameId == null) ? 0 : timeFrameId.hashCode());
		return result;
	}

	/**
	 * @param timeFrameDesc
	 *            the timeFrameDesc to set
	 */
	public void setTimeFrameDesc(String timeFrameDesc) {
		this.timeFrameDesc = timeFrameDesc;
	}

	/**
	 * @param timeFrameId
	 *            the timeFrameId to set
	 */
	public void setTimeFrameId(Integer timeFrameId) {
		this.timeFrameId = timeFrameId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FrameDto [timeFrameId=" + timeFrameId + ", timeFrameDesc=" + timeFrameDesc + "]";
	}

}
