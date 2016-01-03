package pl.com.frxdream.data;

import java.io.Serializable;
import java.sql.Timestamp;

public class CurrencyWrited implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = -3916430623444644231L;

	private Integer id;

	private Timestamp writeTime;

	private Integer symbolListId;

	private Integer timeFrameId;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the writeTime
	 */
	public Timestamp getWriteTime() {
		return writeTime;
	}

	/**
	 * @param writeTime
	 *            the writeTime to set
	 */
	public void setWriteTime(Timestamp writeTime) {
		this.writeTime = writeTime;
	}

	/**
	 * @return the symbolListId
	 */
	public Integer getSymbolListId() {
		return symbolListId;
	}

	/**
	 * @param symbolListId
	 *            the symbolListId to set
	 */
	public void setSymbolListId(Integer symbolListId) {
		this.symbolListId = symbolListId;
	}

	/**
	 * @return the timeFrameId
	 */
	public Integer getTimeFrameId() {
		return timeFrameId;
	}

	/**
	 * @param timeFrameId
	 *            the timeFrameId to set
	 */
	public void setTimeFrameId(Integer timeFrameId) {
		this.timeFrameId = timeFrameId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencyWrited [id=" + id + ", writeTime=" + writeTime + ", symbolListId=" + symbolListId + ", timeFrameId=" + timeFrameId
				+ "]";
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((symbolListId == null) ? 0 : symbolListId.hashCode());
		result = prime * result + ((timeFrameId == null) ? 0 : timeFrameId.hashCode());
		result = prime * result + ((writeTime == null) ? 0 : writeTime.hashCode());
		return result;
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
		CurrencyWrited other = (CurrencyWrited) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (symbolListId == null) {
			if (other.symbolListId != null)
				return false;
		} else if (!symbolListId.equals(other.symbolListId))
			return false;
		if (timeFrameId == null) {
			if (other.timeFrameId != null)
				return false;
		} else if (!timeFrameId.equals(other.timeFrameId))
			return false;
		if (writeTime == null) {
			if (other.writeTime != null)
				return false;
		} else if (!writeTime.equals(other.writeTime))
			return false;
		return true;
	}

}
