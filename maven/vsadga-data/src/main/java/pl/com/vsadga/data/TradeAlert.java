package pl.com.vsadga.data;

import java.io.Serializable;
import java.util.Date;

public class TradeAlert implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 928584500147828793L;

	/**
	 * komunikat alertu
	 */
	private String alertMessage;

	/**
	 * data wystąpienia alertu
	 */
	private Date alertTime;

	/**
	 * ID rekordu
	 */
	private Integer id;

	/**
	 * odwołanie do symbolu, którego dotyczy alert
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
		TradeAlert other = (TradeAlert) obj;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (symbolId == null) {
			if (other.symbolId != null)
				return false;
		} else if (!symbolId.equals(other.symbolId))
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
	 * @return the id
	 */
	public Integer getId() {
		return id;
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
		result = prime * result + ((alertMessage == null) ? 0 : alertMessage.hashCode());
		result = prime * result + ((alertTime == null) ? 0 : alertTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((symbolId == null) ? 0 : symbolId.hashCode());
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
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
		return "TradeAlert [alertMessage=" + alertMessage + ", alertTime=" + alertTime + ", id=" + id
				+ ", symbolId=" + symbolId + "]";
	}

}
