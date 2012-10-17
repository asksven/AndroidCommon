/**
 *
 */
package com.asksven.android.common.utils;

import android.util.Log;

/**
 * @author sven
 */
public abstract class GenericLogger {


    public static void d(String strLogfile, String strTag, String strMessage) {
        Log.d(strTag, strMessage);
        DataStorage.LogToFile(strLogfile, strMessage);
    }

    public static void e(String strLogfile, String strTag, String strMessage) {
        Log.e(strTag, strMessage);
        DataStorage.LogToFile(strLogfile, strMessage);
    }

    public static void i(String strLogFile, String strTag, String strMessage) {
        Log.i(strTag, strMessage);
        DataStorage.LogToFile(strLogFile, strMessage);
    }

    public static void e(String strLogFile, String strTag, StackTraceElement[] stack) {
        Log.e(strTag, "An Exception occured. Stacktrace:");
        for (StackTraceElement aStack : stack) {
            Log.e(strTag, aStack.toString());
        }
        DataStorage.LogToFile(strLogFile, stack);
    }

    public static void stackTrace(String strTag, StackTraceElement[] stack) {
        Log.e(strTag, "An Exception occured. Stacktrace:");
        for (StackTraceElement aStack : stack) {
            Log.e(strTag, ">>> " + aStack.toString());
        }
    }

    private static void writeLog(String strLogFile, String strTag, String strMessage) {
        DataStorage.LogToFile(strLogFile, strMessage);
    }

}
