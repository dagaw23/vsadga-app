package pl.com.vsadga.processor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.xstore.api.message.records.SCandleRecord;
import pro.xstore.api.streaming.StreamingListener;

public class DataStreamListener extends StreamingListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamListener.class);

	@Override
	public void receiveCandleRecord(SCandleRecord record) {
		LOGGER.info("   [CANDLE] " + record.getSymbol() + ": high=" + record.getHigh() + ", low=" + record.getLow()
				+ ", close=" + record.getClose() + ", ctm=" + record.getCtmString() + ", vol=" + record.getVol() + ".");
	}
}
