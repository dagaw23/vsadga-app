package pl.com.vsadga.dto.bar;

import java.math.BigDecimal;

public class BarSimpleDto {

	/**
	 * cena zamknięcia
	 */
	private BigDecimal barClose;

	/**
	 * cena maksymalna w trakcie bara
	 */
	private BigDecimal barHigh;

	/**
	 * cena minimalna w trakcie bara
	 */
	private BigDecimal barLow;

	/**
	 * czas bara (format yy/MM/dd HH:mm)
	 */
	private String barTime;

	/**
	 * wolumen rzeczywisty lub tickowy
	 */
	private Integer barVolume;

	/**
	 * jaki jest status przetworzenia bara:
	 * <ul>
	 * <li>0: wpisane wartości tymczasowe dla cen,
	 * <li>1: wpisane wartości ostateczne dla cen, ale nie zostały wpisane sygnały,
	 * <li>2: przetworzony pod kierunkiem sygnału, ale czeka na potwierdzenie,
	 * <li>3: przetoworzony do końca.
	 * </ul>
	 */
	private Integer processPhase;

	public BarSimpleDto() {
		super();
	}

	public BarSimpleDto(String barTime, BigDecimal barHigh, BigDecimal barLow, BigDecimal barClose,
			Integer barVolume, Integer processPhase) {
		super();
		this.barTime = barTime;
		this.barHigh = barHigh;
		this.barLow = barLow;
		this.barClose = barClose;
		this.barVolume = barVolume;
		this.processPhase = processPhase;
	}

	/**
	 * @return the barClose
	 */
	public BigDecimal getBarClose() {
		return barClose;
	}

	/**
	 * @return the barHigh
	 */
	public BigDecimal getBarHigh() {
		return barHigh;
	}

	/**
	 * @return the barLow
	 */
	public BigDecimal getBarLow() {
		return barLow;
	}

	/**
	 * @return the barTime
	 */
	public String getBarTime() {
		return barTime;
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
	}

	/**
	 * @return the processPhase
	 */
	public Integer getProcessPhase() {
		return processPhase;
	}

	/**
	 * @param barClose
	 *            the barClose to set
	 */
	public void setBarClose(BigDecimal barClose) {
		this.barClose = barClose;
	}

	/**
	 * @param barHigh
	 *            the barHigh to set
	 */
	public void setBarHigh(BigDecimal barHigh) {
		this.barHigh = barHigh;
	}

	/**
	 * @param barLow
	 *            the barLow to set
	 */
	public void setBarLow(BigDecimal barLow) {
		this.barLow = barLow;
	}

	/**
	 * @param barTime
	 *            the barTime to set
	 */
	public void setBarTime(String barTime) {
		this.barTime = barTime;
	}

	/**
	 * @param barVolume
	 *            the barVolume to set
	 */
	public void setBarVolume(Integer barVolume) {
		this.barVolume = barVolume;
	}

	/**
	 * @param processPhase
	 *            the processPhase to set
	 */
	public void setProcessPhase(Integer processPhase) {
		this.processPhase = processPhase;
	}

}
