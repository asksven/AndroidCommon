/**
 *
 */
package com.asksven.android.common.kernelutils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import com.asksven.android.common.CommonLogSettings;
import com.asksven.android.common.contrib.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author sven
 */
public class Wakelocks {
    private final static String TAG = "Wakelocks";

    public static ArrayList<NativeKernelWakelock> parseProcWakelocks(Context context) {
        String filePath = "/proc/wakelocks";
        String delimiter = String.valueOf('\t');
        ArrayList<NativeKernelWakelock> myRet = new ArrayList<NativeKernelWakelock>();
        // format
        // [name, count, expire_count, wake_count, active_since, total_time, sleep_time, max_time, last_change]
        ArrayList<String[]> rows = parseDelimitedFile(filePath, delimiter);

        long msSinceBoot = SystemClock.elapsedRealtime();

        // list the running processes
        ActivityManager actvityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> procInfos = actvityManager.getRunningAppProcesses();
        PackageManager pack = context.getPackageManager();

        // start with 1
        for (int i = 1; i < rows.size(); i++) {
            try {
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

                // post-processing of eventX-YYYY processes
                String details = "";
//				name = "event3-30240";
                // we start with a " here as that is the way the data comes from /proc
                if (name.startsWith("\"event")) {
                    String process = name.replaceAll("\"", "");
                    if (CommonLogSettings.DEBUG) {
                        Log.d(TAG, "Pattern 'event' found in " + process);
                    }

                    int proc = 0;
                    String[] parts = process.split("-");
                    if (parts.length == 2) {
                        try {
                            proc = Integer.valueOf(parts[1]);
                            if (CommonLogSettings.DEBUG) {
                                Log.d(TAG, "Resolving proc name for 'event' " + proc);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Cound not split process name " + process);
                        }
                    }

                    if (proc != 0) {
                        // search for the process in the task list
                        for (RunningAppProcessInfo procInfo : procInfos) {
                            int id = procInfo.pid;
                            if (id == proc) {

                                details = procInfo.processName;
                                String appName = "";

                                String[] pkgList = procInfos.get(count).pkgList;
                                for (String aPkgList : pkgList) {
                                    if (details.length() > 0) {
                                        details += ", ";
                                    }
                                    details += aPkgList;
                                }

                                if (CommonLogSettings.DEBUG) {
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
            } catch (Exception e) {
                // go on
            }
        }
        return myRet;
    }

    private static ArrayList<String[]> parseDelimitedFile(String filePath, String delimiter) {
        ArrayList<String[]> rows = new ArrayList<String[]>();
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String currentRecord;
            while ((currentRecord = br.readLine()) != null)
                rows.add(currentRecord.split(delimiter));
            br.close();
        } catch (Exception ignored) {
        }
        return rows;
    }

    public static boolean isDiscreteKwlPatch() {
        boolean ret = false;

        String filePath = "/sys/module/wakelock/parameters/default_stats";
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            // read first line
            String currentRecord = br.readLine();

            if (!currentRecord.equals("0")) {
                ret = true;
            }
            br.close();
        } catch (Exception ignored) {
        }
        return ret;
    }

    public static boolean hasWakelocks(Context context) {
        boolean myRet = false;
        String filePath = "/sys/power/wake_lock";
        ArrayList<String> res = Util.run("su", "cat " + filePath);
        // Format: 0 /sys/power/wake_lock
        final ArrayList<String> values = new ArrayList<String>();
        if (res.size() != 0) {
            String line = res.get(0);
            try {
                myRet = line.contains("PowerManagerService");
                if (myRet) {
                    Log.i(TAG, "Detected userland wakelock in line " + line);
                } else {
                    Log.i(TAG, "No userland wakelock detected in line " + line);
                }
            } catch (Exception e) {
                // something went wrong
                Log.e(TAG, "Exeception processsing " + filePath + ": " + e.getMessage());
                myRet = false;
            }
        }
        return myRet;
    }

}