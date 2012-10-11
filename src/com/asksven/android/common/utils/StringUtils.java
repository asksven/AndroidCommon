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

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author sven
 *
 */
public class StringUtils
{
	public static final String formatRatio(long num, long den)
	{
		StringBuilder mFormatBuilder = new StringBuilder(8);
	    Formatter mFormatter = new Formatter(mFormatBuilder);
        if (den == 0L)
        {
            return "---%";
        }
        
        float perc = ((float)num) / ((float)den) * 100;
        mFormatBuilder.setLength(0);
        mFormatter.format("%.1f%%", perc);
        return mFormatBuilder.toString();
    }
	
	public static String join(String[] array, String sep, boolean merge)
	{
		String ret = "";
		for (int i = 0; i < array.length; i++)
		{
			if (ret.equals(""))
			{
				ret = array[i];
			}
			else
			{
				if (merge)
				{
					// check if the string is alread present
					if (ret.indexOf(array[i]) == -1)
					{
						// add
						ret += sep + array[i];
					}
				}
				else
				{
					ret += sep + array[i];
				}
			}
		}
		return ret;
	}
	
	public static void splitLine(String line, ArrayList<String> outSplit)
	{
		outSplit.clear();
		final StringTokenizer t = new StringTokenizer(line, " \t\n\r\f:");
		while (t.hasMoreTokens())
		{
			outSplit.add(t.nextToken());
		}
	}	
	
	public static void parseLine(ArrayList<String> keys, ArrayList<String> values, HashMap<String, String> outParsed)
	{
		outParsed.clear();
		final int size = Math.min(keys.size(), values.size());
		for (int i = 0; i < size; i++)
		{
			outParsed.put(keys.get(i), values.get(i));
		}
	}
	
	public static int getParsedInt(HashMap<String, String> parsed, String key)
	{
		final String value = parsed.get(key);
		return value != null ? Integer.parseInt(value) : 0;
	}
	
	public static long getParsedLong(HashMap<String, String> parsed, String key)
	{
		final String value = parsed.get(key);
		return value != null ? Long.parseLong(value) : 0;
	}

	public static String stripLeadingAndTrailingQuotes(String str)
	  {
	      if (str.startsWith("\""))
	      {
	          str = str.substring(1, str.length());
	      }
	      if (str.endsWith("\""))
	      {
	          str = str.substring(0, str.length() - 1);
	      }
	      return str;
	  }


}
