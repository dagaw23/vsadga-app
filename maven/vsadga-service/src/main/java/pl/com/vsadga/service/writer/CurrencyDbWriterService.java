package pl.com.vsadga.service.writer;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface CurrencyDbWriterService {

	public void writeAll(List<String> recordList, CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
	
}
