package pl.com.vsadga.service.writer;

import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface CurrencyDbWriterService {

	/**
	 * Sprawdza wg aktualnej daty systemowej oraz rodzaju ramki - które bary są do wpisania, a które
	 * do aktualizacji w tabeli.
	 * 
	 * @param symbol
	 * @param timeFrame
	 * @param recordList
	 * @param sysTime
	 * @throws BaseServiceException
	 */
	//void write(CurrencySymbol symbol, TimeFrame timeFrame, List<Mt4FileRecord> recordList,
	//		GregorianCalendar sysTime) throws BaseServiceException;
	
	/**
	 * Wpisuje poszczególne bary do tabeli lub aktualizuje istniejące bary ze statusem 0 lub nic nie robi z barami, które mają już status 1.
	 * 
	 * @param symbol
	 * @param timeFrame
	 * @param recordList
	 * @param sysTime
	 * @throws BaseServiceException
	 */
	void writeOrUpdate(CurrencySymbol symbol, TimeFrame timeFrame, List<BarData> barDataList) throws BaseServiceException;

}
