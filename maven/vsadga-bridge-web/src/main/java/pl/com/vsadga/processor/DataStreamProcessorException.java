/**
 * 
 */
package pl.com.vsadga.processor;

/**
 * @author dgawinkowski
 *
 */
public class DataStreamProcessorException extends Exception {

	/**
	 * 
	 */
	public DataStreamProcessorException() {
	}

	/**
	 * @param arg0
	 */
	public DataStreamProcessorException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public DataStreamProcessorException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DataStreamProcessorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
