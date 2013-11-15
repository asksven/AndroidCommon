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
public class AlarmDumpsysTests extends TestCase
{

	/**
	 * Test method for {@link com.asksven.android.common.kernelutils.AlarmsDumpsys#getAlarms()}.
	 */
	public void testGetAlarms()
	{
		ArrayList<StatElement> test4_3 = AlarmsDumpsys.getAlarmsFrom_4_3(getTestData_4_3());
		assertNotNull(test4_3);
		assertTrue(test4_3.size() > 1);
		System.out.print(test4_3);
	}

	static ArrayList<String> getTestData_4_3()
	{
		ArrayList<String> myRet = new ArrayList<String>()
				{{
					add("Alarm Stats:");
					add("  android +1m35s119ms running, 67 wakeups:");
					add("    +39s818ms 0 wakes 9 alarms: act=com.android.server.action.NETWORK_STATS_POLL");
					add("    +4s693ms 8 wakes 8 alarms: act=android.appwidget.action.APPWIDGET_UPDATE cmp={com.devexpert.weather/com.devexpert.weather.view.WidgetWeather5x2}");
					add("  com.google.android.gsf +5s50ms running, 30 wakeups:");
					add("    +5s50ms 30 wakes 30 alarms: cmp={com.google.android.gsf/com.google.android.gsf.checkin.EventLogService$Receiver}");
					add("  com.android.vending +46ms running, 4 wakeups:");
					add("    +29ms 1 wakes 1 alarms: cmp={com.android.vending/com.google.android.finsky.services.DailyHygiene}");
					add("    +17ms 3 wakes 3 alarms: cmp={com.android.vending/com.google.android.finsky.services.ContentSyncService}");
				}};
		return myRet;
	}
	static ArrayList<String> getTestData_4_2()
	{
		ArrayList<String> myRet = new ArrayList<String>()
				{{
					add("Alarm Stats:");
					add("  com.google.android.gsf");
					add("  8417ms running, 204 wakeups");
					add("  17 alarms: act=com.google.android.intent.action.GTALK_RECONNECT flg=0x4");
					add("  187 alarms: flg=0x4");
					add("  com.carl.trafficcounter");
					add("  446486ms running, 5584 wakeups");
					add("  5584 alarms: act=com.carl.trafficcounter.UPDATE_RUN flg=0x4");
				}};

		return myRet;
	}

}
