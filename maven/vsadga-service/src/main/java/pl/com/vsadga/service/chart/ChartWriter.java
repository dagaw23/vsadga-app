package pl.com.vsadga.service.chart;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface ChartWriter {

	public void print(CurrencySymbol symbol, TimeFrame timeFrame) throws BaseServiceException;
}
