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
package com.asksven.android.common.kernelutils;

import android.util.Log;
import com.asksven.android.common.privateapiproxies.StatElement;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;


/**
 * Value holder for CpuState
 * @author sven
 *
 */

/**
 * simple struct for states/time
 */
public class State extends StatElement implements Comparable<State>, Serializable {
    /**
     * the tag for logging
     */
    private transient static final String TAG = "Process";

    private static final long serialVersionUID = 1L;

    public int m_freq = 0;
    public long m_duration = 0;

    public State(int freq, long duration) {
        m_freq = freq;
        m_duration = duration;
    }

    public String getName() {
        String ret = formatFreq(m_freq);
        if (ret.equals("0 kHz")) {
            ret = "Deep Sleep";
        }
        return ret;
    }

    public String toString() {
        return getName() + " " + getData();
    }

    public String getData() {
        return formatDuration(m_duration) + " " + this.formatRatio(m_duration, getTotal());
    }

    /**
     * returns the values of the data
     */
    public double[] getValues() {
        double[] retVal = new double[2];
        retVal[0] = m_duration;
        return retVal;
    }

    /**
     * Formats a freq in Hz in a readable form
     *
     * @param freqkHz frequency to format
     * @return freq in Hz in a readable form
     */
    String formatFreq(int freqkHz) {
        double freqMHz = freqkHz / 1000;
        double freqGHz = freqkHz / 1000 / 1000;

        String formatedFreq = "";
        DecimalFormat df = new DecimalFormat("#.##");

        if (freqGHz >= 1) {
            formatedFreq = df.format(freqGHz) + " GHz";
        } else if (freqMHz >= 1) {
            formatedFreq = df.format(freqMHz) + " MHz";
        } else {
            formatedFreq = df.format(freqkHz) + " kHz";
        }

        return formatedFreq;

    }

    /**
     * Substracts the values from a previous object
     * found in myList from the current Process
     * in order to obtain an object containing only the data since a reference
     *
     * @param myList list with stats
     */
    public void substractFromRef(List<StatElement> myList) {
        if (myList != null) {
            for (StatElement aMyList : myList) {
                try {
                    State myRef = (State) aMyList;
                    if ((this.getName().equals(myRef.getName())) && (this.getuid() == myRef.getuid())) {
                        this.m_duration -= myRef.m_duration;
                        this.setTotal(this.getTotal() - myRef.getTotal());
                        if (m_duration < 0) {
                            Log.e(TAG, "substractFromRef generated negative values (" + this.m_duration + " - " + myRef.m_duration + ")");
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
     * Compare a given Wakelock with this object.
     * If the duration of this object is
     * greater than the received object,
     * then this object is greater than the other.
     */
    public int compareTo(State o) {
        // we want to sort in descending order
        return ((int) ((o.m_duration) - (this.m_duration)));
    }


}
