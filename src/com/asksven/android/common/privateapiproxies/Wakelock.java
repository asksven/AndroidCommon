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

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.asksven.android.common.nameutils.UidInfo;

/**
 * @author sven
 *
 */
public class Wakelock extends StatElement implements Comparable<Wakelock>, Serializable
{	
	/** 
	 * the tag for logging
	 */
	private static transient final String TAG = "Wakelock";

	/**
	 * the wakelock type
	 */
	private int m_wakeType;
	
	/**
	 * the name of the wakelock holder
	 */
	private String m_name;
	
	/**
	 * the duration in ms
	 */
	private long m_duration;
	
	/**
	 * the battery realtime time
	 */
	private long m_totalTime;

	/**
	 * the count
	 */
	private int m_count;
	
	/**
	 * Creates a wakelock instance
	 * @param wakeType the type of wakelock (partial or full)
	 * @param name the speaking name
	 * @param duration the duration the wakelock was held
	 * @param time the battery realtime 
	 * @param count the number of time the wakelock was active
	 */
	public Wakelock(int wakeType, String name, long duration, long time, int count)
	{
		m_wakeType	= wakeType;
		m_name		= name;
		m_duration	= duration;
		m_totalTime = time;
		m_count		= count;
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
					Wakelock myRef = (Wakelock) myList.get(i);
					if ( (this.getName().equals(myRef.getName())) && (this.getuid() == myRef.getuid()) )
					{
						this.m_duration	-= myRef.getDuration();
						this.m_totalTime -= myRef.getTotalTime();
						this.m_count	-= myRef.getCount();

						if ((m_count < 0) || (m_duration < 0) || (m_totalTime < 0))
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
	 * @return the wakeType
	 */
	public int getWakeType() {
		return m_wakeType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return m_duration;
	}

	/**
	 * @return the total time
	 */
	public long getTotalTime() {
		return m_totalTime;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return m_count;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Wakelock [m_wakeType=" + m_wakeType + ", m_name=" + m_name
				+ ", m_duration=" + m_duration + "]";
	}
	
	 /**
     * Compare a given Wakelock with this object.
     * If the duration of this object is 
     * greater than the received object,
     * then this object is greater than the other.
     */
	public int compareTo(Wakelock o)
	{
		// we want to sort in descending order
		return ((int)(o.getDuration() - this.getDuration()));
	}
	
	/**
	 * returns a string representation of the data
	 */
	public String getData()
	{
		return this.formatDuration(getDuration()) 
			+ " (" + getDuration()/1000 + " s)"
			+ " Count:" + getCount()
			+ " " + this.formatRatio(getDuration(), getTotalTime());
	}
	
}
