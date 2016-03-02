package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.TrendParams;
import pl.com.vsadga.service.BaseServiceException;

public interface TrendProcessor {

	void clearTrendData();
	
	TrendData getActualTrend(BarData barData) throws BaseServiceException;
}
