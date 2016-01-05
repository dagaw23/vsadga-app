package pl.com.vsadga.data;

import java.io.Serializable;

public class CurrencySymbol implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = -1106282759948483161L;

	private Integer id;

	private Boolean isActive;

	private Integer m5TabNr;

	private String symbolName;

	private String tableName;

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
		CurrencySymbol other = (CurrencySymbol) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isActive == null) {
			if (other.isActive != null)
				return false;
		} else if (!isActive.equals(other.isActive))
			return false;
		if (m5TabNr == null) {
			if (other.m5TabNr != null)
				return false;
		} else if (!m5TabNr.equals(other.m5TabNr))
			return false;
		if (symbolName == null) {
			if (other.symbolName != null)
				return false;
		} else if (!symbolName.equals(other.symbolName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public Integer getM5TabNr() {
		return m5TabNr;
	}

	public String getSymbolName() {
		return symbolName;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
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
		result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((m5TabNr == null) ? 0 : m5TabNr.hashCode());
		result = prime * result + ((symbolName == null) ? 0 : symbolName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public void setM5TabNr(Integer m5TabNr) {
		this.m5TabNr = m5TabNr;
	}

	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	/**
	 * @param tableName
	 *            the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencySymbol [id=" + id + ", isActive=" + isActive + ", m5TabNr=" + m5TabNr + ", tableName="
				+ tableName + ", symbolName=" + symbolName + "]";
	}

}
