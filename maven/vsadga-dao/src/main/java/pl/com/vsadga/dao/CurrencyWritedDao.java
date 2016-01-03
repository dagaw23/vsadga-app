package pl.com.frxdream.dao;

import java.sql.Timestamp;

public interface CurrencyWritedDao {

	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId);
}
