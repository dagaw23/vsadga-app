package pl.com.vsadga.dao;

import java.util.Date;
import java.util.List;

import pl.com.vsadga.data.BarData;
import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public interface BarDataDao {

	public void batchInsert(CurrencySymbol symbol, TimeFrame timeFrame, List<BarData> dataList);
	
	boolean existBarData(CurrencySymbol symbol, TimeFrame timeFrame, Date date);
	
	public List<BarData> getLastNbarsData(CurrencySymbol symbol, TimeFrame timeFrame, int size);
	
	int insert(CurrencySymbol symbol, TimeFrame timeFrame, BarData barData);
	
	int insertOrUpdate(CurrencySymbol symbol, TimeFrame timeFrame, )
	
	int updateIndyData(Integer id, TimeFrame timeFrame, Integer nr, Integer weight, Boolean isConfirm, Integer phase);
	
}
