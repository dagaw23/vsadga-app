package pl.com.frxdream.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Konwerter dat.
 * 
 */
public final class DateConverter {

	/**
	 * Standardowy wzorzec daty.
	 */
	public static final String STANDARD_DATE_PATTERN = "yyyyMMdd";

	/**
	 * Narzędzia.
	 */
	private static final int DAY_OF_MONTH31 = 31;

	/**
	 * liczba milisekund w dniu.
	 */
	private static final long MILISECONDS_IN_DAY = 86400000L;

	/**
	 * Narzędzia.
	 */
	private static final int MONTH11 = 11;

	/**
	 * Liczba dni pomiędzy [1 stycznia 1900, a 1 stycznia 1970r).
	 */
	private static final long NUMBER_OF_DAYS_FIRST_JAN_1970 = 25567L;

	/**
	 * Narzędzia.
	 */
	private static final int UNDER_TEN = 10;

	/**
	 * Zwraca datę jako napis YYYY/MM/DD.
	 * 
	 * @param date
	 *            data do wypisania
	 * @return sformatowany data
	 */
	public static String asString(final GregorianCalendar date) {
		StringBuilder builder = new StringBuilder();
		builder.append(date.get(Calendar.YEAR));
		builder.append("/");
		if (date.get(Calendar.MONTH) < UNDER_TEN) {
			builder.append("0");
		}
		builder.append(date.get(Calendar.MONTH));
		builder.append("/");
		if (date.get(Calendar.DAY_OF_MONTH) < UNDER_TEN) {
			builder.append("0");
		}
		builder.append(date.get(Calendar.DAY_OF_MONTH));
		return builder.toString();
	}

	/**
	 * Metoda konwertuje przesłaną datę w postaci {@link Date} na reprezentację tekstową z
	 * wykorzystaniem wzorca konwersji. Wykorzystywany jest wzorzec domyslny <code>yyyyMMdd</code>.
	 * 
	 * @param date
	 *            data wejściowa w postaci obiektu typu {@link Date}
	 * @return tekstowa postać przesłanej daty wejściowej - z wykorzystaniem wzorca konwersji
	 */
	public static String dateToString(final Date date) {
		SimpleDateFormat df = null;
		df = new SimpleDateFormat(STANDARD_DATE_PATTERN);
		return df.format(date);
	}

	/**
	 * Metoda konwertuje przesłaną datę w postaci {@link Date} na reprezentację tekstową z
	 * wykorzystaniem wzorca konwersji. Jeżeli wzorzec jest <code>null</code> to wykorzystywany jest
	 * wzorzec domyslny <code>yyyyMMdd</code>.
	 * 
	 * @param date
	 *            data wejściowa w postaci obiektu typu {@link Date}
	 * @param pattern
	 *            wzorzec konwersji daty wejściowej na typ tekstowy
	 * @return tekstowa postać przesłanej daty wejściowej - z wykorzystaniem wzorca konwersji
	 */
	public static String dateToString(final Date date, final String pattern) {
		SimpleDateFormat df = getDateFormatter(pattern);
		return df.format(date);
	}

	/**
	 * Zwraca datę bez części godzinowej
	 * @param date
	 * 			obiekt {@link Date} w którym ma być obcięta część godzinowa 
	 * @return
	 */
	private static Date cutOffTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();		
	}
	
	/**
	 * Zamienia datę z postaci YYYYMMDD na {@link GregorianCalendar}.
	 * 
	 * @param date
	 *            data w postaci YYYYMMDD
	 * @return data jako {@link GregorianCalendar}
	 */
	public static GregorianCalendar stringToGregorianCalendar(final String date) {
		Integer year = new Integer(date.substring(0, 4));
		Integer month = new Integer(date.substring(4, 6)) - 1;
		Integer dayOfMonth = new Integer(date.substring(6, 8));
		return new GregorianCalendar(year, month, dayOfMonth);
	}

	/**
	 * Metoda pobiera datę w postaci tekstowej i dla podanego wzorca wejściowego konwertuje tę datę
	 * na typ {@link Date}.
	 * 
	 * @param date
	 *            data wejściowa w postaci tekstowej
	 * @param pattern
	 *            wzorzec daty wejściowej
	 * @return data wynikowa w postaci {@link Date}
	 * @throws ParseException
	 *             wyjątek zwracany w przypadku parsowania daty z postaci tekstowej na {@link Date}
	 */
	public static Date stringToDate(final String date, final String pattern) throws ParseException {
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
		if (pattern != null) {
			df = new SimpleDateFormat(pattern);
		}
		String replaceAll = date.replaceAll("^0{0,4}", "");
		return df.parse(replaceAll);
	}

	/**
	 * Tworzy {@link java.text.DateFormat;} na podstawie wzorca.
	 * 
	 * @param pattern wzorzec.
	 * @return formater.
	 */
	private static SimpleDateFormat getDateFormatter(final String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(STANDARD_DATE_PATTERN);
		if (pattern != null) {
			df = new SimpleDateFormat(pattern);
		}
		return df;
	}

}
