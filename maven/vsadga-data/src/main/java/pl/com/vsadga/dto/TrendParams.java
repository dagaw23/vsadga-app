package pl.com.vsadga.dto;

public class TrendParams {

	private String trendIndicator;

	private Integer trendWeight;

	private String volumeTrendIndicator;

	public TrendParams(String trendIndicator, Integer trendWeight, String volumeTrendIndicator) {
		super();
		this.trendIndicator = trendIndicator;
		this.trendWeight = trendWeight;
		this.volumeTrendIndicator = volumeTrendIndicator;
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
		TrendParams other = (TrendParams) obj;
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
		if (volumeTrendIndicator == null) {
			if (other.volumeTrendIndicator != null)
				return false;
		} else if (!volumeTrendIndicator.equals(other.volumeTrendIndicator))
			return false;
		return true;
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
	 * @return the volumeTrendIndicator
	 */
	public String getVolumeTrendIndicator() {
		return volumeTrendIndicator;
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
		result = prime * result + ((trendIndicator == null) ? 0 : trendIndicator.hashCode());
		result = prime * result + ((trendWeight == null) ? 0 : trendWeight.hashCode());
		result = prime * result + ((volumeTrendIndicator == null) ? 0 : volumeTrendIndicator.hashCode());
		return result;
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
	 * @param volumeTrendIndicator
	 *            the volumeTrendIndicator to set
	 */
	public void setVolumeTrendIndicator(String volumeTrendIndicator) {
		this.volumeTrendIndicator = volumeTrendIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrendParams [trendIndicator=" + trendIndicator + ", trendWeight=" + trendWeight
				+ ", volumeTrendIndicator=" + volumeTrendIndicator + "]";
	}

}
