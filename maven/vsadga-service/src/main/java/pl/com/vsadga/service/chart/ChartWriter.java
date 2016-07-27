package pl.com.vsadga.service.chart;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface ChartWriter {

	public void writeChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame, int barToPrintCount) throws BaseServiceException;
	
	public boolean deleteChartJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
	
	public void writeChartToPdf(String symbolName1, String symbolName2) throws BaseServiceException;
	
	public void initConfigParams(String pathToJasperFile, String pathToJpgFile, String pathToPdfFile) throws BaseServiceException;
}
