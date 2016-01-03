package pl.com.frxdream.service.writer;

import java.util.List;

import pl.com.frxdream.data.CurrencySymbol;
import pl.com.frxdream.data.TimeFrame;
import pl.com.frxdream.service.BaseServiceException;

public interface CurrencyDbWriterService {

	public void writeAll(List<String> recordList, CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
	
	public void write(String record, CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
}
