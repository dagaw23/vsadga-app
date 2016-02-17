package pl.com.vsadga.service.data;

import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.service.BaseServiceException;

public interface CurrencyDataService {

	List<BarData> getBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException;

	List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame);

	List<BarData> getNotProcessBarDataList(Integer symbolId, String timeFrameDesc) throws BaseServiceException;
}
