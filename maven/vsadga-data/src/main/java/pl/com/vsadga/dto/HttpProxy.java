package pl.com.vsadga.dto;

public class HttpProxy {

	private boolean isHttpProxy;

	private String proxyHost;

	private int proxyPort;

	public HttpProxy(boolean isHttpProxy) {
		this.isHttpProxy = isHttpProxy;
		this.proxyHost = null;
		this.proxyPort = 0;
	}

	public HttpProxy(boolean isHttpProxy, String proxyHost, int proxyPort) {
		this.isHttpProxy = isHttpProxy;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @return the isHttpProxy
	 */
	public boolean isHttpProxy() {
		return isHttpProxy;
	}

	/**
	 * @param isHttpProxy
	 *            the isHttpProxy to set
	 */
	public void setHttpProxy(boolean isHttpProxy) {
		this.isHttpProxy = isHttpProxy;
	}

	/**
	 * @param proxyHost
	 *            the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @param proxyPort
	 *            the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

}
