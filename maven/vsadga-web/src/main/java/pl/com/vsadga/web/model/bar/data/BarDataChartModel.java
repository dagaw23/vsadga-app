package pl.com.vsadga.web.model.bar.data;

public class BarDataChartModel {

	/**
	 * liczba barów do wyrysowania
	 */
	private Integer barCount;

	/**
	 * ramka czasowa waloru
	 */
	private String frame;

	/**
	 * nazwa symbolu waloru
	 */
	private Integer symbolId;

	public BarDataChartModel() {
	}

	/**
	 * @param frame
	 * @param symbolId
	 * @param barCount
	 */
	public BarDataChartModel(String frame, Integer symbolId, Integer barCount) {
		super();
		this.frame = frame;
		this.symbolId = symbolId;
		this.barCount = barCount;
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
		BarDataChartModel other = (BarDataChartModel) obj;
		if (barCount == null) {
			if (other.barCount != null)
				return false;
		} else if (!barCount.equals(other.barCount))
			return false;
		if (frame == null) {
			if (other.frame != null)
				return false;
		} else if (!frame.equals(other.frame))
			return false;
		if (symbolId == null) {
			if (other.symbolId != null)
				return false;
		} else if (!symbolId.equals(other.symbolId))
			return false;
		return true;
	}

	/**
	 * @return the barCount
	 */
	public Integer getBarCount() {
		return barCount;
	}

	/**
	 * @return the frame
	 */
	public String getFrame() {
		return frame;
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
		result = prime * result + ((barCount == null) ? 0 : barCount.hashCode());
		result = prime * result + ((frame == null) ? 0 : frame.hashCode());
		result = prime * result + ((symbolId == null) ? 0 : symbolId.hashCode());
		return result;
	}

	/**
	 * @param barCount
	 *            the barCount to set
	 */
	public void setBarCount(Integer barCount) {
		this.barCount = barCount;
	}

	/**
	 * @param frame
	 *            the frame to set
	 */
	public void setFrame(String frame) {
		this.frame = frame;
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
		return "BarDataChartModel [frame=" + frame + ", symbolId=" + symbolId + ", barCount=" + barCount + "]";
	}

}
