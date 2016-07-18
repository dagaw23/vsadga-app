package pl.com.vsadga.service.io;

import java.io.IOException;
import java.util.Date;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.HttpProxy;

public interface HttpReader {

	String readFromUrl(CurrencySymbol symbol, TimeFrame frame, Date actualDate, Date lastOpen, String accessKey,
			HttpProxy httpProxy) throws IOException;
}
