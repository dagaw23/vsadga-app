package pl.com.vsadga.io.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.dto.Mt4FileRecord;
import pl.com.vsadga.io.BarDataFileReader;
import pl.com.vsadga.io.BaseReader;
import pl.com.vsadga.io.ReaderException;
import pl.com.vsadga.utils.DateConverter;

public class Mt4DataFileReaderImpl extends BaseReader implements BarDataFileReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(Mt4DataFileReaderImpl.class);

	public Mt4DataFileReaderImpl(String encode) {
		super(encode);
	}

	@Override
	public List<Mt4FileRecord> readAll(String path, String symbol, String tmeFrame) throws ReaderException,
			ParseException {
		String one_line = null;
		List<Mt4FileRecord> rec_list = new ArrayList<Mt4FileRecord>();

		try {
			openStreams(path + File.separator + getFileName(symbol, tmeFrame));

			while ((one_line = readLine()) != null)
				rec_list.add(convert(one_line));

			return rec_list;

		} finally {
			closeStreams();
		}
	}

	private Mt4FileRecord convert(String record) throws ParseException {
		Mt4FileRecord mt4_rec = new Mt4FileRecord();
		String[] rec_tab = record.split(";");

		if (rec_tab.length != 6) {
			LOGGER.error("Bledny rozmiar [" + rec_tab.length + "] zawartosci rekordu [" + record + "].");
			return null;
		}

		mt4_rec.setBarTime(DateConverter.stringToGregorian(rec_tab[0], "yyyy.MM.dd HH:mm:ss"));
		mt4_rec.setBarHigh(new BigDecimal(rec_tab[1]));
		mt4_rec.setBarLow(new BigDecimal(rec_tab[2]));

		mt4_rec.setBarClose(new BigDecimal(rec_tab[3]));
		mt4_rec.setBarVolume(Integer.valueOf(rec_tab[4]));
		mt4_rec.setVolumeType("T");
		mt4_rec.setImaCount(new BigDecimal(rec_tab[5]));

		return mt4_rec;
	}

	private String getFileName(String symbol, String tmeFrame) {
		return symbol + "_" + tmeFrame;
	}

}
