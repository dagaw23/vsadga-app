package pl.com.vsadga.web.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

	public static String formatSimpleDate(Date inputDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
		
		return sdf.format(inputDate);
	}
}
