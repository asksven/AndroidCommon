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
import java.util.Map;

import android.os.SystemClock;


/**
 * @author sven
 *
 */
public class Wakelocks
{

    static final int PROC_TERM_MASK = 0xff;
    static final int PROC_ZERO_TERM = 0;
    static final int PROC_SPACE_TERM = (int)' ';
    static final int PROC_TAB_TERM = (int)'\t';
    static final int PROC_COMBINE = 0x100;
    static final int PROC_PARENS = 0x200;
    static final int PROC_OUT_STRING = 0x1000;
    static final int PROC_OUT_LONG = 0x2000;
    static final int PROC_OUT_FLOAT = 0x4000;

    private static int sKernelWakelockUpdateVersion = 0;
    
    private static final int[] PROC_WAKELOCKS_FORMAT = new int[] {
        PROC_TAB_TERM|PROC_OUT_STRING,                // 0: name
        PROC_TAB_TERM|PROC_OUT_LONG,                  // 1: count
        PROC_TAB_TERM,PROC_OUT_LONG,
        PROC_TAB_TERM,PROC_OUT_LONG,
        PROC_TAB_TERM,PROC_OUT_LONG,
        PROC_TAB_TERM|PROC_OUT_LONG			          // 5: totalTime
    };
    
    public static ArrayList<NativeKernelWakelock> parseProcWakelocks()
    {
       	String filePath = "/proc/wakelocks";
    	String delimiter = String.valueOf('\t');
    	ArrayList<NativeKernelWakelock> myRet = new ArrayList<NativeKernelWakelock>();
    	// format 
    	// [name, count, expire_count, wake_count, active_since, total_time, sleep_time, max_time, last_change]
    	ArrayList<String[]> rows = parseDelimitedFile(filePath, delimiter);

    	long msSinceBoot = SystemClock.elapsedRealtime();

    	// start with 1
    	for (int i=1; i < rows.size(); i++ )
    	{
    		try
    		{
    			String[] data = (String[]) rows.get(i);
    			String name = data[0];
    			int count = Integer.valueOf(data[1]);
    			int expire_count = Integer.valueOf(data[2]);
    			int wake_count = Integer.valueOf(data[3]);
    			long active_since = Long.valueOf(data[4]);
    			long total_time = Long.valueOf(data[5]) / 1000000;
    			long sleep_time = Long.valueOf(data[6]) / 1000000;
    			long max_time = Long.valueOf(data[7]) / 1000000;
    			long last_change = Long.valueOf(data[8]);
    			NativeKernelWakelock wl = new NativeKernelWakelock(
    					name, count, expire_count, wake_count,
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
    private static ArrayList<String[]> parseDelimitedFile(String filePath, String delimiter) // throws Exception
    {
		ArrayList<String[]> rows = new ArrayList<String[]>();
    	try
    	{
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			String currentRecord;
			while ((currentRecord = br.readLine()) != null)
				rows.add(currentRecord.split(delimiter));
			br.close();
    	}
    	catch (Exception e)
    	{
    		
    	}
		return rows;
    }
}