package pl.com.frxdream.dao;

import java.util.List;

import pl.com.frxdream.data.BarData;
import pl.com.frxdream.data.CurrencySymbol;
import pl.com.frxdream.data.TimeFrame;

public interface BarDataDao {

	public void batchInsert(List<BarData> recordList, CurrencySymbol symbol, TimeFrame timeFrame);
	
}
