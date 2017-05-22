package pl.com.vsadga.service.process;

import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.service.BaseServiceException;

public interface BarDataProcessor {

	void processBarDataByPhase(List<BarData> barDataList, String timeFrameDesc) throws BaseServiceException;
	
	
}
