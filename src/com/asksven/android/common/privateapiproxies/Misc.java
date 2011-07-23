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
public class Misc extends StatElement implements Comparable<Misc>
{
	/**
	 * the name of the process
	 */
	private String m_name;
	
	/**
	 * the time on in ms
	 */
	private long m_timeOn;
	
	/**
	 * the time running in ms
	 */
	private long m_timeRunning;

	/**
	 * Constructor
	 * @param name
	 * @param timeOn
	 * @param ratio
	 */
	public Misc(String name, long timeOn, long timeRunning)
	{

		m_name			= name;
		m_timeOn		= timeOn;
		m_timeRunning	= timeRunning;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return m_name;
	}

	/**
	 * @return the time on
	 */
	public long getTimeOn()
	{
		return m_timeOn;
	}

	/**
	 * @return the time running
	 */
	public long getTimeRunning()
	{
		return m_timeRunning;
	}

	/**
     * Compare a given Wakelock with this object.
     * If the duration of this object is 
     * greater than the received object,
     * then this object is greater than the other.
     */
	public int compareTo(Misc o)
	{
		// we want to sort in descending order
		return (int)( o.getTimeOn() - this.getTimeOn());
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Misc [m_name=" + m_name + ", m_timeOn=" + m_timeOn
				+ ", m_timeRunning=" + m_timeRunning + "]";
	}

	/**
	 * returns a string representation of the data
	 */
	public String getData()
	{
		
		return "Sys: " + this.formatDuration(getTimeOn()) + " (" + getTimeOn()/1000 + " s)"
		+ " Ratio: " + formatRatio(getTimeOn(), getTimeRunning());
	}

	

}

