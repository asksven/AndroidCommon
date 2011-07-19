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

public class NetworkUsage extends StatElement implements Comparable<NetworkUsage>
{
	/**
	 * the uid
	 */
	private int m_uid;
	
	/**
	 * tcpBytes reived
	 */
	private long m_bytesReceived;
	
	/**
	 * tcpBytes sent
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
