package pl.com.vsadga.service.chart;

import java.util.Map;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.data.TimeFrameName;
import pl.com.vsadga.service.BaseServiceException;

public interface ChartWriter {

	public boolean deleteAccumulateChartJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
	
	public boolean deleteChartJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;

	public void initConfigParams(String pathToJasperFile, String pathToJpgFile, String pathToPdfFile,
			Map<TimeFrameName, Integer> frameConfigMap) throws BaseServiceException;

	public void writeChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
	
	public void writeAccumulateChartToJpg(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;

	public void writeAccumulateChartToPdf(String symbolName1, String symbolName2) throws BaseServiceException;
	
	public void writeChartToPdf(String symbolName1, String symbolName2) throws BaseServiceException;
	
}
