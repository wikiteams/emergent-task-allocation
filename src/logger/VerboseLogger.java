package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VerboseLogger {

	/**
	 * Lets avoid in the normal situations outputting all the debug messages
	 * to a log file, it can grow enormous big, so we want to disable it
	 * for every day use..
	 * 
	 * FORCE_NO_DEBUG = true; will enable diagnostic messages
	 */
	private static boolean FORCE_NO_DEBUG = false;

	public static void say(String s) {
		if (FORCE_NO_DEBUG) {
			String dateStringRepresentation = getDateLogs();
			System.out.println(dateStringRepresentation + ": " + s);
			PjiitLogger.info(dateStringRepresentation + ": " + s);
		}
	}

	public static void sanity(String s) {
		if (FORCE_NO_DEBUG) {
			String dateStringRepresentation = getDateLogs();
			System.out.println(dateStringRepresentation + ": " + s);
			SanityLogger.sanity(dateStringRepresentation + ": " + s);
		}
	}

	private static String getDateLogs() {
		return new SimpleDateFormat("DD/MM/yyyy HH:mm").format(new Date());
	}
}
