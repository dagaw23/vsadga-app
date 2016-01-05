package pl.com.vsadga.service.data;

import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public interface CurrencyDataService {

	public List<BarData> getLastNbarData(int size, CurrencySymbol symbol, TimeFrame timeFrame);
}
