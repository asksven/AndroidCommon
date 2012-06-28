/**
 * 
 */
package com.asksven.android.common.privateapiproxies;

import java.io.Serializable;
import java.util.Formatter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.asksven.android.common.nameutils.UidInfo;
import com.asksven.android.common.nameutils.UidNameResolver;

/**
 * @author sven
 *
 */
public abstract class StatElement implements Serializable
{
	/**
	 * The process uid
	 */
	private int m_uid = -1;
	/**
	 * The resolved name info
	 */
	protected transient UidInfo m_uidInfo;

	/**
	 * the battery realtime time
	 */
	protected long m_totalTime;	
	/**
	 * Set the uid
	 * @param uid a process uid
	 */
	public void setUid(int uid)
	{
		m_uid = uid;
	}
	
	/**
	 * Get the uid
	 * @return the process uid
	 */
	public int getuid()
	{
		return m_uid;
	}
	
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
	public String getFullQualifiedName(Context context)
	{
		String ret = "";
		
		if (m_uidInfo == null)
		{
			// may have been left out for lazy loading
			if (m_uid != -1)
			{
				UidNameResolver m_nameResolver = new UidNameResolver();
				m_uidInfo = m_nameResolver.getNameForUid(context, m_uid);
			}
			else
			{
				return ret;
			}
		}
		
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
	public String getFqn(Context context)
	{
		return getFullQualifiedName(context);
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
	
	public final String formatRatio(long num, long den)
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
	
	/** 
	 * returns the representation of the data for file dimp
	 */	
	public String getDumpData(Context context)
	{
		return this.getName() + " (" + this.getFqn(context) + "): " + this.getData();
	}

	/** 
	 * returns the values of the data
	 */	
	public double[] getValues()
	{
		double[] retVal = new double[2];
		retVal[0] = m_totalTime;
		return retVal;
	}


	/** 
	 * returns the max of the data
	 */	
	public double getMaxValue()
	{
		return getValues()[0]; //m_totalTime;
	}
}
