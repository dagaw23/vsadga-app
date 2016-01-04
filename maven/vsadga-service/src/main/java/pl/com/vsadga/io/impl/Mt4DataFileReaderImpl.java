package pl.com.vsadga.io.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.com.vsadga.io.BarDataFileReader;
import pl.com.vsadga.io.BaseReader;
import pl.com.vsadga.io.ReaderException;

public class Mt4DataFileReaderImpl extends BaseReader implements BarDataFileReader {

	public Mt4DataFileReaderImpl(String encode) {
		super(encode);
	}

	@Override
	public List<String> readAll(String path, String symbol, String tmeFrame) throws ReaderException {
		String one_line = null;
		List<String> line_list = new ArrayList<String>();
		
		try {
			openStreams(path + File.separator + getFileName(symbol, tmeFrame));
			
			while ((one_line = readLine()) != null)
				line_list.add(one_line);
			
			return line_list;
			
		} finally {
			closeStreams();
		}
	}
	
	private String getFileName(String symbol, String tmeFrame) {
		return symbol + "_" + tmeFrame;
	}

}
