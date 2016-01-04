package pl.com.vsadga.service.symbol;

import java.sql.Timestamp;

public interface CurrencyWritedService {

	public Timestamp getWritedTime(Integer symbolId, Integer timeFrameId);
}
