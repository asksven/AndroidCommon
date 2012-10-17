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
package com.asksven.android.common.kernelutils;

import com.asksven.android.common.contrib.Util;
import com.asksven.android.common.privateapiproxies.NetworkUsage;
import com.asksven.android.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parses the content of /proc/net/xt_qtaguid/stats
 * instead of using the API functions NetworkStats stats = new NetworkStatsFactory().readNetworkStatsDetail(uid-here) because http://stackoverflow.com/questions/9080229/why-i-cant-read-proc-net-xt-qtaguid-stats-correctly-by-filereader-in-android-i
 * format is:
 * idx iface acct_tag_hex uid_tag_int cnt_set rx_bytes rx_packets tx_bytes tx_packets rx_tcp_bytes rx_tcp_packets rx_udp_bytes rx_udp_packets rx_other_bytes rx_other_packets tx_tcp_bytes tx_tcp_packets tx_udp_bytes tx_udp_packets tx_other_bytes tx_other_packets
 * example data:
 * 2 bnep1 0x0 0 0 21672 217 101512 526 0 0 16940 200 4732 17 0 0 50421 174 51091 352
 * 3 bnep1 0x0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 * 4 bnep0 0x0 0 0 151693 2305 408790 2375 334 7 151359 2298 0 0 1010 5 309743 1720 98037 650
 * 5 bnep0 0x0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 * 6 wl0.1 0x0 0 0 10836 131 18274 139 0 0 8716 109 2120 22 0 0 15114 96 3160 43
 * 7 wl0.1 0x0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 * 8 wlan0 0x0 0 0 6449931 34871 624544 8518 335259 2562 5965630 30745 149042 1564 12579 203 467333 7261 144632 1054
 * 9 wlan0 0x0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 * 10 wlan0 0x0 1000 0 12096 146 14679 194 11944 144 152 2 0 0 14332 189 347 5 0 0
 * 11 wlan0 0x0 1000 1 1885 10 1867 26 1885 10 0 0 0 0 1466 20 401 6 0 0
 * 12 wlan0 0x0 1014 0 317376 551 238620 615 0 0 317376 551 0 0 0 0 238620 615 0 0
 * 13 wlan0 0x0 1014 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 * 14 wlan0 0x0 10003 0 697215 713 84843 848 697215 713 0 0 0 0 84371 840 472 8 0 0
 * 15 wlan0 0x0 10003 1 1938719 2030 321314 2437 1938719 2030 0 0 0 0 321314 2437 0 0 0 0
 * value holder is Netstat
 *
 * @author sven
 */
public class Netstats {
    static final String TAG = "AlarmsDumpsys";

    private static final String KEY_IDX = "idx";
    private static final String KEY_IFACE = "iface";
    private static final String KEY_UID = "uid_tag_int";
    private static final String KEY_COUNTER_SET = "cnt_set";
    private static final String KEY_TAG_HEX = "acct_tag_hex";
    private static final String KEY_RX_BYTES = "rx_bytes";
    private static final String KEY_RX_PACKETS = "rx_packets";
    private static final String KEY_TX_BYTES = "tx_bytes";
    private static final String KEY_TX_PACKETS = "tx_packets";


    /**
     * @return list of alarm value objects
     */
    public static ArrayList<NetworkUsage> parseNetstats() {
        ArrayList<NetworkUsage> myStats = new ArrayList<NetworkUsage>();
//		ExecResult res = Exec.execPrint(new String[]{"su", "-c", "cat /proc/net/xt_qtaguid/stats"});
        ArrayList<String> res = Util.run("su", "cat /proc/net/xt_qtaguid/stats");
//		if (res.getSuccess())
        if (res.size() != 0) {
//			String strRes = res.getResultLine(); 
            if (true) //(!strRes.contains("Permission Denial"))
            {
                ArrayList<String> keys = new ArrayList<String>();
                keys.add(KEY_IDX);
                keys.add(KEY_IFACE);
                keys.add(KEY_TAG_HEX);
                keys.add(KEY_UID);
                keys.add(KEY_COUNTER_SET);
                keys.add(KEY_RX_BYTES);
                keys.add(KEY_RX_PACKETS);
                keys.add(KEY_TX_BYTES);
                keys.add(KEY_TX_PACKETS);

                final ArrayList<String> values = new ArrayList<String>();
                final HashMap<String, String> parsed = new HashMap<String, String>();

//				ArrayList<String> myRes = res.getResult(); // getTestData();


                // process the file, starting on line 2
                long totalBytes = 0;
                for (int i = 1; i < res.size(); i++) {
                    String line = res.get(i);
                    StringUtils.splitLine(line, values);
                    StringUtils.parseLine(keys, values, parsed);

                    //Netstat entry = new Netstat();
                    NetworkUsage entry = new NetworkUsage(
                            StringUtils.getParsedInt(parsed, KEY_UID),
                            parsed.get(KEY_IFACE),
                            StringUtils.getParsedLong(parsed, KEY_RX_BYTES),
                            StringUtils.getParsedLong(parsed, KEY_TX_BYTES));

//					entry.iface = parsed.get(KEY_IFACE);
//					entry.setUid(getParsedInt(parsed, KEY_UID));
//					entry.set = getParsedInt(parsed, KEY_COUNTER_SET);
//					
//					entry.rxBytes 	= getParsedLong(parsed, KEY_RX_BYTES);
//					entry.rxPackets	= getParsedLong(parsed, KEY_RX_PACKETS);
//					entry.txBytes 	= getParsedLong(parsed, KEY_TX_BYTES);
//					entry.txPackets = getParsedLong(parsed, KEY_TX_PACKETS);

                    myStats = addToStats(myStats, entry);
                    totalBytes += entry.getTotalBytes();
                }

                // set the total so that we can calculate the ratio
                for (NetworkUsage myStat : myStats) {
                    myStat.setTotal(totalBytes);
                }

            }
        }

        return myStats;
    }


    /**
     * Stats may be duplicate for one uid+iface so we sum them up
     *
     * @param stats stats which should add to
     * @param entry entry to add
     * @return stats with added entry if applicable
     */
    static ArrayList<NetworkUsage> addToStats(ArrayList<NetworkUsage> stats, NetworkUsage entry) {
        boolean merged = false;
        for (NetworkUsage current : stats) {
            if ((current.getuid() == entry.getuid()) && (current.getInterface().equals(entry.getInterface()))) {
                current.addBytesReceived(entry.getBytesReceived());
                current.addBytesSent(entry.getBytesSent());
                merged = true;
                break;
            }
        }

        // if not summed up add normally
        if (!merged) {
            stats.add(entry);
        }
        return stats;
    }
}
