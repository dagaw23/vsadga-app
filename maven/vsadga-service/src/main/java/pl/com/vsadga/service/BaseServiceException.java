/**
 * 
 */
package pl.com.frxdream.service;

/**
 * @author dgawinkowski
 *
 */
public class BaseServiceException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -727205900114344125L;

	/**
	 * 
	 */
	public BaseServiceException() {
	}

	/**
	 * @param arg0
	 */
	public BaseServiceException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public BaseServiceException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BaseServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
