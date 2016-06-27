package pl.com.vsadga.service.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.com.vsadga.data.CurrencySymbol;
import pl.com.vsadga.data.TimeFrame;

public class HttpReaderImpl implements HttpReader {
	/**
	 * logger do zapisywania komunikatów do pliku log
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpReaderImpl.class);
	
	private final String ZERO_DATE = "1970.01.01%2000:00";
	
	private final String ACCESS_KEY = "";
	
	public static void main(String[] args) {
		HttpReaderImpl obj = new HttpReaderImpl();
		
		CurrencySymbol symbol = new CurrencySymbol();
		symbol.setSymbolName("EURUSD");
		symbol.setFuturesSymbol("6e");
		
		TimeFrame frame = new TimeFrame();
		frame.setTimeFrame(5);
		
		GregorianCalendar greg = new GregorianCalendar();
		greg.setTime(new Date());
		greg.add(Calendar.HOUR_OF_DAY, 1);
		
		obj.readFromUrl(symbol, frame, greg.getTime(), null, "88301876068826");
	}

	@Override
	public String readFromUrl(CurrencySymbol symbol, TimeFrame frame, Date actualDate, Date lastOpen, String accessKey) {
		
		HttpHost proxy = new HttpHost("proxy-usr.kir.pl", 8080, "http");
		RequestConfig config = RequestConfig.custom().setProxy(proxy);
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(buildHttpUrl(symbol, frame.getTimeFrame(), actualDate, lastOpen, accessKey));
		
		HttpResponse response = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		
		
		//HttpParams params = 
		
		//request.setParams();
		

		try {
			response = client.execute(request);
			isr = new InputStreamReader(response.getEntity().getContent());
			br = new BufferedReader(isr);

			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println(" > " + line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "test";
	}
	
	private String buildHttpUrl(CurrencySymbol symbol, int timeFrame, Date actualDate, Date lastOpen, String accessKey) {
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
