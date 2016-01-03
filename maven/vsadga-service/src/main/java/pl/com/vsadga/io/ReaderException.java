package pl.com.frxdream.io;

public class ReaderException extends Exception {

	public ReaderException() {
		super();
	}

	public ReaderException(String msg, Throwable e) {
		super(msg, e);
	}

	public ReaderException(String msg) {
		super(msg);
	}

	public ReaderException(Throwable e) {
		super(e);
	}

}
