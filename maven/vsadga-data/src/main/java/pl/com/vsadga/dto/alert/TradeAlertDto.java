package pl.com.vsadga.dto.alert;

import java.io.Serializable;
import java.util.Date;

import pl.com.vsadga.data.alert.AlertType;

public class TradeAlertDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2418735029950852907L;

	/**
	 * komunikat alertu
	 */
	private String alertMessage;

	/**
	 * data wystąpienia alertu
	 */
	private Date alertTime;

	/**
	 * typ alertu
	 */
	private AlertType alertType;

	/**
	 * status bara:
	 * <ul>
	 * <li>zakończony: E,
	 * <li>tymczasowy: T.
	 * </ul>
	 */
	private String barStatus;

	/**
	 * czas bara
	 */
	private String barTime;

	/**
	 * ID rekordu
	 */
	private Integer id;

	private String symbolName;

	private String timeFrameDesc;

	/**
	 * 
	 */
	public TradeAlertDto() {
		super();
	}

	/**
	 * @param id
	 * @param alertTime
	 * @param alertType
	 * @param barTime
	 * @param alertMessage
	 * @param barStatus
	 * @param symbolName
	 * @param timeFrameDesc
	 */
	public TradeAlertDto(Integer id, Date alertTime, AlertType alertType, String barTime, String alertMessage,
			String barStatus, String symbolName, String timeFrameDesc) {
		super();
		this.id = id;
		this.alertTime = alertTime;
		this.alertType = alertType;
		this.barTime = barTime;
		this.alertMessage = alertMessage;
		this.barStatus = barStatus;
		this.symbolName = symbolName;
		this.timeFrameDesc = timeFrameDesc;
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
		TradeAlertDto other = (TradeAlertDto) obj;
		if (alertMessage == null) {
			if (other.alertMessage != null)
				return false;
		} else if (!alertMessage.equals(other.alertMessage))
			return false;
		if (alertTime == null) {
			if (other.alertTime != null)
				return false;
		} else if (!alertTime.equals(other.alertTime))
			return false;
		if (alertType != other.alertType)
			return false;
		if (barStatus == null) {
			if (other.barStatus != null)
				return false;
		} else if (!barStatus.equals(other.barStatus))
			return false;
		if (barTime == null) {
			if (other.barTime != null)
				return false;
		} else if (!barTime.equals(other.barTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (symbolName == null) {
			if (other.symbolName != null)
				return false;
		} else if (!symbolName.equals(other.symbolName))
			return false;
		if (timeFrameDesc == null) {
			if (other.timeFrameDesc != null)
				return false;
		} else if (!timeFrameDesc.equals(other.timeFrameDesc))
			return false;
		return true;
	}

	/**
	 * @return the alertMessage
	 */
	public String getAlertMessage() {
		return alertMessage;
	}

	/**
	 * @return the alertTime
	 */
	public Date getAlertTime() {
		return alertTime;
	}

	/**
	 * @return the alertType
	 */
	public AlertType getAlertType() {
		return alertType;
	}

	/**
	 * @return the barStatus
	 */
	public String getBarStatus() {
		return barStatus;
	}

	/**
	 * @return the barTime
	 */
	public String getBarTime() {
		return barTime;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the symbolName
	 */
	public String getSymbolName() {
		return symbolName;
	}

	/**
	 * @return the timeFrameDesc
	 */
	public String getTimeFrameDesc() {
		return timeFrameDesc;
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
		result = prime * result + ((alertMessage == null) ? 0 : alertMessage.hashCode());
		result = prime * result + ((alertTime == null) ? 0 : alertTime.hashCode());
		result = prime * result + ((alertType == null) ? 0 : alertType.hashCode());
		result = prime * result + ((barStatus == null) ? 0 : barStatus.hashCode());
		result = prime * result + ((barTime == null) ? 0 : barTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((symbolName == null) ? 0 : symbolName.hashCode());
		result = prime * result + ((timeFrameDesc == null) ? 0 : timeFrameDesc.hashCode());
		return result;
	}

	/**
	 * @param alertMessage
	 *            the alertMessage to set
	 */
	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}

	/**
	 * @param alertTime
	 *            the alertTime to set
	 */
	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	}

	/**
	 * @param alertType
	 *            the alertType to set
	 */
	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}

	/**
	 * @param barStatus
	 *            the barStatus to set
	 */
	public void setBarStatus(String barStatus) {
		this.barStatus = barStatus;
	}

	/**
	 * @param barTime
	 *            the barTime to set
	 */
	public void setBarTime(String barTime) {
		this.barTime = barTime;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param symbolName
	 *            the symbolName to set
	 */
	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	/**
	 * @param timeFrameDesc
	 *            the timeFrameDesc to set
	 */
	public void setTimeFrameDesc(String timeFrameDesc) {
		this.timeFrameDesc = timeFrameDesc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TradeAlertDto [alertTime=" + alertTime + ", alertType=" + alertType + ", barTime=" + barTime
				+ ", symbolName=" + symbolName + ", timeFrameDesc=" + timeFrameDesc + ", alertMessage="
				+ alertMessage + ", id=" + id + ", barStatus=" + barStatus + "]";
	}

}
