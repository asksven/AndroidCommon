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

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.lang.Math;

import com.asksven.android.common.privateapiproxies.StatElement;
import com.asksven.android.common.utils.StringUtils;

import android.content.Context;
import android.util.Log;

/**
 * @author sven
 *
 */
public class NativeKernelWakelock extends StatElement implements Comparable<NativeKernelWakelock>, Serializable
{	
	
	// a kernel wakelock from /proc/wakelocks is of the format
	// [name, count, expire_count, wake_count, active_since, total_time, sleep_time, max_time, last_change]

	/** 
	 * the tag for logging
	 */
	private static transient final String TAG = "NativeKernelWakelock";

	/**
	 * the name of the wakelock holder
	 */
	private String m_name;
	
	/**
	 * the details (packages) for that wakelock (if any
	 */
	private String m_details;

	/**
	 * the count
	 */
	private int m_count;

	/**
	 * the expire count
	 */
	private int m_expireCount;

	/**
	 * the wake count
	 */
	private int m_wakeCount;

	/**
	 * the active_since time
	 */
	private long m_activeSince;

	/**
	 * the total_time
	 */
	private long m_ttlTime;
	
	/**
	 * the sleep time
	 */
	private long m_sleepTime;

	/**
	 * the max time
	 */
	private long m_maxTime;

	/**
	 * the last change
	 */
	private long m_lastChange;

	/**
	 * Creates a wakelock instance
	 * @param wakeType the type of wakelock (partial or full)
	 * @param name the speaking name
	 * @param duration the duration the wakelock was held
	 * @param time the battery realtime 
	 * @param count the number of time the wakelock was active
	 */
	public NativeKernelWakelock(String name, String details, int count, int expire_count, int wake_count, long active_since, long total_time, long sleep_time, long max_time, long last_change, long time)
	{
		m_name			= name;
		m_details 		= details;
		m_count			= count;
		m_expireCount	= expire_count;
		m_wakeCount		= wake_count;
		m_activeSince	= active_since;
		m_ttlTime		= total_time;
		m_sleepTime		= sleep_time;
		m_maxTime		= max_time;
		m_lastChange	= last_change;
		setTotal(time);
	}

	/**
	 * Substracts the values from a previous object
	 * found in myList from the current Process
	 * in order to obtain an object containing only the data since a referenc
	 * @param myList
	 */
	public void substractFromRef(List<StatElement> myList )
	{
		if (myList != null)
		{
			for (int i = 0; i < myList.size(); i++)
			{
				try
				{
					NativeKernelWakelock myRef = (NativeKernelWakelock) myList.get(i);
					if ( (this.getName().equals(myRef.getName())) && (this.getuid() == myRef.getuid()) )
					{
//						Log.i(TAG, "Substracting " + myRef.toString() + " from " + this.toString());
						this.m_count		-= myRef.m_count;
						this.m_expireCount	-= myRef.m_expireCount;
						this.m_wakeCount	-= myRef.m_wakeCount;
						this.m_activeSince	-= myRef.m_activeSince;
						this.m_ttlTime		-= myRef.m_ttlTime;
						this.m_sleepTime	-= myRef.m_sleepTime;
						this.m_maxTime		-= myRef.m_maxTime;
						this.m_lastChange	= Math.max(this.m_lastChange, myRef.m_lastChange);
						this.setTotal( this.getTotal() - myRef.getTotal() );
						
						// for kernel wakelocks we need to merge the package list (aka. fqn)
						// we don't care about double entries here, this must be handeled in getFqn
						if (!myRef.m_details.equals(""))
						{
							if (!this.m_details.equals(""))
							{
								this.m_details += ", " + myRef.m_details;
							}
							else
							{
								this.m_details = myRef.m_details;
							}
						}
						
//						Log.i(TAG, "Result: " + this.toString());

						if ((m_count < 0) || (m_sleepTime < 0) || (this.getTotal() < 0))
						{
							Log.e(TAG, "substractFromRef generated negative values (" + this.toString() + " - " + myRef.toString() + ")");
						}
					}
				}
				catch (ClassCastException e)
				{
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
	public String getName()
	{
		return m_name;
	}

	/**
	 * @return the duration
	 */
	public long getDuration()
	{
		return m_sleepTime;
	}

	/**
	 * @return the count
	 */
	public int getCount()
	{
		return m_count;
	}

	/**
	 * the expire count
	 */
	public int getExpireCount()
	{
		return m_expireCount;
	}

	/**
	 * the wake count
	 */
	public int getWakeCount()
	{
		return m_wakeCount;
	}

	/**
	 * the active_since time
	 */
	public long getActiveSince()
	{
		return m_activeSince;
	}

	/**
	 * the total_time
	 */
	public long getTtlTime()
	{
		return m_ttlTime;
	}
	
	/**
	 * the sleep time
	 */
	public long getSleepTime()
	{
		return m_sleepTime;
	}

	/**
	 * the max time
	 */
	public long getMaxTime()
	{
		return m_maxTime;
	}

	/**
	 * the last change
	 */
	public long getLastChange()
	{
		return m_lastChange;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return getName() + " ["
//				+ "m_name=" + m_name + ", "
//				+ "m_count=" + m_count + ", "
//				+ "m_expire_count=" + m_expireCount + ", "
//				+ "m_wake_count=" + m_wakeCount + ", "
//				+ "m_active_since="+ m_activeSince + ", "
//				+ "m_total_time="+ m_ttlTime + ", "
//				+ "m_sleep_time=" + m_sleepTime + ", "
//				+ "m_max_time=" + m_maxTime + ", "
//				+ "m_last_change=" + m_lastChange + ", "
//				+ "m_total_time=" + m_totalTime
				+ getData()
				+ "]";
	}
	
	 /**
     * Compare a given Wakelock with this object.
     * If the duration of this object is 
     * greater than the received object,
     * then this object is greater than the other.
     */
	public int compareTo(NativeKernelWakelock o)
	{
		// we want to sort in descending order
		return ((int)(o.getDuration() - this.getDuration()));
	}
	
	/**
	 * Returns the full qualified name (default, can be overwritten)
	 * @return the full qualified name
	 */
	public String getFqn(Context context)
	{
		// we need to do some formating here as m_details may be of the form "a, b, a, c, b"
		if (m_details.equals(""))
		{
			return m_details;
		}
		else
		{
			// remove any left over "(" and ")"
			m_details.replace("(", "");
			m_details.replace(")", "");
			
			// merge
			String[] splitArray = m_details.split(", ");
			
			m_details = StringUtils.join(splitArray, ", ", true);
			return m_details;
		}
		
	}

	/**
	 * returns a string representation of the data
	 */
	public String getData()
	{
		return this.formatDuration(getDuration()) 
			+ " (" + getDuration()/1000 + " s)"
			+ " Cnt:(c/wc/ec)" + getCount() + "/" + m_wakeCount + "/" + m_expireCount
			+ " " + this.formatRatio(getDuration(), getTotal());
	}
	
	/** 
	 * returns the values of the data
	 */	
	public double[] getValues()
	{
		double[] retVal = new double[2];
		retVal[0] = getDuration();
		return retVal;
	}
	
	public static class CountComparator implements Comparator<NativeKernelWakelock>
	{
		public int compare(NativeKernelWakelock a, NativeKernelWakelock b)
		{
			return ((int)(b.getCount() - a.getCount()));
		}
	}
	
	public static class TimeComparator implements Comparator<NativeKernelWakelock>
	{
		public int compare(NativeKernelWakelock a, NativeKernelWakelock b)
		{
			return ((int)(b.getDuration() - a.getDuration()));
		}
	}

}
