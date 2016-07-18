package pl.com.vsadga.service.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;
import pl.com.vsadga.dto.HttpProxy;

public class HttpReaderImpl implements HttpReader {
	private final String ZERO_DATE = "1970.01.01%2000:00";

	@Override
	public String readFromUrl(CurrencySymbol symbol, TimeFrame frame, Date actualDate, Date lastOpen,
			String accessKey, HttpProxy httpProxy) throws IOException {
		URL url = null;
		Proxy proxy = null;
		URLConnection conn = null;
		BufferedReader buff_reader = null;

		String inputLine = null;
		StringBuffer sb = new StringBuffer();

		try {
			url = new URL(buildHttpUrl(symbol, frame.getTimeFrame(), actualDate, lastOpen, accessKey));

			// czy połączenie poprzez proxy:
			if (httpProxy.isHttpProxy()) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(httpProxy.getProxyHost(),
						httpProxy.getProxyPort()));
				conn = url.openConnection(proxy);
			} else {
				conn = url.openConnection();
			}

			buff_reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((inputLine = buff_reader.readLine()) != null)
				sb.append(inputLine).append("\n");

		} finally {
			if (buff_reader != null)
				buff_reader.close();
		}

		return sb.toString();
	}

	private String buildHttpUrl(CurrencySymbol symbol, int timeFrame, Date actualDate, Date lastOpen,
			String accessKey) {
		StringBuilder sb = new StringBuilder();

		sb.append("http://generatedata.biz/mt4/smart.php");
		sb.append("?symbol=");
		sb.append(symbol.getSymbolName());
		sb.append("&timeframe=");
		sb.append(timeFrame);
		sb.append("&usertime=");
		sb.append(format(actualDate));

		sb.append("&lastopen=");
		if (lastOpen == null)
			sb.append(ZERO_DATE);
		else
			sb.append(format(lastOpen));

		sb.append("&accesskey=");
		sb.append(accessKey);
		sb.append("&extradata=");
		sb.append(symbol.getFuturesSymbol());

		sb.append("&lastloaded=");
		if (lastOpen == null)
			sb.append(ZERO_DATE);
		else
			sb.append(format(lastOpen));

		return sb.toString();
	}

	private String format(Date inputDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd%20HH:mm");

		return sdf.format(inputDate);
	}

}
