package pl.com.vsadga.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class BarData implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = -404888175506948188L;

	/**
	 * cena zamknięcia
	 */
	private BigDecimal barClose;

	/**
	 * czas bara (w milisekundach)
	 */
	private Date barTime;

	/**
	 * cena maksymalna w trakcie bara
	 */
	private BigDecimal barHigh;

	/**
	 * cena minimalna w trakcie bara
	 */
	private BigDecimal barLow;

	/**
	 * ilość ticków w trakcie bara
	 */
	private Integer barVolume;

	/**
	 * ID rekordu
	 */
	private Integer id;

	/**
	 * wyliczona średnia krocząca
	 */
	private BigDecimal imaCount;

	/**
	 * numer sygnału, jaki wystąpił na barze
	 */
	private Integer indicatorNr;

	/**
	 * waga wskaźnika <br/>
	 * (im większy numer, tym większe znaczenie wskaźnika)
	 */
	private Integer indicatorWeight;

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
	 * <li>2: przetoworzony do końca.
	 * </ul>
	 */
	private Integer processPhase;

	/**
	 * odwołanie do symbolu, którego dotyczy bar
	 */
	private Integer symbolId;

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
		BarData other = (BarData) obj;
		if (barClose == null) {
			if (other.barClose != null)
				return false;
		} else if (!barClose.equals(other.barClose))
			return false;
		if (barTime == null) {
			if (other.barTime != null)
				return false;
		} else if (!barTime.equals(other.barTime))
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
		if (imaCount == null) {
			if (other.imaCount != null)
				return false;
		} else if (!imaCount.equals(other.imaCount))
			return false;
		if (indicatorNr == null) {
			if (other.indicatorNr != null)
				return false;
		} else if (!indicatorNr.equals(other.indicatorNr))
			return false;
		if (indicatorWeight == null) {
			if (other.indicatorWeight != null)
				return false;
		} else if (!indicatorWeight.equals(other.indicatorWeight))
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
		if (symbolId == null) {
			if (other.symbolId != null)
				return false;
		} else if (!symbolId.equals(other.symbolId))
			return false;
		return true;
	}

	/**
	 * @return the barClose
	 */
	public BigDecimal getBarClose() {
		return barClose;
	}

	public Date getBarTime() {
		return barTime;
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

	public Integer getBarVolume() {
		return barVolume;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the imaCount
	 */
	public BigDecimal getImaCount() {
		return imaCount;
	}

	public Integer getIndicatorNr() {
		return indicatorNr;
	}

	public Integer getIndicatorWeight() {
		return indicatorWeight;
	}

	public Boolean getIsConfirm() {
		return isConfirm;
	}

	public Integer getProcessPhase() {
		return processPhase;
	}

	/**
	 * @return the symbolId
	 */
	public Integer getSymbolId() {
		return symbolId;
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
		result = prime * result + ((barTime == null) ? 0 : barTime.hashCode());
		result = prime * result + ((barHigh == null) ? 0 : barHigh.hashCode());
		result = prime * result + ((barLow == null) ? 0 : barLow.hashCode());
		result = prime * result + ((barVolume == null) ? 0 : barVolume.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imaCount == null) ? 0 : imaCount.hashCode());
		result = prime * result + ((indicatorNr == null) ? 0 : indicatorNr.hashCode());
		result = prime * result + ((indicatorWeight == null) ? 0 : indicatorWeight.hashCode());
		result = prime * result + ((isConfirm == null) ? 0 : isConfirm.hashCode());
		result = prime * result + ((processPhase == null) ? 0 : processPhase.hashCode());
		result = prime * result + ((symbolId == null) ? 0 : symbolId.hashCode());
		return result;
	}

	/**
	 * @param barClose
	 *            the barClose to set
	 */
	public void setBarClose(BigDecimal barClose) {
		this.barClose = barClose;
	}

	public void setBarTime(Date barTime) {
		this.barTime = barTime;
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

	/**
	 * @param imaCount
	 *            the imaCount to set
	 */
	public void setImaCount(BigDecimal imaCount) {
		this.imaCount = imaCount;
	}

	public void setIndicatorNr(Integer indicatorNr) {
		this.indicatorNr = indicatorNr;
	}

	public void setIndicatorWeight(Integer indicatorWeight) {
		this.indicatorWeight = indicatorWeight;
	}

	public void setIsConfirm(Boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	public void setProcessPhase(Integer processPhase) {
		this.processPhase = processPhase;
	}

	/**
	 * @param symbolId
	 *            the symbolId to set
	 */
	public void setSymbolId(Integer symbolId) {
		this.symbolId = symbolId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BarData [barClose=" + barClose + ", barTime=" + barTime + ", barHigh=" + barHigh + ", barLow="
				+ barLow + ", barVolume=" + barVolume + ", id=" + id + ", imaCount=" + imaCount + ", indicatorNr="
				+ indicatorNr + ", indicatorWeight=" + indicatorWeight + ", isConfirm=" + isConfirm
				+ ", processPhase=" + processPhase + ", symbolId=" + symbolId + "]";
	}

}
