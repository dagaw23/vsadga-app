package pl.com.vsadga.dto.process;

public class IndicatorBarData {

	/**
	 * wolumen 1-go bara w kolejności
	 */
	private Integer barVolume1;

	/**
	 * wolumen 2-go bara w kolejności
	 */
	private Integer barVolume2;

	public void addVolume(Integer barVolume) {
		this.barVolume1 = this.barVolume2;
		this.barVolume2 = barVolume;
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
		IndicatorBarData other = (IndicatorBarData) obj;
		if (barVolume1 == null) {
			if (other.barVolume1 != null)
				return false;
		} else if (!barVolume1.equals(other.barVolume1))
			return false;
		if (barVolume2 == null) {
			if (other.barVolume2 != null)
				return false;
		} else if (!barVolume2.equals(other.barVolume2))
			return false;
		return true;
	}

	/**
	 * @return the barVolume1
	 */
	public Integer getBarVolume1() {
		return barVolume1;
	}

	/**
	 * @return the barVolume2
	 */
	public Integer getBarVolume2() {
		return barVolume2;
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
		result = prime * result + ((barVolume1 == null) ? 0 : barVolume1.hashCode());
		result = prime * result + ((barVolume2 == null) ? 0 : barVolume2.hashCode());
		return result;
	}

	/**
	 * @param barVolume1
	 *            the barVolume1 to set
	 */
	public void setBarVolume1(Integer barVolume1) {
		this.barVolume1 = barVolume1;
	}

	/**
	 * @param barVolume2
	 *            the barVolume2 to set
	 */
	public void setBarVolume2(Integer barVolume2) {
		this.barVolume2 = barVolume2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IndicatorBarData [barVolume1=" + barVolume1 + ", barVolume2=" + barVolume2 + "]";
	}

}
