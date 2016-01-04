package pl.com.vsadga.dao;

import java.sql.Timestamp;

public interface CurrencyWritedDao {

	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId);
}
