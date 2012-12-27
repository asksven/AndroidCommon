/**
 * 
 */
package com.asksven.android.common.kernelutils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.asksven.andoid.common.CommonLogSettings;
import com.asksven.andoid.common.contrib.Util;
import com.asksven.android.common.privateapiproxies.NetworkUsage;
import com.asksven.android.common.privateapiproxies.StatElement;
import com.asksven.android.common.shellutils.Exec;
import com.asksven.android.common.shellutils.ExecResult;
import com.asksven.android.common.utils.StringUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;


/**
 * @author sven
 *
 */
public class WakeupSources extends Wakelocks
{
    private final static String TAG ="WakeupSources";
    private static String FILE_PATH = "/sys/kernel/debug/wakeup_sources";
    //private static String FILE_PATH = "/sdcard/wakeup_sources.txt";
    
    public static ArrayList<StatElement> parseWakeupSources(Context context)
    {
    	Log.i(TAG, "Parsing " + FILE_PATH);
       	
    	String delimiter = String.valueOf('\t');
    	delimiter = delimiter + "+";
    	ArrayList<StatElement> myRet = new ArrayList<StatElement>();
    	// format 
    	// new [name	active_count	event_count		wakeup_count	expire_count	active_since	total_time	max_time	last_change	prevent_suspend_time]
    	ArrayList<String[]> rows = parseDelimitedFile(FILE_PATH, delimiter);

    	long msSinceBoot = SystemClock.elapsedRealtime();

		// list the running processes
		ActivityManager actvityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();

    	// start with 1
    	for (int i=1; i < rows.size(); i++ )
    	{
    		try
    		{
    			// times in file are milliseconds
    			String[] data = (String[]) rows.get(i);
    			String name = data[0]; 								// name
    			int count = Integer.valueOf(data[1]);				// active_count
    			int expire_count = Integer.valueOf(data[4]);		// expire_count
    			int wake_count = Integer.valueOf(data[3]);			// wakeup_count
    			long active_since = Long.valueOf(data[5]);			// active_since
    			long total_time = Long.valueOf(data[6]) / 1000;	// total_time
    			long sleep_time = Long.valueOf(data[9]) / 1000;	// prevent_suspend_time
    			long max_time = Long.valueOf(data[7]) / 1000;	// max_time
    			long last_change = Long.valueOf(data[8]);			// last_change
    			
				// post-processing of eventX-YYYY processes
				String details = "";

				// we start with a " here as that is the way the data comes from /proc
				if (name.startsWith("\"event"))
				{
					String process = name.replaceAll("\"", "");
					if (CommonLogSettings.DEBUG)
					{
						Log.d(TAG, "Pattern 'event' found in " + process);
					}
					
					int proc = 0;
					String[] parts = process.split("-");
					if (parts.length == 2)
					{
						try
						{
							proc = Integer.valueOf(parts[1]);
							if (CommonLogSettings.DEBUG)
							{
								Log.d(TAG, "Resolving proc name for 'event' " + proc);
							}
						}
						catch (Exception e)
						{
							Log.e(TAG, "Cound not split process name " + process);
						}
					}
					
					if (proc != 0)
					{
						// search for the process in the task list
						for (int psCount = 0; psCount < procInfos.size(); psCount++)
						{
							int id = procInfos.get(psCount).pid; 
							if ( id == proc)
							{
								String processName = procInfos.get(psCount).processName;

								details= processName; 
								String appName = "";

								String[] pkgList = procInfos.get(count).pkgList;
								for (int j=0; j < pkgList.length; j++)
								{
									if (details.length() > 0)
									{
										details += ", ";
									}
									details += pkgList[j];
								}

								if (CommonLogSettings.DEBUG)
								{
									Log.d(TAG, "Pattern 'event' resolved to " + details);
								}
							}
						}
					}
				}

    			NativeKernelWakelock wl = new NativeKernelWakelock(
    					name, details, count, expire_count, wake_count,
    					active_since, total_time, sleep_time, max_time,
    					last_change, msSinceBoot);
    			myRet.add(wl);
    		}
    		catch (Exception e)
    		{
    			// go on
    		}
    	}
    	return myRet;
    }
}