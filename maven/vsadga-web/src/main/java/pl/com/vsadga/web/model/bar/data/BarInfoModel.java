package pl.com.vsadga.web.model.bar.data;

import java.math.BigDecimal;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.VolumeSize;
import pl.com.vsadga.dto.BarType;
import pl.com.vsadga.utils.DateConverter;

public class BarInfoModel {

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
	 * czas bara
	 */
	private String barTime;

	private String barType;

	/**
	 * wielkość wolumenu
	 */
	private Integer barVolume;

	/**
	 * numer sygnału, jaki wystąpił na barze
	 */
	private Integer indicatorNr;

	/**
	 * czy sygnał został potwierdzony
	 */
	private Boolean isConfirm;

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

	/**
	 * jaki jest aktualny trend:
	 * <ul>
	 * <li>U - uptrend,
	 * <li>D - downtrend,
	 * <li>S - boczny.
	 * </ul>
	 */
	private String trendIndicator;

	/**
	 * kolejność wystąpienia barów trendowych
	 */
	private Integer trendWeight;

	/**
	 * wolumen absorbcyjny - dla ciągu barów
	 */
	private Integer volumeAbsorb;

	/**
	 * wielkość wolumenu - w porównaniu ze średnim wolumenem:
	 * <ul>
	 * <li>VH - bardzo wysoki,
	 * <li>Hi - wysoki,
	 * <li>Av - średni
	 * <li>Lo - mały.
	 * </ul>
	 */
	private VolumeSize volumeSize;

	/**
	 * rodzaj wolumenu:
	 * <ul>
	 * <li>rzeczywisty: R,
	 * <li>tickowy: T.
	 * </ul>
	 */
	private String volumeType;

	public BarInfoModel() {
		super();
	}

	public BarInfoModel(BarData barData) {
		this.barClose = barData.getBarClose();
		this.barHigh = barData.getBarHigh();
		this.barLow = barData.getBarLow();
		this.barTime = DateConverter.dateToString(barData.getBarTime(), "yy/MM/dd HH:mm");

		this.barType = convert(barData.getBarType());
		this.barVolume = barData.getBarVolume();
		this.indicatorNr = barData.getIndicatorNr();
		this.isConfirm = barData.getIsConfirm();

		this.processPhase = barData.getProcessPhase();
		this.trendIndicator = barData.getTrendIndicator();
		this.trendWeight = barData.getTrendWeight();

		this.volumeAbsorb = barData.getVolumeAbsorb();
		this.volumeSize = barData.getVolumeSize();
		this.volumeType = barData.getVolumeType();
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
		BarInfoModel other = (BarInfoModel) obj;
		if (barClose == null) {
			if (other.barClose != null)
				return false;
		} else if (!barClose.equals(other.barClose))
			return false;
		if (barHigh == null) {
			if (other.barHigh != null)
				return false;
		} else if (!barHigh.equals(other.barHigh))
			return false;
		if (barLow == null) {
			if (other.barLow != null)
				return false;
		} else if (!barLow.equals(other.barLow))
			return false;
		if (barTime == null) {
			if (other.barTime != null)
				return false;
		} else if (!barTime.equals(other.barTime))
			return false;
		if (barType == null) {
			if (other.barType != null)
				return false;
		} else if (!barType.equals(other.barType))
			return false;
		if (barVolume == null) {
			if (other.barVolume != null)
				return false;
		} else if (!barVolume.equals(other.barVolume))
			return false;
		if (indicatorNr == null) {
			if (other.indicatorNr != null)
				return false;
		} else if (!indicatorNr.equals(other.indicatorNr))
			return false;
		if (isConfirm == null) {
			if (other.isConfirm != null)
				return false;
		} else if (!isConfirm.equals(other.isConfirm))
			return false;
		if (processPhase == null) {
			if (other.processPhase != null)
				return false;
		} else if (!processPhase.equals(other.processPhase))
			return false;
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
		if (volumeAbsorb == null) {
			if (other.volumeAbsorb != null)
				return false;
		} else if (!volumeAbsorb.equals(other.volumeAbsorb))
			return false;
		if (volumeSize != other.volumeSize)
			return false;
		if (volumeType == null) {
			if (other.volumeType != null)
				return false;
		} else if (!volumeType.equals(other.volumeType))
			return false;
		return true;
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
	 * @return the barType
	 */
	public String getBarType() {
		return barType;
	}

	/**
	 * @return the barVolume
	 */
	public Integer getBarVolume() {
		return barVolume;
	}

	/**
	 * @return the indicatorNr
	 */
	public Integer getIndicatorNr() {
		return indicatorNr;
	}

	/**
	 * @return the isConfirm
	 */
	public Boolean getIsConfirm() {
		return isConfirm;
	}

	/**
	 * @return the processPhase
	 */
	public Integer getProcessPhase() {
		return processPhase;
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
	 * @return the volumeAbsorb
	 */
	public Integer getVolumeAbsorb() {
		return volumeAbsorb;
	}

	/**
	 * @return the volumeSize
	 */
	public VolumeSize getVolumeSize() {
		return volumeSize;
	}

	/**
	 * @return the volumeType
	 */
	public String getVolumeType() {
		return volumeType;
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
		result = prime * result + ((barClose == null) ? 0 : barClose.hashCode());
		result = prime * result + ((barHigh == null) ? 0 : barHigh.hashCode());
		result = prime * result + ((barLow == null) ? 0 : barLow.hashCode());
		result = prime * result + ((barTime == null) ? 0 : barTime.hashCode());
		result = prime * result + ((barType == null) ? 0 : barType.hashCode());
		result = prime * result + ((barVolume == null) ? 0 : barVolume.hashCode());
		result = prime * result + ((indicatorNr == null) ? 0 : indicatorNr.hashCode());
		result = prime * result + ((isConfirm == null) ? 0 : isConfirm.hashCode());
		result = prime * result + ((processPhase == null) ? 0 : processPhase.hashCode());
		result = prime * result + ((trendIndicator == null) ? 0 : trendIndicator.hashCode());
		result = prime * result + ((trendWeight == null) ? 0 : trendWeight.hashCode());
		result = prime * result + ((volumeAbsorb == null) ? 0 : volumeAbsorb.hashCode());
		result = prime * result + ((volumeSize == null) ? 0 : volumeSize.hashCode());
		result = prime * result + ((volumeType == null) ? 0 : volumeType.hashCode());
		return result;
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
	 * @param barType
	 *            the barType to set
	 */
	public void setBarType(String barType) {
		this.barType = barType;
	}

	/**
	 * @param barVolume
	 *            the barVolume to set
	 */
	public void setBarVolume(Integer barVolume) {
		this.barVolume = barVolume;
	}

	/**
	 * @param indicatorNr
	 *            the indicatorNr to set
	 */
	public void setIndicatorNr(Integer indicatorNr) {
		this.indicatorNr = indicatorNr;
	}

	/**
	 * @param isConfirm
	 *            the isConfirm to set
	 */
	public void setIsConfirm(Boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	/**
	 * @param processPhase
	 *            the processPhase to set
	 */
	public void setProcessPhase(Integer processPhase) {
		this.processPhase = processPhase;
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
	 * @param volumeAbsorb
	 *            the volumeAbsorb to set
	 */
	public void setVolumeAbsorb(Integer volumeAbsorb) {
		this.volumeAbsorb = volumeAbsorb;
	}

	/**
	 * @param volumeSize
	 *            the volumeSize to set
	 */
	public void setVolumeSize(VolumeSize volumeSize) {
		this.volumeSize = volumeSize;
	}

	/**
	 * @param volumeType
	 *            the volumeType to set
	 */
	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BarInfoModel [barClose=" + barClose + ", barHigh=" + barHigh + ", barLow=" + barLow + ", barTime="
				+ barTime + ", barType=" + barType + ", barVolume=" + barVolume + ", indicatorNr=" + indicatorNr
				+ ", isConfirm=" + isConfirm + ", processPhase=" + processPhase + ", trendIndicator="
				+ trendIndicator + ", trendWeight=" + trendWeight + ", volumeAbsorb=" + volumeAbsorb
				+ ", volumeSize=" + volumeSize + ", volumeType=" + volumeType + "]";
	}

	private String convert(BarType barType) {
		if (barType == BarType.UP_BAR)
			return "U";
		else if (barType == BarType.DOWN_BAR)
			return "D";
		else if (barType == BarType.LEVEL_BAR)
			return "L";
		else
			return " ";
	}

}
