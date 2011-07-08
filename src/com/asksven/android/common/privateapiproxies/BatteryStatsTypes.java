/*
 * Copyright (C) 2011 asksven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asksven.android.common.privateapiproxies;

import java.util.Map;

import android.util.Printer;
import android.util.SparseArray;



/**
 * This class copies the nested classes and constants from BatteryStats
 * Copyright (C) 2008 The Android Open Source Project applies
 * @see android.os.BatteryStats
 * @author sven
 *
 */
public class BatteryStatsTypes
{
    /**
     * A constant indicating a partial wake lock timer.
     */
    public static final int WAKE_TYPE_PARTIAL = 0;
    
    /**
     * Include all of the data in the stats, including previously saved data.
     */
    public static final int STATS_SINCE_CHARGED = 0;

    /**
     * Include only the last run in the stats.
     */
    public static final int STATS_LAST = 1;

    /**
     * Include only the current run in the stats.
     */
    public static final int STATS_CURRENT = 2;

    /**
     * Include only the run since the last time the device was unplugged in the stats.
     */
    public static final int STATS_SINCE_UNPLUGGED = 3;
    
    /**
     * State for keeping track of counting information.
     */
    public static abstract class Counter
    {

        /**
         * Returns the count associated with this Counter for the
         * selected type of statistics.
         *
         * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT
         */
        public abstract int getCountLocked(int which);

        /**
         * Temporary for debugging.
         */
        public abstract void logState(Printer pw, String prefix);
    }

    /**
     * State for keeping track of timing information.
     */
    public static abstract class Timer
    {

        /**
         * Returns the count associated with this Timer for the
         * selected type of statistics.
         *
         * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT
         */
        public abstract int getCountLocked(int which);

        /**
         * Returns the total time in microseconds associated with this Timer for the
         * selected type of statistics.
         *
         * @param batteryRealtime system realtime on  battery in microseconds
         * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT
         * @return a time in microseconds
         */
        public abstract long getTotalTimeLocked(long batteryRealtime, int which);

        /**
         * Temporary for debugging.
         */
        public abstract void logState(Printer pw, String prefix);
    }

    /**
     * The statistics associated with a particular uid.
     */
    public static abstract class Uid {

        /**
         * Returns a mapping containing wakelock statistics.
         *
         * @return a Map from Strings to Uid.Wakelock objects.
         */
        public abstract Map<String, ? extends Wakelock> getWakelockStats();

        /**
         * The statistics associated with a particular wake lock.
         */
        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int type);
        }

        /**
         * Returns a mapping containing sensor statistics.
         *
         * @return a Map from Integer sensor ids to Uid.Sensor objects.
         */
        public abstract Map<Integer, ? extends Sensor> getSensorStats();

        /**
         * Returns a mapping containing active process data.
         */
        public abstract SparseArray<? extends Pid> getPidStats();
        
        /**
         * Returns a mapping containing process statistics.
         *
         * @return a Map from Strings to Uid.Proc objects.
         */
        public abstract Map<String, ? extends Proc> getProcessStats();

        /**
         * Returns a mapping containing package statistics.
         *
         * @return a Map from Strings to Uid.Pkg objects.
         */
        public abstract Map<String, ? extends Pkg> getPackageStats();
        
        /**
         * {@hide}
         */
        public abstract int getUid();
        
        /**
         * {@hide}
         */
        public abstract long getTcpBytesReceived(int which);
        
        /**
         * {@hide}
         */
        public abstract long getTcpBytesSent(int which);
        
        public abstract void noteWifiRunningLocked();
        public abstract void noteWifiStoppedLocked();
        public abstract void noteFullWifiLockAcquiredLocked();
        public abstract void noteFullWifiLockReleasedLocked();
        public abstract void noteScanWifiLockAcquiredLocked();
        public abstract void noteScanWifiLockReleasedLocked();
        public abstract void noteWifiMulticastEnabledLocked();
        public abstract void noteWifiMulticastDisabledLocked();
        public abstract void noteAudioTurnedOnLocked();
        public abstract void noteAudioTurnedOffLocked();
        public abstract void noteVideoTurnedOnLocked();
        public abstract void noteVideoTurnedOffLocked();
        public abstract long getWifiRunningTime(long batteryRealtime, int which);
        public abstract long getFullWifiLockTime(long batteryRealtime, int which);
        public abstract long getScanWifiLockTime(long batteryRealtime, int which);
        public abstract long getWifiMulticastTime(long batteryRealtime,
                                                  int which);
        public abstract long getAudioTurnedOnTime(long batteryRealtime, int which);
        public abstract long getVideoTurnedOnTime(long batteryRealtime, int which);

        /**
         * Note that these must match the constants in android.os.LocalPowerManager.
         */
        static final String[] USER_ACTIVITY_TYPES = {
            "other", "cheek", "touch", "long_touch", "touch_up", "button", "unknown"
        };
        
        public static final int NUM_USER_ACTIVITY_TYPES = 7;
        
        public abstract void noteUserActivityLocked(int type);
        public abstract boolean hasUserActivity();
        public abstract int getUserActivityCount(int type, int which);
        
        public static abstract class Sensor {
            // Magic sensor number for the GPS.
            public static final int GPS = -10000;
            
            public abstract int getHandle();
            
            public abstract Timer getSensorTime();
        }

        public class Pid {
            public long mWakeSum;
            public long mWakeStart;
        }

        /**
         * The statistics associated with a particular process.
         */
        public static abstract class Proc {

            public static class ExcessivePower {
                public static final int TYPE_WAKE = 1;
                public static final int TYPE_CPU = 2;

                public int type;
                public long overTime;
                public long usedTime;
            }

            /**
             * Returns the total time (in 1/100 sec) spent executing in user code.
             *
             * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
             */
            public abstract long getUserTime(int which);

            /**
             * Returns the total time (in 1/100 sec) spent executing in system code.
             *
             * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
             */
            public abstract long getSystemTime(int which);

            /**
             * Returns the number of times the process has been started.
             *
             * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
             */
            public abstract int getStarts(int which);

            /**
             * Returns the cpu time spent in microseconds while the process was in the foreground.
             * @param which one of STATS_TOTAL, STATS_LAST, STATS_CURRENT or STATS_UNPLUGGED
             * @return foreground cpu time in microseconds
             */
            public abstract long getForegroundTime(int which);

            /**
             * Returns the approximate cpu time spent in microseconds, at a certain CPU speed.
             * @param speedStep the index of the CPU speed. This is not the actual speed of the
             * CPU.
             * @param which one of STATS_TOTAL, STATS_LAST, STATS_CURRENT or STATS_UNPLUGGED
             * @see BatteryStats#getCpuSpeedSteps()
             */
            public abstract long getTimeAtCpuSpeedStep(int speedStep, int which);

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);
        }

        /**
         * The statistics associated with a particular package.
         */
        public static abstract class Pkg {

            /**
             * Returns the number of times this package has done something that could wake up the
             * device from sleep.
             *
             * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
             */
            public abstract int getWakeups(int which);

            /**
             * Returns a mapping containing service statistics.
             */
            public abstract Map<String, ? extends Serv> getServiceStats();

            /**
             * The statistics associated with a particular service.
             */
            public abstract class Serv {

                /**
                 * Returns the amount of time spent started.
                 *
                 * @param batteryUptime elapsed uptime on battery in microseconds.
                 * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
                 * @return
                 */
                public abstract long getStartTime(long batteryUptime, int which);

                /**
                 * Returns the total number of times startService() has been called.
                 *
                 * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
                 */
                public abstract int getStarts(int which);

                /**
                 * Returns the total number times the service has been launched.
                 *
                 * @param which one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
                 */
                public abstract int getLaunches(int which);
            }
        }
    }


}
