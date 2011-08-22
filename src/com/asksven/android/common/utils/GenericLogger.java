/**
 * 
 */
package com.asksven.android.common.utils;

import android.util.Log;

import com.asksven.android.common.utils.DataStorage;

/**
 * @author sven
 *
 */
public class GenericLogger
{

	/** The application's own logfile */
	public static String LOGFILE = "androidcommon.log";

	public static void d(String strTag, String strMessage)
	{
		Log.d(strTag, strMessage);
		DataStorage.LogToFile(LOGFILE, strMessage);
	}
	
	public static void e(String strTag, String strMessage)
	{
		Log.e(strTag, strMessage);
		DataStorage.LogToFile(LOGFILE, strMessage);
	}

	public static void i(String strTag, String strMessage)
	{
		Log.i(strTag, strMessage);
		DataStorage.LogToFile(LOGFILE, strMessage);
	}

	private static void writeLog(String strTag, String strMessage)
	{
		DataStorage.LogToFile(LOGFILE, strMessage);
	}
}
