package pl.com.vsadga.service.process;

import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface BarDataProcessor {

	void processBarsData(List<BarData> barDataList, TimeFrame timeFrame) throws BaseServiceException;
}
