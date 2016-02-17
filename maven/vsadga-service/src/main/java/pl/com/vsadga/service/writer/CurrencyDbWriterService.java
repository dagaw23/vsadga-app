package pl.com.vsadga.service.writer;

import java.util.List;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface CurrencyDbWriterService {

	public void writeAll(List<String> recordList, CurrencySymbol symbol, TimeFrame timeFrame)
			throws BaseServiceException;

	/**
	 * Sprawdza wg aktualnej daty systemowej oraz rodzaju ramki - które bary są
	 * do wpisania, a które do aktualizacji w tabeli.
	 * 
	 * @param symbol
	 * @param timeFrame
	 * @param recordList
	 * @throws BaseServiceException
	 */
	void write(CurrencySymbol symbol, TimeFrame timeFrame, List<String> recordList, int hourShift) throws BaseServiceException;

}
