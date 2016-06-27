package pl.com.vsadga.data;

import java.io.Serializable;

public class CurrencySymbol implements Serializable {
	/**
	 * wygenerowany UID
	 */
	private static final long serialVersionUID = -1106282759948483161L;

	private String futuresSymbol;

	private Integer id;

	private Boolean isActive;

	private String symbolName;

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
		if (futuresSymbol == null) {
			if (other.futuresSymbol != null)
				return false;
		} else if (!futuresSymbol.equals(other.futuresSymbol))
			return false;
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
		if (symbolName == null) {
			if (other.symbolName != null)
				return false;
		} else if (!symbolName.equals(other.symbolName))
			return false;
		return true;
	}

	/**
	 * @return the futuresSymbol
	 */
	public String getFuturesSymbol() {
		return futuresSymbol;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the isActive
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * @return the symbolName
	 */
	public String getSymbolName() {
		return symbolName;
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
		result = prime * result + ((futuresSymbol == null) ? 0 : futuresSymbol.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
		result = prime * result + ((symbolName == null) ? 0 : symbolName.hashCode());
		return result;
	}

	/**
	 * @param futuresSymbol
	 *            the futuresSymbol to set
	 */
	public void setFuturesSymbol(String futuresSymbol) {
		this.futuresSymbol = futuresSymbol;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @param symbolName
	 *            the symbolName to set
	 */
	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencySymbol [id=" + id + ", isActive=" + isActive + ", symbolName=" + symbolName
				+ ", futuresSymbol=" + futuresSymbol + "]";
	}

}
