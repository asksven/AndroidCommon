/**
 * 
 */
package com.asksven.android.common.kernelutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;








//import com.asksven.andoid.common.contrib.Shell;
import com.asksven.andoid.common.contrib.Util;
import com.asksven.android.common.RootShell;
import com.asksven.android.common.privateapiproxies.Alarm;
import com.asksven.android.common.privateapiproxies.BatteryStatsTypes;
import com.asksven.android.common.privateapiproxies.StatElement;
import com.asksven.android.common.privateapiproxies.Wakelock;
import com.asksven.android.common.shellutils.Exec;
import com.asksven.android.common.shellutils.ExecResult;
import com.asksven.android.common.utils.DateUtils;

/**
 * Parses the content of 'dumpsys battery'
 * @author sven
 */
public class PartialWakelocksDumpsys
{
	static final String TAG = "PartialWakelocksDumpsys";
	static final String PERMISSION_DENIED = "su rights required to access alarms are not available / were not granted";

	/**
	 * Returns a list of alarm value objects
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<StatElement> getPartialWakelocks(Context ctx)
	{
		
		// get the list of all installed packages
		PackageManager pm = ctx.getPackageManager();
		List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_META_DATA);
		
		HashMap<String, Integer> xrefPackages = new HashMap<String, Integer>();
		
		for (int i=0; i < apps.size(); i++)
		{
			xrefPackages.put(apps.get(i).packageName, apps.get(i).uid);
		}
		
		final String START_PATTERN = "Statistics since last charge";
		final String STOP_PATTERN = "Statistics since last unplugged";
		
		ArrayList<StatElement> myWakelocks = null;

		List<String> res = RootShell.getInstance().run("dumpsys batterystats");
		//List<String> res = getTestData();

		HashMap<String, String> xrefUserNames = getPackages(res);

		if ((res != null) && (res.size() != 0))

		{
			if (true) //strRes.contains("Permission Denial"))
			{
				Pattern begin = Pattern.compile(START_PATTERN);
				Pattern end = Pattern.compile(STOP_PATTERN);
				
				boolean bParsing = false;

				// we are looking for singlr line entries in the format
				// Wake lock 1001 RILJ: 1h 8m 23s 575ms (930 times) realtime
				// Wake lock 1013 AudioMix: 26m 33s 343ms (10 times) realtime
//				' <package name>
				// '  <time> ms running, <number> wakeups
				// '  <number> alarms: act=<intent name> flg=<flag> (repeating 1..n times)
				Pattern pattern	= Pattern.compile("\\s\\sWake lock\\s([a-z0-9]+)\\s(.*): ([a-z0-9\\s]+)ms \\((\\d+) times\\).*");
				
				myWakelocks = new ArrayList<StatElement>();
				Wakelock myWl = null;
				
				// process the file
				long total = 0;
				for (int i=0; i < res.size(); i++)
				{
					// skip till start mark found
					if (bParsing)
					{
						// look for end
						Matcher endMatcher = end.matcher(res.get(i));
						if (endMatcher.find())
						{
							break;
						}
						
						// parse the alarms by block 
						String line = res.get(i);
						Matcher mPackage 	= pattern.matcher(line);
						
						// first line
						if ( mPackage.find() )
						{
							try
							{
								String id 		= mPackage.group(1);
								String wakelock = mPackage.group(2);
								long duration 	= DateUtils.durationToLong(mPackage.group(3));
								int times 		= Integer.valueOf(mPackage.group(4));

								// get the package associated with id
								String packageName = xrefUserNames.get(id);
								// get the uid for that package
								int uid = xrefPackages.get(packageName);
								
								myWl = new Wakelock(BatteryStatsTypes.WAKE_TYPE_PARTIAL, wakelock, duration, 0, times);
								myWl.setUid(uid);
								
								total += duration;
								myWakelocks.add(myWl);
								Log.i(TAG, "Adding partial wakelock: " + myWl.getData());
							}
							catch (Exception e)
							{
								Log.e(TAG, "Error: parsing error in package line (" + line + ")");
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
				
				// set the total
				for (int i=0; i < myWakelocks.size(); i++)
				{
					myWakelocks.get(i).setTotal(total);
				}
			}
			else
			{
				myWakelocks = new ArrayList<StatElement>();
				Wakelock myWl = new Wakelock(0, PERMISSION_DENIED, 1, 1, 0);
				myWakelocks.add(myWl);
			}
		}
		else
		{
			myWakelocks = new ArrayList<StatElement>();
			Wakelock myWl = new Wakelock(0, PERMISSION_DENIED, 1, 1, 0);
			myWakelocks.add(myWl);

		}
		
		return myWakelocks;
	}


	static ArrayList<String> getTestData()
	{
		ArrayList<String> myRet = new ArrayList<String>()
				{{
					add("Alarm Stats:");
					add("  All partial wake locks:");
					add("  Wake lock 1001 RILJ: 1h 8m 23s 575ms (930 times) realtime");
					add("  Wake lock 1013 AudioMix: 26m 33s 343ms (10 times) realtime");
					add("  Wake lock u0a203 android.media.MediaPlayer: 26m 20s 380ms (3 times) realtime");
					add("  Wake lock u0a203 pocketcasts_wake_lock: 26m 19s 956ms (3 times) realtime");
					add("  Wake lock u0a18 NlpCollectorWakeLock: 5m 1s 608ms (347 times) realtime");
					add("  Wake lock u0a18 NlpWakeLock: 1m 58s 440ms (1473 times) realtime");
					add("  Wake lock u0a18 Checkin Service: 1m 36s 820ms (47 times) realtime");
					add("  Wake lock u0a203 pocketcasts_update_wake_lock: 44s 69ms (5 times) realtime");
					add("  Wake lock 1000 ActivityManager-Launch: 27s 214ms (72 times) realtime");
					add("  Wake lock u0a18 WakefulIntentService[GCoreUlr-LocationReportingService]: 27s 108ms (11 times) realtime");
					add("  Wake lock u0a47 StartingAlertService: 23s 785ms (15 times) realtime");
					add("  Wake lock u0a59 *sync*/gmail-ls/com.google/sven.knispel@gmail.com: 17s 777ms (6 times) realtime");
					add("  Wake lock 1000 AlarmManager: 17s 235ms (193 times) realtime");
					add("  Wake lock u0a18 Icing: 14s 250ms (45 times) realtime");
					add("  Wake lock u0a18 GCM_CONN_ALARM: 13s 467ms (25 times) realtime");
					add("  Wake lock u0a18 ezk: 11s 653ms (136 times) realtime");
					add("  Wake lock u0a178 AlarmManager: 10s 671ms (162 times) realtime");

				}};

		return myRet;
	}
	
	private static HashMap<String, String> getPackages(List<String> res)
	{
		HashMap<String, String> xref = new HashMap<String, String>();
				
		final String START_PATTERN = "Statistics since last charge";
		final String STOP_PATTERN = "Statistics since last unplugged";
		
		if ((res != null) && (res.size() != 0))

		{
			Pattern begin = Pattern.compile(START_PATTERN);
			Pattern end = Pattern.compile(STOP_PATTERN);
			
			boolean bParsing = false;

			Pattern patternUser	= Pattern.compile("\\s\\s(u0a\\d+):");
			Pattern patternPackage	= Pattern.compile("\\s\\s\\s\\sApk\\s(.*):");

			String user = "";
			for (int i=0; i < res.size(); i++)
			{
				// skip till start mark found
				if (bParsing)
				{

					// look for end
					Matcher endMatcher = end.matcher(res.get(i));
					if (endMatcher.find())
					{
						break;
					}

					String line = res.get(i);
					Matcher mUser 	= patternUser.matcher(line);
					Matcher mApk 	= patternPackage.matcher(line);
					
					if ( mUser.find() )
					{
						user = mUser.group(1);
					}
					if ( mApk.find() )
					{
						xref.put(user, mApk.group(1));
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
		}
		
		return xref;
	}
}
