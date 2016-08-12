package pl.com.vsadga.web.model.bar.data;

public class BarDataModel {

	/**
	 * ramka czasowa waloru
	 */
	private String frame;

	/**
	 * nazwa symbolu waloru
	 */
	private String symbolId;

	public BarDataModel() {
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
	public String getSymbolId() {
		return symbolId;
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
	public void setSymbolId(String symbolId) {
		this.symbolId = symbolId;
	}

}
