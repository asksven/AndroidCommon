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

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.*;

/**
 * @author sven
 */
public class DataStorage {
    /**
     * The logging TAG
     */
    private static final String TAG = "DataStorage";

    public static boolean isExternalStorageWritable() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        return (mExternalStorageAvailable && mExternalStorageWriteable);
    }

    public static void LogToFile(String fileName, StackTraceElement[] stack) {
        LogToFile(fileName, "---- Begin Callstack ----");
        for (StackTraceElement aStack : stack) {
            LogToFile(fileName, aStack.toString());
        }
        LogToFile(fileName, "---- End Callstack ----");
    }

    public static void LogToFile(String fileName, String strText) {

        try {
            // open file for writing
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()) {
                File dumpFile = new File(root, fileName);
                // append
                FileWriter fw = new FileWriter(dumpFile, true);
                BufferedWriter out = new BufferedWriter(fw);

                // write header
                out.write(DateUtils.now() + " " + strText + "\n");

                // close file
                out.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public static boolean objectToFile(Context context, String fileName, Object serializableObject) {
        boolean bRet = true;

        if (!(serializableObject instanceof java.io.Serializable)) {
            Log.e(TAG, "The object is not serializable: " + fileName);
            return false;
        }
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(serializableObject);
            os.close();
        } catch (Exception e) {
            Log.e(TAG, "An error occured while writing " + fileName + " " + e.getMessage());

            bRet = false;
        }

        return bRet;
    }

    public static Object fileToObject(Context context, String fileName) {
        FileInputStream fis;
        Object myRet = null;
        try {
            fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            myRet = is.readObject();
            is.close();

        } catch (FileNotFoundException e) {
            myRet = null;
        } catch (IOException e) {
            myRet = null;
        } catch (ClassNotFoundException e) {
            myRet = null;
        }

        return myRet;
    }

}
