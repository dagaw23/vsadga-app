package pl.com.vsadga.service.io;

import java.util.Date;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public interface HttpReader {

	String readFromUrl(CurrencySymbol symbol, TimeFrame frame, Date actualDate, Date lastOpen, String accessKey);
}
