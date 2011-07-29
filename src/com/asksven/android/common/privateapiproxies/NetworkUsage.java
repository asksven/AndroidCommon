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

import java.util.List;

import android.util.Log;

public class NetworkUsage extends StatElement implements Comparable<NetworkUsage>
{
	/** 
	 * the tag for logging
	 */
	private static final String TAG = "Misc";
	
	/**
	 * the uid of the program
	 */
	private int m_uid;
	
	/**
	 * tcpBytes received by the program
	 */
	private long m_bytesReceived;
	
	/**
	 * tcpBytes sent by the program
	 */
	private long m_bytesSent;
	
	/**
	 * 
	 * @param uid
	 * @param bytesReceived
	 * @param bytesSent
	 */
	public NetworkUsage(int uid, long bytesReceived, long bytesSent)
	{
		m_uid		= uid;
		m_bytesReceived	= bytesReceived;
		m_bytesSent		= bytesSent;
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
					NetworkUsage myRef = (NetworkUsage) myList.get(i);
					if ( (this.getName().equals(myRef.getName())) && (this.getuid() == myRef.getuid()) )
					{
						this.m_bytesReceived	= getBytesReceived();
						this.m_bytesSent		= getBytesSent();
					
						if ((m_bytesReceived < 0) || (m_bytesSent < 0))
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
	 * @return the uid
	 */
	public int getUid()
	{
		return m_uid;
	}

	/**
	 * @return the bytes received
	 */
	public long getBytesReceived()
	{
		return m_bytesReceived;
	}

	/**
	 * @return the bytes sent
	 */
	public long getBytesSent()
	{
		return m_bytesSent;
	}
	
	 /**
     * Compare a given NetworkUsage with this object.
     * If the duration of this object is 
     * greater than the received object,
     * then this object is greater than the other.
     */
	public int compareTo(NetworkUsage o)
	{
		// we want to sort in descending order
		return ((int)((o.getBytesReceived() + o.getBytesSent()) - (this.getBytesReceived() + this.getBytesSent())));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NetworkUsage [m_uid=" + m_uid + ", m_bytesReceived="
				+ m_bytesReceived + ", m_bytesSent=" + m_bytesSent + "]";
	}
	
	/**
	 * Network Stats do not have a speaking name, just the UID
	 */
	public String getName()
	{
		return String.valueOf(m_uid);
	}
	
	/**
	 * returns a string representation of the data
	 */
	public String getData()
	{
		return "Rec. Byes: " + String.valueOf(m_bytesReceived) + ", Sent Bytes: " + String.valueOf(m_bytesSent);
	}
}
