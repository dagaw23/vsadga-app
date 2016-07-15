package pl.com.vsadga.io;

import java.text.ParseException;
import java.util.List;

import pl.com.vsadga.dto.Mt4FileRecord;

public interface BarDataFileReader {

	List<Mt4FileRecord> readAll(String path, String symbol, String tmeFrame) throws ReaderException, ParseException;
}
