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

		ArrayList<StatElement> test2_3_7 = AlarmsDumpsys.getAlarmsPriorTo_4_2_2(getTestData_2_3_7());
		assertNotNull(test2_3_7);
		assertTrue(test2_3_7.size() > 1);
		System.out.print(test2_3_7);

		ArrayList<StatElement> test4_4_4 = AlarmsDumpsys.getAlarmsFrom_4_3(getTestData_4_4_4());
		assertNotNull(test4_4_4);
		assertTrue(test4_4_4.size() > 1);
		System.out.print(test4_4_4);

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
	
	static ArrayList<String> getTestData_2_3_7()
	{
		ArrayList<String> myRet = new ArrayList<String>()
				{{
					add("Alarm Stats:");
					add("  com.google.android.gsf");
					add("  Realtime wakeup (now=2014-07-01 13:18:57):");
					add("  RTC_WAKEUP #13: Alarm{409bfb70 type 0 com.google.android.gms}");
					add("    type=0 when=+5d7h36m8s686ms repeatInterval=579075000 count=0");
					add("    operation=PendingIntent{409e0ca0: PendingIntentRecord{40906778 com.google.android.gms broadcastIntent}}");
					add("  RTC_WAKEUP #12: Alarm{40a8fbb8 type 0 com.google.android.gms}");
					add("    type=0 when=+5d7h36m8s686ms repeatInterval=579075000 count=0");
					add("    operation=PendingIntent{40985ef8: PendingIntentRecord{40977218 com.google.android.gms broadcastIntent}}");
					add("  Alarm Stats:");
					add("  com.google.android.gsf");
					add("  73040ms running, 38 wakeups");
					add("    13 alarms: act=com.google.android.intent.action.GTALK_RECONNECT flg=0x4");
					add("    25 alarms: act=com.google.android.intent.action.GTALK_HEARTBEAT flg=0x4");
					add("  com.android.vending");
					add("    642ms running, 10 wakeups");
					add("    4 alarms: flg=0x4 cmp=com.android.vending/com.google.android.finsky.services.DailyHygiene");
					add("    6 alarms: flg=0x4 cmp=com.android.vending/com.google.android.finsky.services.ContentSyncService");
					add("  android");
					add("    651712ms running, 44 wakeups");
					add("    2 alarms: act=android.intent.action.DATE_CHANGED flg=0x20000004");
					add("    1112 alarms: act=android.intent.action.TIME_TICK flg=0x40000004");
					add("    110 alarms: act=com.android.server.ThrottleManager.action.POLL flg=0x4");
					add("    27 alarms: act=android.net.wifi.DHCP_RENEW flg=0x4");
					add("    4 alarms: act=android.bluetooth.profile_state.CONNECT flg=0x4");
					add("    6 alarms: act=android.content.syncmanager.SYNC_ALARM flg=0x4");
					add("    7 alarms: act=com.android.internal.policy.impl.KeyguardViewMediator.DELAYED_KEYGUARD flg=0x4");
					add("  com.quoord.tapatalkpro.activity");
					add("    9573ms running, 36 wakeups");
					add("    36 alarms: flg=0x4");
/*			  com.android.providers.calendar
			    8348ms running, 13 wakeups
			    1 alarms: act=android.intent.action.EVENT_REMINDER dat=content://com.android.calendar/1404212922000 flg=0x4
			    2 alarms: act=android.intent.action.EVENT_REMINDER dat=content://com.android.calendar/1404154819000 flg=0x4
			    2 alarms: act=android.intent.action.EVENT_REMINDER dat=content://com.android.calendar/1404210053000 flg=0x4
			    5 alarms: act=com.android.providers.calendar.SCHEDULE_ALARM flg=0x4
			    2 alarms: act=android.intent.action.EVENT_REMINDER dat=content://com.android.calendar/1404151203000 flg=0x4
			    1 alarms: act=android.intent.action.EVENT_REMINDER dat=content://com.android.calendar/1404153003000 flg=0x4
			  com.viber.voip
			    31314ms running, 111 wakeups
			    110 alarms: act=com.viber.voip.action.KEEP_ALIVE_RECEIVE flg=0x4
			    1 alarms: act=com.viber.voip.action.VERSION_CHECK flg=0x4
			  org.sipdroid.sipua
			    7413ms running, 144 wakeups
			    144 alarms: flg=0x4
			  com.android.deskclock
			    1189ms running, 1 wakeups
			    1 alarms: act=com.android.deskclock.ALARM_ALERT flg=0x4
			  com.google.android.gms
			    94959ms running, 67 wakeups
			    47 alarms: flg=0x4
			    1 alarms: act=com.google.android.gms.icing.INDEX_RECURRING_MAINTENANCE flg=0x4 cmp=com.google.android.gms/.icing.service.IndexWorkerService
			    24 alarms: act=com.google.android.intent.action.SEND_IDLE flg=0x4
			    5 alarms: act=com.google.android.intent.action.GCM_RECONNECT flg=0x4
			  com.android.phone
			    387ms running, 4 wakeups
			    4 alarms: act=com.android.phone.PhoneApp.ACTION_VIBRATE_45 flg=0x4
*/	
				}};
		return myRet;
	}


	static ArrayList<String> getTestData_4_4_4()
	{
		ArrayList<String> myRet = new ArrayList<String>()
			{{
				add("  Alarm Stats:");
				add("  com.android.keyguard +246ms running, 6 wakeups:");
				add("    +246ms 6 wakes 6 alarms: act=com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGUARD");
				add("  com.cyanogenmod.lockclock +4ms running, 0 wakeups:");
				add("    +4ms 0 wakes 2 alarms: act=com.cyanogenmod.lockclock.action.FORCE_WEATHER_UPDATE cmp={com.cyanogenmod.lockclock/com.cyanogenmod.lockclock.weather.WeatherUpdateService}");
				add("  android +1m23s914ms running, 10 wakeups:");
				add("    +1m21s585ms 0 wakes 446 alarms: act=android.intent.action.TIME_TICK");
				add("    +7s372ms 0 wakes 38 alarms: act=com.android.server.action.NETWORK_STATS_POLL");
				add("    +1s213ms 0 wakes 1 alarms: act=android.intent.action.DATE_CHANGED");
				add("    +763ms 6 wakes 6 alarms: act=android.content.syncmanager.SYNC_ALARM");
				add("    +205ms 3 wakes 3 alarms: act=com.android.server.IdleMaintenanceService.action.UPDATE_IDLE_MAINTENANCE_STATE");
				add("    +135ms 0 wakes 1 alarms: act=com.android.server.NetworkTimeUpdateService.action.POLL");
				add("    +134ms 1 wakes 1 alarms: act=android.net.ConnectivityService.action.PKT_CNT_SAMPLE_INTERVAL_ELAPSED");
				add("  com.android.deskclock +24s199ms running, 0 wakeups:");
				add("    +24s199ms 0 wakes 63 alarms: act=com.android.deskclock.ON_QUARTER_HOUR");
				add("  com.google.android.gms +51s865ms running, 0 wakeups:");
				add("    +34s205ms 0 wakes 2 alarms: cmp={com.google.android.gms/com.google.android.gms.playlog.uploader.UploaderAlarmReceiver}");
				add("    +17s530ms 0 wakes 10 alarms: cmp={com.google.android.gms/com.google.android.gms.common.download.DownloadAlarmReceiver}");
				add("    +88ms 0 wakes 2 alarms: cmp={com.google.android.gms/com.google.android.gms.security.snet.SnetService}");
				add("    +42ms 0 wakes 1 alarms: act=com.google.android.gms.icing.INDEX_RECURRING_MAINTENANCE cmp={com.google.android.gms/com.google.android.gms.icing.service.IndexWorkerService}");
				add("  com.touchtype.swiftkey +34s244ms running, 0 wakeups:");
				add("    +32s314ms 0 wakes 1 alarms: act=com.touchtype.ACTION_SCHEDULED_JOB cmp={com.touchtype.swiftkey/com.touchtype.CustomUpdaterScheduledJob}");
				add("    +1s855ms 0 wakes 3 alarms: act=com.touchtype.ACTION_SCHEDULED_JOB cmp={com.touchtype.swiftkey/com.touchtype.RefreshLanguageConfigurationScheduledJob}");
				add("    +75ms 0 wakes 1 alarms: act=com.touchtype.ACTION_SCHEDULED_JOB cmp={com.touchtype.swiftkey/com.touchtype.KeyboardUsesDailyScheduledJob}");
				add("    +2ms 0 wakes 1 alarms: cmp={com.touchtype.swiftkey/com.touchtype.ReferrerRegistrationService}");
				add("  com.android.phone +189ms running, 0 wakeups:");
				add("    +189ms 0 wakes 2 alarms: act=com.android.phone.UPDATE_CALLER_INFO_CACHE cmp={com.android.phone/com.android.phone.CallerInfoCacheUpdateReceiver}");
			}};
			return myRet;
	}	
}
