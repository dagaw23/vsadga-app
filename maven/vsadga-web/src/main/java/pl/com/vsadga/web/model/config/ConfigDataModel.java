package pl.com.vsadga.web.model.config;

public class ConfigDataModel {

	private Integer paramId;

	private String paramNewValue;

	public ConfigDataModel() {
		super();
	}

	/**
	 * @return the paramId
	 */
	public Integer getParamId() {
		return paramId;
	}

	/**
	 * @return the paramNewValue
	 */
	public String getParamNewValue() {
		return paramNewValue;
	}

	/**
	 * @param paramId
	 *            the paramId to set
	 */
	public void setParamId(Integer paramId) {
		this.paramId = paramId;
	}

	/**
	 * @param paramNewValue
	 *            the paramNewValue to set
	 */
	public void setParamNewValue(String paramNewValue) {
		this.paramNewValue = paramNewValue;
	}

}
