package pl.com.vsadga.dao;

import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public interface BarDataDao {

	public void batchInsert(List<BarData> recordList, CurrencySymbol symbol, TimeFrame timeFrame);
	
}
