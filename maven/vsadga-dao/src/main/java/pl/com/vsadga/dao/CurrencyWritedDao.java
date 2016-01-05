package pl.com.vsadga.dao;

import java.sql.Timestamp;

public interface CurrencyWritedDao {
	
	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId);
	
	public int insert(long writeTime, Integer symbolId, Integer timeFrameId);
	
	public int update(long writeTime, Integer symbolId, Integer timeFrameId);
	
}
