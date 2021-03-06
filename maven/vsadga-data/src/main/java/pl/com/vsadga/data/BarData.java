package pl.com.vsadga.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import pl.com.vsadga.dto.BarType;

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
	private Date barTime;

	private BarType barType;

	/**
	 * wielkość wolumenu
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
	 * wielkość spreadu - w porównaniu z barami sąsiednimi:
	 * <ul>
	 * <li>VH - bardzo wysoki,
	 * <li>Hi - wysoki,
	 * <li>Av - średni,
	 * <li>Lo - mały,
	 * <li>VL - bardzo mały.
	 * </ul>
	 */
	private SpreadSize spreadSize;

	/**
	 * odwołanie do symbolu, którego dotyczy bar
	 */
	private Integer symbolId;

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
	 * wielkość wolumenu - w porównaniu z barami sąsiednimi:
	 * <ul>
	 * <li>VH - bardzo wysoki,
	 * <li>Hi - wysoki,
	 * <li>Av - średni,
	 * <li>Lo - mały,
	 * <li>VL - bardzo mały.
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
		if (barType != other.barType)
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
		if (spreadSize != other.spreadSize)
			return false;
		if (symbolId == null) {
			if (other.symbolId != null)
				return false;
		} else if (!symbolId.equals(other.symbolId))
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

	public Date getBarTime() {
		return barTime;
	}

	/**
	 * @return the barType
	 */
	public BarType getBarType() {
		return barType;
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

	public Boolean getIsConfirm() {
		return isConfirm;
	}

	public Integer getProcessPhase() {
		return processPhase;
	}

	/**
	 * @return the spreadSize
	 */
	public SpreadSize getSpreadSize() {
		return spreadSize;
	}

	/**
	 * @return the symbolId
	 */
	public Integer getSymbolId() {
		return symbolId;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imaCount == null) ? 0 : imaCount.hashCode());
		result = prime * result + ((indicatorNr == null) ? 0 : indicatorNr.hashCode());
		result = prime * result + ((isConfirm == null) ? 0 : isConfirm.hashCode());
		result = prime * result + ((processPhase == null) ? 0 : processPhase.hashCode());
		result = prime * result + ((spreadSize == null) ? 0 : spreadSize.hashCode());
		result = prime * result + ((symbolId == null) ? 0 : symbolId.hashCode());
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

	public void setBarTime(Date barTime) {
		this.barTime = barTime;
	}

	/**
	 * @param barType
	 *            the barType to set
	 */
	public void setBarType(BarType barType) {
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

	public void setIsConfirm(Boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	public void setProcessPhase(Integer processPhase) {
		this.processPhase = processPhase;
	}

	/**
	 * @param spreadSize
	 *            the spreadSize to set
	 */
	public void setSpreadSize(SpreadSize spreadSize) {
		this.spreadSize = spreadSize;
	}

	/**
	 * @param symbolId
	 *            the symbolId to set
	 */
	public void setSymbolId(Integer symbolId) {
		this.symbolId = symbolId;
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
		return "BarData [barClose=" + barClose + ", barHigh=" + barHigh + ", barLow=" + barLow + ", barTime="
				+ barTime + ", barType=" + barType + ", barVolume=" + barVolume + ", id=" + id + ", imaCount="
				+ imaCount + ", indicatorNr=" + indicatorNr + ", isConfirm=" + isConfirm + ", processPhase="
				+ processPhase + ", symbolId=" + symbolId + ", trendIndicator=" + trendIndicator
				+ ", trendWeight=" + trendWeight + ", volumeAbsorb=" + volumeAbsorb + ", volumeSize=" + volumeSize
				+ ", spreadSize=" + spreadSize + ", volumeType=" + volumeType + "]";
	}

}
