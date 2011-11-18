/**
 * 
 */
package com.asksven.android.common.kernelutils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.asksven.android.common.shellutils.Exec;
import com.asksven.android.common.shellutils.ExecResult;

/**
 * Parses the content of 'dumpsys alarm'
 * processes the result of 'dumpsys alarm' as explained in KB article
 * https://github.com/asksven/BetterBatteryStats-Knowledge-Base/wiki/AlarmManager
 * @author sven
 */
public class AlarmsDumpsys
{
	static final String TAG = "AlarmsDumpsys";

	/**
	 * Returns a list of alarm value objects
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Alarm> getAlarms() throws Exception
	{
		ArrayList<Alarm> myAlarms = null;
		// ExecResult res = Exec.execPrint(new String[]{"/system/bin/su", "-c", "/system/bin/dumpsys alarm"});
		ExecResult res = Exec.execPrint(new String[]{"su", "-c", "dumpsys alarm"});
		if (res.getSuccess())
		{
			String strRes = res.getResultLine(); 
			if (strRes.contains("Permission Denial"))
			{
				Exception e = new RuntimeException("Permission Denied");
			}
			else
			{
				Pattern begin = Pattern.compile("Alarm Stats");
				boolean bParsing = false;
				ArrayList<String> myRes = res.getResult();

				// we are looking for multiline entries in the format
				// ' <package name>
				// '  <time> ms running, <number> wakeups
				// '  <number> alarms: act=<intent name> flg=<flag> (repeating 1..n times)
				Pattern packagePattern 	= Pattern.compile("\\s\\s([a-z\\.]+)");
				Pattern timePattern 	= Pattern.compile("\\s\\s(\\d+)ms running, (\\d+) wakeups");
				Pattern numberPattern	= Pattern.compile("\\s\\s(\\d+) alarms: act=([A-Za-z0-9\\-\\_\\.]+)");
				
				myAlarms = new ArrayList<Alarm>();
				Alarm myAlarm = null;
				
				// process the file
				for (int i=0; i < myRes.size(); i++)
				{
					// skip till start mark found
					if (bParsing)
					{
						// parse the alarms by block 
						String line = myRes.get(i);
						Matcher mPackage 	= packagePattern.matcher(line);
						Matcher mTime 		= timePattern.matcher(line);
						Matcher mNumber 	= numberPattern.matcher(line);
						
						// first line
						if ( mPackage.find() )
						{
							try
							{
								// if there was a previous Alarm populated store it
								if (myAlarm != null)
								{
									myAlarms.add(myAlarm);
								}
								// we are interested in the first token 
								String strPackageName = mPackage.group(1);
								myAlarm = new Alarm(strPackageName);
							}
							catch (Exception e)
							{
								Log.e(TAG, "Error: parsing error in package line (" + line + ")");
							}
						}

						// second line
						if ( mTime.find() )
						{
							try
							{
								// we are interested in the second token
								String strWakeups = mTime.group(2);
								long nWakeups = Long.parseLong(strWakeups);
	
								if (myAlarm == null)
								{
									Log.e(TAG, "Error: time line found but without alarm object (" + line + ")");
								}
								else
								{
									myAlarm.setWakeups(nWakeups);
								}
							}
							catch (Exception e)
							{
								Log.e(TAG, "Error: parsing error in time line (" + line + ")");
							}
						}

						// third line (and following till next package
						if ( mNumber.find() )
						{
							try
							{
								// we are interested in the first and second token
								String strNumber = mNumber.group(1);
								String strIntent = mNumber.group(2);
								long nNumber = Long.parseLong(strNumber);
	
								if (myAlarm == null)
								{
									Log.e(TAG, "Error: number line found but without alarm object (" + line + ")");
								}
								else
								{
									myAlarm.addItem(nNumber, strIntent);
								}
							}
							catch (Exception e)
							{
								Log.e(TAG, "Error: parsing error in number line (" + line + ")");
							}
						}
					}
					else
					{
						// look for beginning
						Matcher line = begin.matcher(myRes.get(i));
						if (line.find())
						{
							bParsing = true;
						}
					}
				}
			}
		}
		return myAlarms;
	}
}
