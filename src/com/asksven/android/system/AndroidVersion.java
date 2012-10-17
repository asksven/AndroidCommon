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
package com.asksven.android.system;

import android.os.Build;

/**
 * Handles android version detection with proper backwards compatibility older SDK Target Versions
 *
 * @author sven
 * @author pedronveloso@androidpt.com
 */
public class AndroidVersion {
    public static boolean isFroyo() {
        boolean bRet = false;
        if (Build.VERSION.SDK_INT == 8) {
            bRet = true;
        }

        return bRet;
    }

    public static boolean isGingerbread() {
        boolean bRet = false;
        // GINGERBREAD_MR1 = 10, GINGERBREAD = 9
        if (Build.VERSION.SDK_INT == 9 || Build.VERSION.SDK_INT == 10) {
            bRet = true;
        }

        return bRet;
    }

    public static boolean isIcs() {
        boolean bRet = false;
        if (Build.VERSION.SDK_INT == 14 || Build.VERSION.SDK_INT == 15) {
            bRet = true;
        }
        return bRet;
    }

    public static boolean isIcsOrAbove() {
        boolean bRet = false;
        if (Build.VERSION.SDK_INT >= 14) {
            bRet = true;
        }
        return bRet;
    }

    public static boolean isJellyBean() {
        boolean bRet = false;
        if (Build.VERSION.SDK_INT >= 16) {
            bRet = true;
        }
        return bRet;
    }

}
