package pl.com.vsadga.processor;

public interface DataStreamProcessor {

	void connect(String user, String passwd) throws DataStreamProcessorException;

	void disconnect() throws DataStreamProcessorException;
	
	void subscribe(String symbol) throws DataStreamProcessorException;
	
	void unsubscribe(String symbol) throws DataStreamProcessorException;
}
