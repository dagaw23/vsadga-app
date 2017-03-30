package pl.com.vsadga.web.model.alert;

public class AlertDataModel {

	private String frameSelected;

	private String symbolSelected;

	public AlertDataModel() {
		super();
	}

	/**
	 * @return the frameSelected
	 */
	public String getFrameSelected() {
		return frameSelected;
	}

	/**
	 * @return the symbolSelected
	 */
	public String getSymbolSelected() {
		return symbolSelected;
	}

	/**
	 * @param frameSelected
	 *            the frameSelected to set
	 */
	public void setFrameSelected(String frameSelected) {
		this.frameSelected = frameSelected;
	}

	/**
	 * @param symbolSelected
	 *            the symbolSelected to set
	 */
	public void setSymbolSelected(String symbolSelected) {
		this.symbolSelected = symbolSelected;
	}

}
