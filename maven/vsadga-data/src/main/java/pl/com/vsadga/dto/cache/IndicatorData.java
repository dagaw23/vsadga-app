package pl.com.vsadga.dto.cache;

import java.math.BigDecimal;

public class IndicatorData {

	/**
	 * ilość ticków w trakcie bara
	 */
	private Integer barVolume;
	
	/**
	 * rozmiar bara
	 */
	private BigDecimal barSpread;
	
	public IndicatorData(Integer barVolume, BigDecimal barHigh, BigDecimal barLow) {
		super();
		this.barVolume = barVolume;
		this.barSpread = barHigh.subtract(barLow);
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
	}

	/**
	 * @param barVolume the barVolume to set
	 */
	public void setBarVolume(Integer barVolume) {
		this.barVolume = barVolume;
	}

	/**
	 * @return the barSpread
	 */
	public BigDecimal getBarSpread() {
		return barSpread;
	}

	/**
	 * @param barSpread the barSpread to set
	 */
	public void setBarSpread(BigDecimal barSpread) {
		this.barSpread = barSpread;
	}
	
	
}
