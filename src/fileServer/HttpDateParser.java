package fileServer;

import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This is a help class for converting a HTTP-date string to Date.
 * 
 * @author Jiaqi LI
 * @since 25/11/2012
 * @version 1
 * 
 */
public class HttpDateParser {

	/**
	 * Convert a HTTP-date string to Date. This method accept the format of RFC
	 * 822, RFC 850 and ANSI C's format.
	 * 
	 * @param dateString
	 *            the date in the format of RFC 822, RFC 850 or ANSI C's format.
	 * @return a Date representation of that date, return null if the input date
	 *         is not understandable.
	 */
	public static Date parseHttpDate(String dateString) {

		if (dateString == null) {
			return null;
		}

		Date date;
		String dayOfWeek, dayOfMonth, month, year, hour, minute, second;

		StringTokenizer tokens = new StringTokenizer(dateString, " s,:-");

		if (tokens.countTokens() == 8) {
			// RFC 850 or RFC 822 format
			dayOfWeek = tokens.nextToken();
			dayOfMonth = tokens.nextToken();
			month = tokens.nextToken();
			year = tokens.nextToken();
			hour = tokens.nextToken();
			minute = tokens.nextToken();
			second = tokens.nextToken();
		} else if (tokens.countTokens() == 7) {
			// ANSI C's asctime() format
			dayOfWeek = tokens.nextToken();
			month = tokens.nextToken();
			dayOfMonth = tokens.nextToken();
			hour = tokens.nextToken();
			minute = tokens.nextToken();
			second = tokens.nextToken();
			year = tokens.nextToken();
		} else {
			// The parameter is not a correct date format.
			return null;
		}

		try {
			if (year.length() == 2) {
				// RFC 850
				year = (Integer.valueOf(year) < 70) ? "19" + year : "20" + year;
			}
			date = new Date(
					Integer.valueOf(year),
					Integer.valueOf(monthStringToInt.get(month.toLowerCase())) + 1,
					Integer.valueOf(dayOfMonth), Integer.valueOf(hour), Integer
							.valueOf(minute), Integer.valueOf(second));
			return date;
		} catch (Exception e) {
			// Date format is not understood.
			return null;
		}
	}

	/**
	 * Map from month string to int.
	 */
	private static final HashMap<String, Integer> monthStringToInt = new HashMap<String, Integer>() {
		{
			put("jan", 0);
			put("feb", 1);
			put("mar", 2);
			put("apr", 3);
			put("may", 4);
			put("jun", 5);
			put("jul", 6);
			put("aug", 7);
			put("sep", 8);
			put("oct", 9);
			put("nov", 10);
			put("dec", 11);
		}
	};
}
