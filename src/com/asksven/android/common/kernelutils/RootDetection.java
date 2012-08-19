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

import java.util.ArrayList;

import com.asksven.andoid.common.contrib.Util;
import com.asksven.android.common.shellutils.Exec;
import com.asksven.android.common.shellutils.ExecResult;

/**
 * This class provides methods for detecting if the phone is rooted
 * @author sven
 *
 */
public class RootDetection
{
	
	/**
	 * Tests whether su can be executed.
	 * @return 0 if 
	 */
	public static boolean hasSuRights()
	{
		return hasSuRights("ls /");
	}

	/**
	 * Checks for su with a specific command
	 * @param command
	 * @return
	 */
	public static boolean hasSuRights(String command)
	{
//		ExecResult res = Exec.execPrint(new String[]{"su", "-c", command});
		ArrayList<String> res = Util.run("su", command);
		boolean bRet = false;
		
		if (res.size() != 0)
		{
			bRet = true;
		}
		
		return bRet;
	}

}
