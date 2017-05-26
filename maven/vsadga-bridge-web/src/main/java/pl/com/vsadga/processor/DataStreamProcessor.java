package pl.com.vsadga.processor;

public interface DataStreamProcessor {

	void connect(String user, String passwd) throws DataStreamProcessorException;

	void disconnect() throws DataStreamProcessorException;
}
