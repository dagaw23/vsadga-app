package pl.com.vsadga.service.process;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.dto.process.TrendData;
import pl.com.vsadga.service.BaseServiceException;

public interface TrendProcessor {

	TrendData getActualTrend(BarData barData) throws BaseServiceException;
}
