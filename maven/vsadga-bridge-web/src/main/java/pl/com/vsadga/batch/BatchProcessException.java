/**
 * 
 */
package pl.com.vsadga.batch;

/**
 * Ogólny wyjątek przetwarzania klasy komponentu batchowego.
 * 
 * @author dgawinkowski
 *
 */
public class BatchProcessException extends Exception {

	/**
	 * 
	 */
	public BatchProcessException() {
	}

	/**
	 * @param arg0
	 */
	public BatchProcessException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public BatchProcessException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BatchProcessException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
