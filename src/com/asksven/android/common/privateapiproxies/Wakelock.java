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

/**
 * @author sven
 *
 */
public class Wakelock implements Comparable<Wakelock>
{
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
	 * the count
	 */
	private int m_count;
	
	/**
	 * 
	 * @param wakeType
	 * @param name
	 * @param duration
	 */
	public Wakelock(int wakeType, String name, long duration, int count)
	{
		m_wakeType	= wakeType;
		m_name		= name;
		m_duration	= duration;
		m_count		= count;
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
	 * @return the count
	 */
	public long getCount() {
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
}
