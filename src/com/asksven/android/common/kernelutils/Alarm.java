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
import java.util.ArrayList;
import java.util.Comparator;

import com.asksven.android.common.privateapiproxies.StatElement;

/**
 * @author sven
 * Value holder for alarms
 */
public class Alarm extends StatElement implements Comparable<Alarm>, Serializable
{
	/** The name of the app responsible for the alarm */
	String m_strPackageName;
	
	/** The number od wakeups */
	long m_nWakeups;
	
	/** The details */
	ArrayList<AlarmItem> m_items;
	


	/**
	 * The default cctor
	 * @param strName
	 */
	public Alarm(String strName)
	{
		m_strPackageName = strName;
		m_items = new ArrayList<AlarmItem>();
	}
	
	/**
	 * Store the number of wakeups 
	 * @param nCount
	 */
	public void setWakeups(long nCount)
	{
		m_nWakeups = nCount;
	}
	

	/* (non-Javadoc)
	 * @see com.asksven.android.common.privateapiproxies.StatElement#getName()
	 */
	public String getName()
	{
		return m_strPackageName;
	}
	
	/**
	 * Returns the number of wakeups
	 * @return
	 */
	public long getWakeups()
	{
		return m_nWakeups;
	}
	
	/**
	 * @see getWakeups
	 * @return
	 */
	long getCount()
	{
		return getWakeups();
	}
	
	/** 
	 * Not supported for alarms
	 * @return 0
	 */
	long getDuration()
	{
		return 0;
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
	
	/**
	 * returns a string representation of the data
	 */
	public String getData()
	{
		return "Wakeups: " + getCount();
	}


	
	/**
	 * Adds an item
	 * @param nCount
	 * @param strIntent
	 */
	public void addItem(long nCount, String strIntent)
	{
		m_items.add(new AlarmItem(nCount, strIntent));
	}
	
	/**
	 * Retrieve the list of items
	 * @return
	 */
	public ArrayList<AlarmItem> getItems()
	{
		return m_items;
	}
	
	/**
	 * Value holder for alarm items
	 * @author sven
	 *
	 */
	public class AlarmItem
	{
		long m_nNumber;
		String m_strIntent;
		
		/**
		 * Default cctor
		 * @param nCount
		 * @param strIntent
		 */
		public AlarmItem(long nCount, String strIntent)
		{
			m_nNumber 	= nCount;
			m_strIntent = strIntent;
		}
		
		/**
		 * Returns the data as a string
		 * @return
		 */
		public String getData()
		{
			return "Alarms: " + m_nNumber + ", Intent: " + m_strIntent;
		}
	}
	
	 /**
     * Compare a given Wakelock with this object.
     * If the duration of this object is 
     * greater than the received object,
     * then this object is greater than the other.
     */
	public int compareTo(Alarm o)
	{
		// we want to sort in descending order
		return ((int)(o.getWakeups() - this.getWakeups()));
	}

	public static class CountComparator implements Comparator<Alarm>
	{
		public int compare(Alarm a, Alarm b)
		{
			return ((int)(b.getCount() - a.getCount()));
		}
	}
	
	public static class TimeComparator implements Comparator<Alarm>
	{
		public int compare(Alarm a, Alarm b)
		{
			return ((int)(b.getDuration() - a.getDuration()));
		}
	}

	
	

}
