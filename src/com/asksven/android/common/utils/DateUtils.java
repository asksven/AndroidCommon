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
package com.asksven.android.common.utils;

/**
 * @author sven
 *
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SHORT = "HH:mm:ss";

    /**
     * Returns the current date in the default format.
     *
     * @return the current formatted date/time
     */
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    /**
     * Returns the current date in a given format.
     * DateUtils.now("dd MMMMM yyyy")
     * DateUtils.now("yyyyMMdd")
     * DateUtils.now("dd.MM.yy")
     * DateUtils.now("MM/dd/yy")
     *
     * @param dateFormat a date format (See examples)
     * @return the current formatted date/time
     */
    public static String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    public static String format(String dateFormat, Date time) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(time);
    }

    public static String format(String dateFormat, Long time) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(time);
    }

    public static String format(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(time);
    }

    public static String format(long timeMs) {
        return format(timeMs, DATE_FORMAT_NOW);
    }

    public static String formatShort(long timeMs) {
        return format(timeMs, DATE_FORMAT_SHORT);
    }

    public static String format(long timeMs, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(timeMs);
    }

    /**
     * Formats milliseconds to a friendly form
     *
     * @param millis milliseconds to format
     * @return the formatted string
     */
    public static String formatDuration(long millis) {
        String ret = "";

        int seconds = (int) Math.floor(millis / 1000);

        int days = 0, hours = 0, minutes = 0;
        if (seconds > (60 * 60 * 24)) {
            days = seconds / (60 * 60 * 24);
            seconds -= days * (60 * 60 * 24);
        }
        if (seconds > (60 * 60)) {
            hours = seconds / (60 * 60);
            seconds -= hours * (60 * 60);
        }
        if (seconds > 60) {
            minutes = seconds / 60;
            seconds -= minutes * 60;
        }
        ret = "";
        if (days > 0) {
            ret += days + " d ";
        }

        if (hours > 0) {
            ret += hours + " h ";
        }

        if (minutes > 0) {
            ret += minutes + " m ";
        }
        if (seconds > 0) {
            ret += seconds + " s ";
        }

        if (ret.equals("")) {
            ret = "0 s";
        }
        return ret;
    }

    /**
     * Formats milliseconds to a friendly form. Short means that seconds are truncated if value > 1 Day
     *
     * @param millis millis milliseconds to format
     * @return the formatted string
     */
    public static String formatDurationShort(long millis) {
        String ret = "";

        int seconds = (int) Math.floor(millis / 1000);

        int days = 0, hours = 0, minutes = 0;
        if (seconds > (60 * 60 * 24)) {
            days = seconds / (60 * 60 * 24);
            seconds -= days * (60 * 60 * 24);
        }
        if (seconds > (60 * 60)) {
            hours = seconds / (60 * 60);
            seconds -= hours * (60 * 60);
        }
        if (seconds > 60) {
            minutes = seconds / 60;
            seconds -= minutes * 60;
        }
        ret = "";
        if (days > 0) {
            ret += days + " d ";
        }

        if (hours > 0) {
            ret += hours + " h ";
        }

        if (minutes > 0) {
            ret += minutes + " m ";
        }
        if ((seconds > 0) && (days == 0)) {
            // only show seconds when value < 1 day
            ret += seconds + " s ";
        }

        if (ret.equals("")) {
            ret = "0 s";
        }
        return ret;
    }

}