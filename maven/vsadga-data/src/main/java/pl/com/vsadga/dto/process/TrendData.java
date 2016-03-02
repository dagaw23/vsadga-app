package pl.com.vsadga.dto.process;

public class TrendData {

	/**
	 * trend w poprzednim barze:
	 * <ul>
	 * <li>U: rosnący,
	 * <li>D: malejący,
	 * <li>S: boczny.
	 * </ul>
	 */
	private String trendIndicator;

	/**
	 * waga trendu <br/>
	 * dla malejącego wagi mogą być następujące: 1, 2, 3.<br/>
	 * dla rosnącego wagi mogą być następujące: 6, 7, 8.<br/>
	 */
	private Integer trendWeight;
	
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
		TrendData other = (TrendData) obj;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrendData [trendIndicator=" + trendIndicator + ", trendWeight=" + trendWeight + "]";
	}

}
