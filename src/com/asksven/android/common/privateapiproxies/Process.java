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

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * @author sven
 */
public class Process extends StatElement implements Comparable<Process>, Serializable {
    /**
     * the tag for logging
     */
    private transient static final String TAG = "Process";

    /**
     * the name of the process
     */
    private String m_name;

    /**
     * the system time in ms
     */
    private long m_systemTime;

    /**
     * the user time in ms
     */
    private long m_userTime;

    /**
     * the number of starts
     */
    private int m_starts;

    /**
     * Constructor
     *
     * @param name
     * @param userTime
     * @param systemTime
     * @param starts
     */
    public Process(String name, long userTime, long systemTime, int starts) {

        m_name = name;
        m_userTime = userTime;
        m_systemTime = systemTime;
        m_starts = starts;
    }

    /**
     * Substracts the values from a previous object
     * found in myList from the current Process
     * in order to obtain an object containing only the data since a referenc
     *
     * @param myList
     */
    public void substractFromRef(List<StatElement> myList) {
        if (myList != null) {
            for (StatElement aMyList : myList) {
                try {
                    Process myRef = (Process) aMyList;
                    if ((this.getName().equals(myRef.getName())) && (this.getuid() == myRef.getuid())) {
                        this.m_userTime -= myRef.getUserTime();
                        this.m_systemTime -= myRef.getSystemTime();
                        this.m_starts -= myRef.getStarts();
                        if ((m_userTime < 0) || (m_systemTime < 0) || (m_starts < 0)) {
                            Log.e(TAG, "substractFromRef generated negative values (" + this.toString() + " - " + myRef.toString() + ")");
                        }
                    }
                } catch (ClassCastException e) {
                    // just log as it is no error not to change the process
                    // being substracted from to do nothing
                    Log.e(TAG, "substractFromRef was called with a wrong list type");
                }

            }
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @return the system time
     */
    public long getSystemTime() {
        return m_systemTime;
    }

    /**
     * @return the user time
     */
    public long getUserTime() {
        return m_userTime;
    }

    /**
     * @return the number of starts
     */
    public int getStarts() {
        return m_starts;
    }

    /**
     * Compare a given Wakelock with this object.
     * If the duration of this object is
     * greater than the received object,
     * then this object is greater than the other.
     */
    public int compareTo(Process o) {
        // we want to sort in descending order
        return ((int) ((o.getSystemTime() + o.getUserTime()) - (this.getSystemTime() + this.getUserTime())));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Process [m_name=" + m_name + ", m_systemTime=" + m_systemTime
                + ", m_userTime=" + m_userTime + ", m_starts=" + m_starts + "]";
    }

    /**
     * returns a string representation of the data
     */
    public String getData() {

        return "Uid: " + this.getuid() + " Sys: " + this.formatDuration(getSystemTime()) + " (" + getSystemTime() / 1000 + " s)"
                + " Us: " + this.formatDuration(getUserTime()) + " (" + getUserTime() / 1000 + " s)"
                + " Starts: " + String.valueOf(getStarts());
    }

    /**
     * returns the values of the data
     */
    public double[] getValues() {
        double[] retVal = new double[2];
        retVal[0] = getUserTime();
        retVal[1] = getUserTime() + getSystemTime();
        return retVal;
    }

    public static class ProcessCountComparator implements Comparator<Process> {
        public int compare(Process a, Process b) {
            return ((int) (b.getStarts() - a.getStarts()));
        }
    }

    public static class ProcessTimeComparator implements Comparator<Process> {
        public int compare(Process a, Process b) {
            return ((int) ((b.getSystemTime() + b.getUserTime())
                    - (a.getSystemTime() + a.getUserTime())));
        }
    }

    public Drawable getIcon(Context ctx) {
        if (m_icon == null) {
            // retrieve and store the icon for that package
            if (m_uidInfo != null) {
                String myPackage = m_uidInfo.getNamePackage();
                if (!myPackage.equals("")) {
                    PackageManager manager = ctx.getPackageManager();
                    try {
                        m_icon = manager.getApplicationIcon(myPackage);
                    } catch (Exception e) {
                        // nop: no icon found
                        m_icon = null;
                    }

                }
            }
        }
        return m_icon;
    }

    public String getPackageName() {
        if (m_uidInfo != null) {
            return m_uidInfo.getNamePackage();
        } else {
            return "";
        }
    }


}

