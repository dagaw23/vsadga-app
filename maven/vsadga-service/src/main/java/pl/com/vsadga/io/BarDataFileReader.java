package pl.com.frxdream.io;

import java.util.List;

public interface BarDataFileReader {

	List<String> readAll(String path, String symbol, String tmeFrame) throws ReaderException;
}
