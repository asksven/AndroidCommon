/**
 * 
 */
package com.asksven.android.common.kernelutils;

import java.util.ArrayList;
import java.util.List;

import com.asksven.android.common.privateapiproxies.StatElement;

import junit.framework.TestCase;

/**
 * @author sven
 *
 */
public class NetstatsTests extends TestCase
{

	/**
	 * Test method for {@link com.asksven.android.common.kernelutils.Netstats#parseNetstats(List))}.
	 */
	public void testGetAlarms()
	{
		ArrayList<StatElement> test1 = Netstats.parseNetstats(getTestData_1());
		assertNotNull(test1);
		assertTrue(test1.size() > 1);
		System.out.print(test1);
	}

	static ArrayList<String> getTestData_1()
	{
		ArrayList<String> myRet = new ArrayList<String>()
				{{
					add("idx iface acct_tag_hex uid_tag_int cnt_set rx_bytes rx_packets tx_bytes tx_packets rx_tcp_bytes rx_tcp_packets rx_udp_bytes rx_udp_packets rx_other_bytes rx_other_packets tx_tcp_bytes tx_tcp_packets tx_udp_bytes tx_udp_packets tx_other_bytes tx_other_packets");
					add("2 rmnet0 0x0 10054 0 126043 536 83128 521 126043 536 0 0 0 0 83128 521 0 0 0 0");
					add("3 rmnet0 0x0 10054 1 797329 826 84364 551 797329 826 0 0 0 0 84364 551 0 0 0 0");
					add("4 rmnet0 0x1010000000000000 10054 0 113846 407 75741 372 113846 407 0 0 0 0 75741 372 0 0 0 0");
					add("5 rmnet0 0x1010000000000000 10054 1 9618 41 12646 43 9618 41 0 0 0 0 12646 43 0 0 0 0");
					add("6 rmnet0 0xffffff0100000000 10054 0 7595 14 1663 14 7595 14 0 0 0 0 1663 14 0 0 0 0");
					add("7 rmnet0 0xffffff0100000000 10054 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
					add("8 wlan0 0x0 10054 0 130404 350 72333 387 130404 350 0 0 0 0 72333 387 0 0 0 0");
					add("9 wlan0 0x0 10054 1 66344 113 24746 124 66344 113 0 0 0 0 24746 124 0 0 0 0");
					add("10 wlan0 0x1010000000000000 10054 0 107098 302 64738 321 107098 302 0 0 0 0 64738 321 0 0 0 0");
					add("11 wlan0 0x1010000000000000 10054 1 12130 69 20782 74 12130 69 0 0 0 0 20782 74 0 0 0 0");
					add("12 wlan0 0xffffff0100000000 10054 0 21831 27 3570 26 21831 27 0 0 0 0 3570 26 0 0 0 0");
					add("13 wlan0 0xffffff0100000000 10054 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
					add("14 wlan0 0xffffff0300000000 10054 0 591 4 2117 4 591 4 0 0 0 0 2117 4 0 0 0 0");
					add("15 wlan0 0xffffff0300000000 10054 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");

				}};
		return myRet;
	}
}
