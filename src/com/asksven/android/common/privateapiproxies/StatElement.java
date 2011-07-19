/**
 * 
 */
package com.asksven.android.common.privateapiproxies;

import com.android.asksven.common.nameutils.UidInfo;

/**
 * @author sven
 *
 */
public abstract class StatElement
{
	/**
	 * The resolved name
	 */
	protected UidInfo m_uidInfo;

	
	/**
	 * Store the name info
	 * @param myInfo (@see com.android.asksven.common.nameutils.UidNameResolver)
	 */
	public void setUidInfo(UidInfo myInfo)
	{
		m_uidInfo = myInfo;
	}
	
	/**
	 * Returns the full qualified name
	 * @return the full qualified name
	 */
	public String getFullQualifiedName()
	{
		String ret = "";
		if (!m_uidInfo.getNamePackage().equals(""))
		{
			ret = m_uidInfo.getNamePackage() + ".";
		}
		ret += m_uidInfo.getName();
		
		return ret;
			
	}
	
	/**
	 * Returns the full qualified name (default, can be overwritten)
	 * @return the full qualified name
	 */
	public String getFqn()
	{
		return getFullQualifiedName();
	}
	
	

	/**
	 * Returns the associated UidInfo
	 * @return the UidInfo
	 */
	public UidInfo getUidInfo()
	{
		return m_uidInfo;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return m_uidInfo.toString();
	}
	
	/**
	 * Returns a speaking name
	 * @return the name
	 */
	public abstract String getName();
	
	/**
	 * Returns data as displayable string
	 * @return the data
	 */
	public abstract String getData();
	
	/**
	 * Formats milliseconds to a friendly form 
	 * @param millis
	 * @return the formated string
	 */
	protected String formatDuration(double millis)
	{
		String ret = "";
		
        int seconds = (int) Math.floor(millis / 1000);
        
        int days = 0, hours = 0, minutes = 0;
        if (seconds > (60*60*24)) {
            days = seconds / (60*60*24);
            seconds -= days * (60*60*24);
        }
        if (seconds > (60 * 60)) {
            hours = seconds / (60 * 60);
            seconds -= hours * (60 * 60);
        }
        if (seconds > 60) {
            minutes = seconds / 60;
            seconds -= minutes * 60;
        }
        ret = "";
        if (days > 0)
        {
            ret += days + " d ";
        }
        
        if (hours > 0)
        {
        	ret += hours + " h ";
        }
        
        if (minutes > 0)
        { 
        	ret += minutes + " m ";
        }
        if (seconds > 0)
        {
        	ret += seconds + " s ";
        }
        
        return ret;
    }
	
}
