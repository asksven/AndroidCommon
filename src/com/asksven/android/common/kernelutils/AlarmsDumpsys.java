/**
 * 
 */
package com.asksven.android.common.kernelutils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.asksven.andoid.common.contrib.Util;
import com.asksven.android.common.privateapiproxies.Alarm;
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
	static final String PERMISSION_DENIED = "su rights required to access alarms are not available / were not granted";

	/**
	 * Returns a list of alarm value objects
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Alarm> getAlarms()
	{
		ArrayList<Alarm> myAlarms = null;
		long nTotalCount = 0;
		// ExecResult res = Exec.execPrint(new String[]{"/system/bin/su", "-c", "/system/bin/dumpsys alarm"});
//		ExecResult res = Exec.execPrint(new String[]{"su", "-c", "dumpsys alarm"});
		ArrayList<String> res = Util.run("su", "dumpsys alarm");
//		if (res.getSuccess())
		if (res.size() != 0)

		{
//			String strRes = res.getResultLine(); 
			if (true) //strRes.contains("Permission Denial"))
			{
				Pattern begin = Pattern.compile("Alarm Stats");
				boolean bParsing = false;
//				ArrayList<String> myRes = res.getResult(); // getTestData();

				// we are looking for multiline entries in the format
				// ' <package name>
				// '  <time> ms running, <number> wakeups
				// '  <number> alarms: act=<intent name> flg=<flag> (repeating 1..n times)
				Pattern packagePattern 	= Pattern.compile("\\s\\s([a-z][a-zA-Z0-9\\.]+)");
				Pattern timePattern 	= Pattern.compile("\\s\\s(\\d+)ms running, (\\d+) wakeups");
				Pattern numberPattern	= Pattern.compile("\\s\\s(\\d+) alarms: act=([A-Za-z0-9\\-\\_\\.]+)");
				
				myAlarms = new ArrayList<Alarm>();
				Alarm myAlarm = null;
				
				// process the file
				for (int i=0; i < res.size(); i++)
				{
					// skip till start mark found
					if (bParsing)
					{
						// parse the alarms by block 
						String line = res.get(i);
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
									nTotalCount += nWakeups;
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
						Matcher line = begin.matcher(res.get(i));
						if (line.find())
						{
							bParsing = true;
						}
					}
				}
				// the last populated alarms has not been added to the list yet
				myAlarms.add(myAlarm);
				
			}
			else
			{
				myAlarms = new ArrayList<Alarm>();
				Alarm myAlarm = new Alarm(PERMISSION_DENIED);
				myAlarm.setWakeups(1);
				myAlarms.add(myAlarm);
			}
		}
		else
		{
			myAlarms = new ArrayList<Alarm>();
			Alarm myAlarm = new Alarm(PERMISSION_DENIED);
			myAlarm.setWakeups(1);
			myAlarms.add(myAlarm);

		}
		
		
		for (int i=0; i < myAlarms.size(); i++)
		{
			myAlarms.get(i).setTotalCount(nTotalCount);
		}
		return myAlarms;
	}
	
	static ArrayList<String> getTestData()
	{
		ArrayList<String> myRet = new ArrayList<String>()
				{{
					add("Alarm Stats:");
					add("  com.google.android.gsf");
					add("  8417ms running, 204 wakeups");
					add("  17 alarms: act=com.google.android.intent.action.GTALK_RECONNECT flg=0x4");
					add("  187 alarms: flg=0x4");
//						  com.anod.calendar
//						    311ms running, 0 wakeups
//						    4 alarms: act=android.appwidget.action.APPWIDGET_UPDATE dat=com.anod.calendar://widget/id/45 flg=0x4
//						  com.yahoo.mobile.client.android.mail
//						    1248ms running, 96 wakeups
//						    2 alarms: act=com.yahoo.android.push.Timer_VitalizeSessionybres89mail flg=0x4 cmp=com.yahoo.mobile.client.android.mail/com.yahoo.mobile.client.share.push.HTTPKeepAliveService
//						    2 alarms: act=com.yahoo.android.push.Connection_Watchdogybres89mail flg=0x4 cmp=com.yahoo.mobile.client.android.mail/com.yahoo.mobile.client.share.push.HTTPKeepAliveService
//						    92 alarms: act=com.yahoo.android.push.Connection_Recoveryybres89mail flg=0x4 cmp=com.yahoo.mobile.client.android.mail/com.yahoo.mobile.client.share.push.HTTPKeepAliveService
//						  com.android.vending
//						    6669ms running, 0 wakeups
//						    12 alarms: act=com.android.vending.FORCE_UPDATE_CHECK flg=0x4
//						  com.moctav.weather
//						    3783ms running, 0 wakeups
//						    94 alarms: act=Autoupdate flg=0x4 cmp=com.moctav.weather/.WeatherUpdateService
//						  android
//						    517606ms running, 137 wakeups
//						    4 alarms: act=android.intent.action.DATE_CHANGED flg=0x30000004
//						    70 alarms: act=com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD flg=0x4
//						    5599 alarms: act=android.intent.action.TIME_TICK flg=0x40000004
//						    557 alarms: act=com.android.server.ThrottleManager.action.POLL flg=0x4
//						    54 alarms: act=android.app.backup.intent.RUN flg=0x40000004
//						    13 alarms: act=android.content.syncmanager.SYNC_ALARM flg=0x4
//						  com.whatsapp
//						    36757ms running, 11 wakeups
//						    4 alarms: act=ALARM_REPORT_SYNCS flg=0x4
//						    4 alarms: act=ALARM_MESSAGES_DB_BACKUP flg=0x4
//						    4 alarms: act=ALARM_ROTATE_LOGS flg=0x4
//						    2 alarms: act=ALARM_AVAILABLE_TIMEOUT flg=0x4
//						    114 alarms: act=ALARM_ACTION flg=0x4
//						    1 alarms: act=com.whatsapp.MessageService.RECONNECT flg=0x4 cmp=com.whatsapp/.messaging.MessageService
//						  com.google.android.apps.maps
//						    2238ms running, 93 wakeups
//						    93 alarms: flg=0x4 cmp=com.google.android.apps.maps/com.google.googlenav.prefetch.android.PrefetcherService
//						  com.android.providers.calendar
//						    344ms running, 4 wakeups
//						    4 alarms: act=com.android.providers.calendar.SCHEDULE_ALARM flg=0x4
//						  com.android.deskclock
//						    1219ms running, 4 wakeups
//						    4 alarms: act=com.android.deskclock.ALARM_ALERT flg=0x4
						add("  com.carl.trafficcounter");
						add("  446486ms running, 5584 wakeups");
						add("  5584 alarms: act=com.carl.trafficcounter.UPDATE_RUN flg=0x4");
				}};

		return myRet;
	}
}
