package pl.com.vsadga.dto;

import java.util.GregorianCalendar;

/**
 * Pojedyncza informacja o wolumenie w postaci daty oraz wartości wolumenu.
 * 
 * @author dgawinkowski
 *
 */
public class VolumeData {

	private Integer volumeSize;

	private GregorianCalendar volumeTime;

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
		VolumeData other = (VolumeData) obj;
		if (volumeSize == null) {
			if (other.volumeSize != null)
				return false;
		} else if (!volumeSize.equals(other.volumeSize))
			return false;
		if (volumeTime == null) {
			if (other.volumeTime != null)
				return false;
		} else if (!volumeTime.equals(other.volumeTime))
			return false;
		return true;
	}

	/**
	 * @return the volumeSize
	 */
	public Integer getVolumeSize() {
		return volumeSize;
	}

	/**
	 * @return the volumeTime
	 */
	public GregorianCalendar getVolumeTime() {
		return volumeTime;
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
		result = prime * result + ((volumeSize == null) ? 0 : volumeSize.hashCode());
		result = prime * result + ((volumeTime == null) ? 0 : volumeTime.hashCode());
		return result;
	}

	/**
	 * @param volumeSize
	 *            the volumeSize to set
	 */
	public void setVolumeSize(Integer volumeSize) {
		this.volumeSize = volumeSize;
	}

	/**
	 * @param volumeTime
	 *            the volumeTime to set
	 */
	public void setVolumeTime(GregorianCalendar volumeTime) {
		this.volumeTime = volumeTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VolumeData [volumeTime=" + volumeTime + ", volumeSize=" + volumeSize + "]";
	}

}
