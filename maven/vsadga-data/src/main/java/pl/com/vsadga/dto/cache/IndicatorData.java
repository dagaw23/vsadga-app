package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;

public class IndicatorData {

	/**
	 * rozmiar bara
	 */
	private BigDecimal barSpread;

	/**
	 * iloczyn wolumenu oraz spreadu
	 */
	private BigDecimal barSpreadVolume;

	/**
	 * ilość ticków w trakcie bara
	 */
	private Integer barVolume;

	public IndicatorData(Integer barVolume, BigDecimal barSpread, BigDecimal barSpreadVolume) {
		super();
		this.barVolume = barVolume;
		this.barSpread = barSpread;
		this.barSpreadVolume = barSpreadVolume;
	}

	/**
	 * @return the barSpread
	 */
	public BigDecimal getBarSpread() {
		return barSpread;
	}

	/**
	 * @return the barSpreadVolume
	 */
	public BigDecimal getBarSpreadVolume() {
		return barSpreadVolume;
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
	}

	/**
	 * @param barSpread
	 *            the barSpread to set
	 */
	public void setBarSpread(BigDecimal barSpread) {
		this.barSpread = barSpread;
	}

	/**
	 * @param barSpreadVolume
	 *            the barSpreadVolume to set
	 */
	public void setBarSpreadVolume(BigDecimal barSpreadVolume) {
		this.barSpreadVolume = barSpreadVolume;
	}

	/**
	 * @param barVolume
	 *            the barVolume to set
	 */
	public void setBarVolume(Integer barVolume) {
		this.barVolume = barVolume;
	}

}
