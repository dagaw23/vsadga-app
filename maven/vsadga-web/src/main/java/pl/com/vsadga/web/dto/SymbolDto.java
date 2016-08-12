package pl.com.vsadga.web.dto;

public class SymbolDto {

	private Integer symbolId;

	private String symbolName;

	public SymbolDto() {
		super();
	}

	public SymbolDto(Integer symbolId, String symbolName) {
		super();
		this.symbolId = symbolId;
		this.symbolName = symbolName;
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
		SymbolDto other = (SymbolDto) obj;
		if (symbolId == null) {
			if (other.symbolId != null)
				return false;
		} else if (!symbolId.equals(other.symbolId))
			return false;
		if (symbolName == null) {
			if (other.symbolName != null)
				return false;
		} else if (!symbolName.equals(other.symbolName))
			return false;
		return true;
	}

	/**
	 * @return the symbolId
	 */
	public Integer getSymbolId() {
		return symbolId;
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
		result = prime * result + ((symbolId == null) ? 0 : symbolId.hashCode());
		result = prime * result + ((symbolName == null) ? 0 : symbolName.hashCode());
		return result;
	}

	/**
	 * @param symbolId
	 *            the symbolId to set
	 */
	public void setSymbolId(Integer symbolId) {
		this.symbolId = symbolId;
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
		return "SymbolDto [symbolName=" + symbolName + ", symbolId=" + symbolId + "]";
	}

}
