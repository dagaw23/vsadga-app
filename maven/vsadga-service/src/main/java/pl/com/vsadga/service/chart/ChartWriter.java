package pl.com.vsadga.service.chart;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface ChartWriter {

	public void writeChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount, String pathToWrite) throws BaseServiceException;
	
	public boolean deleteChartJpg(CurrencySymbol symbol, TimeFrame timeFrame, String pathToWrite) throws BaseServiceException;
	
	public void writeChartToPdf(String symbolName1, String symbolName2) throws BaseServiceException;
	
	public void initConfigParams() throws BaseServiceException;
}
