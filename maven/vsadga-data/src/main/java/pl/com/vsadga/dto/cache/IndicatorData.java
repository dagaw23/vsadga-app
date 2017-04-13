package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;

public class IndicatorData {

	/**
	 * rozmiar bara
	 */
	private BigDecimal barSpread;

	/**
	 * ilość ticków w trakcie bara
	 */
	private Integer barVolume;

	/**
	 * ID rekordu
	 */
	private Integer id;

	public IndicatorData(Integer barVolume, BigDecimal barSpread, Integer id) {
		super();
		this.barVolume = barVolume;
		this.barSpread = barSpread;
		this.id = id;
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
		IndicatorData other = (IndicatorData) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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
	 * @return the id
	 */
	public Integer getId() {
		return id;
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
		result = prime * result + ((barSpread == null) ? 0 : barSpread.hashCode());
		result = prime * result + ((barVolume == null) ? 0 : barVolume.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IndicatorData [barSpread=" + barSpread + ", barVolume=" + barVolume + ", id=" + id + "]";
	}

}
