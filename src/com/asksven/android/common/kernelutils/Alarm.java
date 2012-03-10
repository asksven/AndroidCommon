/*
 * Copyright (C) 2011-2012 asksven
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
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.asksven.android.common.privateapiproxies.StatElement;

/**
 * @author sven
 * Value holder for alarms
 */
public class Alarm extends StatElement implements Comparable<Alarm>, Serializable
{
	/** 
	 * the tag for logging
	 */
	private static transient final String TAG = "Alarm";
	
	/** The name of the app responsible for the alarm */
	String m_strPackageName;
	
	/** The number od wakeups */
	long m_nWakeups;
	
	/** The total count */
	long m_nTotalCount;
	
	
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
	

	/**
	 * Set the total wakeup count for the sum of all alarms
	 * @param nCount
	 */
	public void setTotalCount(long nCount)
	{
		m_nTotalCount = nCount;
	}
	
	/**
	 * Return the max of all alarms (wakeups) 
	 */
	public double getMaxValue()
	{
		return m_nTotalCount;
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
		retVal[0] = getCount();
		return retVal;
	}
	
	/**
	 * returns a string representation of the data
	 */
	public String getData()
	{
		String strRet = "";
		strRet = "Wakeups: " + getCount();
		
		return strRet;
	}

	/**
	 * returns a string representation of the detailed data (including children)  
	 */
	public String getDetailedData()
	{
		String strRet = "";
		strRet = "Wakeups: " + getCount() + "\n";
		
		for (int i=0; i < m_items.size(); i++)
		{
			strRet += "  " + m_items.get(i).getData() + "\n";
		}
		
		return strRet;
	}

	/** 
	 * returns the representation of the data for file dump
	 */	
	public String getDumpData(Context context)
	{
		return this.getName() + " (" + this.getFqn(context) + "): " + this.getDetailedData();
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
					Alarm myRef = (Alarm) myList.get(i);
					if (this.getName().equals(myRef.getName()))
					{
						// process main values
						Log.i(TAG, "Substracting " + myRef.toString() + " from " + this.toString());
						this.m_nWakeups		-= myRef.getCount();
						this.m_nTotalCount  -= myRef.getMaxValue();
						Log.i(TAG, "Result: " + this.toString());
						
						// and process items
						for (int j=0; j < this.m_items.size(); j++)
						{
							AlarmItem myItem = this.m_items.get(j);
							myItem.substractFromRef(myRef.getItems());
						}

						if (this.getCount() < 0)
						{
							Log.e(TAG, "substractFromRef generated negative values (" + this.toString() + " - " + myRef.toString() + ")");
						}
						if (this.getItems().size() < myRef.getItems().size())
						{
							Log.e(TAG, "substractFromRef error processing alarm items: ref can not have less items");
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return getName() + " ["
				+ getData()
				+ "]";
	}


	/**
	 * Value holder for alarm items
	 * @author sven
	 *
	 */
	public class AlarmItem implements Serializable
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
		 * Returns the intent name
		 * @return
		 */
		public String getIntent()
		{
			return m_strIntent;
		}
		
		/**
		 * Returns the count
		 * @return
		 */
		public long getCount()
		{
			return m_nNumber;
		}
		/**
		 * Returns the data as a string
		 * @return
		 */
		public String getData()
		{
			return "Alarms: " + m_nNumber + ", Intent: " + m_strIntent;
		}
		/**
		 * Substracts the values from a previous object
		 * found in myList from the current Process
		 * in order to obtain an object containing only the data since a referenc
		 * @param myList
		 */
		public void substractFromRef(List<AlarmItem> myList )
		{
			if (myList != null)
			{
				for (int i = 0; i < myList.size(); i++)
				{
					try
					{
						AlarmItem myRef = (AlarmItem) myList.get(i);
						if (this.getIntent().equals(myRef.getIntent()))
						{
							// process main values
							Log.i(TAG, "Substracting " + myRef.toString() + " from " + this.toString());
							this.m_nNumber		-= myRef.getCount();
							Log.i(TAG, "Result: " + this.toString());
						}
					}
					catch (ClassCastException e)
					{
						// just log as it is no error not to change the process
						// being substracted from to do nothing
						Log.e(TAG, "AlarmItem.substractFromRef was called with a wrong list type");
					}
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
