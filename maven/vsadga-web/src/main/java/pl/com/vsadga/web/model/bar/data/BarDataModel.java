package pl.com.vsadga.web.model.bar.data;

public class BarDataModel {

	/**
	 * ramka czasowa waloru
	 */
	private String frame;

	/**
	 * nazwa symbolu waloru
	 */
	private Integer symbolId;

	public BarDataModel() {
	}
	
	/**
	 * @param frame
	 * @param symbolId
	 */
	public BarDataModel(String frame, Integer symbolId) {
		super();
		this.frame = frame;
		this.symbolId = symbolId;
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

}
