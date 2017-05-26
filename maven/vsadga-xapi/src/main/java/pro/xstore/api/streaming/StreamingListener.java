package pro.xstore.api.streaming;

import pro.xstore.api.message.records.SBalanceRecord;
import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.message.records.SKeepAliveRecord;
import pro.xstore.api.message.records.SNewsRecord;
import pro.xstore.api.message.records.SProfitRecord;
import pro.xstore.api.message.records.STickRecord;
import pro.xstore.api.message.records.STradeRecord;
import pro.xstore.api.message.records.STradeStatusRecord;

public class StreamingListener implements StreamingListenerInterface {

	@Override
	public void receiveTradeRecord(STradeRecord tradeRecord) {
		throw new UnsupportedOperationException(
				"listener method not implemented for receiveTradeRecord(tradeRecord)");
	}

	@Override
	public void receiveTickRecord(STickRecord tickRecord) {
		if (tickRecord.getLevel() == 0)
			System.out.println("Current price: [" + tickRecord.getAsk() + "].");

	}

	@Override
	public void receiveBalanceRecord(SBalanceRecord balanceRecord) {
		throw new UnsupportedOperationException(
				"listener method not implemented for receiveBalanceRecord(balanceRecord)");
	}

	@Override
	public void receiveNewsRecord(SNewsRecord newsRecord) {
		throw new UnsupportedOperationException(
				"listener method not implemented for receiveNewsRecord(newsRecord)");
	}

	@Override
	public void receiveKeepAliveRecord(SKeepAliveRecord keepAliveRecord) {
		throw new UnsupportedOperationException(
				"listener method not implemented for receiveKeepAliveRecord(keepAliveRecord)");
	}

	@Override
	public void receiveCandleRecord(SCandleRecord candleRecord) {
		System.out.println("Candle price: [" + candleRecord.getClose() + "], czas [" + candleRecord.getCtmString()
				+ "], wolumen: [" + candleRecord.getVol() + "].");

	}

	@Override
	public void receiveTradeStatusRecord(STradeStatusRecord tradeStatusRecord) {
		throw new UnsupportedOperationException(
				"listener method not implemented for receiveTradeStatusRecord(tradeStatusRecord)");
	}

	@Override
	public void receiveProfitRecord(SProfitRecord profitRecord) {
		throw new UnsupportedOperationException(
				"listener method not implemented for receiveProfitRecord(profitRecord)");
	}
}