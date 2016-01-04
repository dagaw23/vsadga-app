/**
 * 
 */
package pl.com.vsadga.dao;

/**
 * @author dariuszg
 *
 */
public class DaoBaseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7427300604687339269L;

	/**
	 * 
	 */
	public DaoBaseException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DaoBaseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public DaoBaseException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DaoBaseException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
