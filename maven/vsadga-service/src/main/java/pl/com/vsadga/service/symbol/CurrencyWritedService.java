package pl.com.frxdream.service.symbol;

import java.sql.Timestamp;

public interface CurrencyWritedService {

	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId);
}
