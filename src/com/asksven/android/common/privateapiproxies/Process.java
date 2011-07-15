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
public class Process
{
	/**
	 * the name of the process
	 */
	private String m_name;
	
	/**
	 * the system time in ms
	 */
	private long m_systemTime;
	
	/**
	 * the user time in ms
	 */
	private long m_userTime;

	/**
	 * 
	 * @param wakeType
	 * @param name
	 * @param duration
	 */
	public Process(String name, long userTime, long systemTime)
	{

		m_name			= name;
		m_userTime		= userTime;
		m_systemTime	= systemTime;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return the system time
	 */
	public long getSystemTime() {
		return m_systemTime;
	}

	/**
	 * @return the user time
	 */
	public long getUserTime() {
		return m_userTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Process [m_name=" + m_name + ", m_systemTime=" + m_systemTime
				+ ", m_userTime=" + m_userTime + "]";
	}


}

