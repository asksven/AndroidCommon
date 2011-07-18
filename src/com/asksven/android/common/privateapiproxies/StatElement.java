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
	
	
	
}
