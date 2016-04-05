package pl.com.vsadga.dto;

public class IndicatorInfo {
	/**
	 * wartość numeryczna wskaźnika
	 */
	private int indicatorNr;

	/**
	 * czy wskaźnik już został potwierdzony
	 */
	private boolean isConfirm;

	public IndicatorInfo(boolean isConfirm, int indicatorNr) {
		super();
		this.indicatorNr = indicatorNr;
		this.isConfirm = isConfirm;
	}

	/**
	 * @return the indicatorNr
	 */
	public int getIndicatorNr() {
		return indicatorNr;
	}

	/**
	 * @return the isConfirm
	 */
	public boolean isConfirm() {
		return isConfirm;
	}

}
