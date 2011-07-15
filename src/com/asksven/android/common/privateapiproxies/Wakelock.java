/**
 * 
 */
package com.asksven.android.common.privateapiproxies;

/**
 * @author sven
 *
 */
public class Wakelock
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
	 * 
	 * @param wakeType
	 * @param name
	 * @param duration
	 */
	public Wakelock(int wakeType, String name, long duration)
	{
		m_wakeType	= wakeType;
		m_name		= name;
		m_duration	= duration;
	}

	/**
	 * @return the wakeType
	 */
	public int getWwakeType() {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Wakelock [m_wakeType=" + m_wakeType + ", m_name=" + m_name
				+ ", m_duration=" + m_duration + "]";
	}
}
