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
	public void testGetAlarms4_3()
	{
		ArrayList<StatElement> test4_3 = AlarmsDumpsys.getAlarmsFrom_4_3(getTestData_4_3());
		assertNotNull(test4_3);
		assertTrue(test4_3.size() > 1);
		System.out.println(this.getName() + ":" + test4_3);
	}
	public void testGetAlarms2_3_7()
	{
		ArrayList<StatElement> test2_3_7 = AlarmsDumpsys.getAlarmsPriorTo_4_2_2(getTestData_2_3_7());
		assertNotNull(test2_3_7);
		assertTrue(test2_3_7.size() > 1);
		System.out.println(this.getName() + ":" + test2_3_7);
	}
	public void testGetAlarms4_4_4()
	{
		ArrayList<StatElement> test4_4_4 = AlarmsDumpsys.getAlarmsFrom_4_3(getTestData_4_4_4());
		assertNotNull(test4_4_4);
		assertTrue(test4_4_4.size() > 1);
		System.out.println(this.getName() + ":" + test4_4_4);
	}
	
	public void testGetAlarms6()
	{
		ArrayList<StatElement> test6 = AlarmsDumpsys.getAlarmsFrom_6(getTestData_6());
		assertNotNull(test6);
		assertTrue(test6.size() > 1);
		System.out.println(this.getName() + ":" + test6);
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

	static ArrayList<String> getTestData_6()
	{
		ArrayList<String> myRet = new ArrayList<String>()
			{{
				add("  Alarm Stats:");
				add("  1000:android +2m36s732ms running, 6 wakeups:");
				add("    +2m5s454ms 1 wakes 1 alarms, last -16m28s887ms:");
				add("      *walarm*:android.net.netmon.lingerExpired_100_-2115997319");
				add("    +22s954ms 0 wakes 2 alarms, last -13m31s364ms:");
				add("      *alarm*:android.content.jobscheduler.JOB_DELAY_EXPIRED");
				add("    +9s623ms 0 wakes 18 alarms, last -34s554ms:");
				add("      *alarm*:android.intent.action.TIME_TICK");
				add("    +4s71ms 0 wakes 1 alarms, last -17m32s307ms:");
				add("      *alarm*:com.android.server.action.NETWORK_STATS_POLL");
				add("    +171ms 1 wakes 1 alarms, last -6m36s811ms:");
				add("      *walarm*:ScheduleConditionProvider.EVALUATE");
				add("    +23ms 4 wakes 4 alarms, last -2m59s811ms:");
				add("      *walarm*:android.content.syncmanager.SYNC_ALARM");
				add("  1001:com.android.phone +1m37s675ms running, 1 wakeups:");
				add("    +1m37s675ms 1 wakes 1 alarms, last -15m56s941ms:");
				add("      *walarm*:com.android.internal.telephony.data-stall");
			}};
			return myRet;
	}
/*		add("  1002:com.android.bluetooth +3m15s184ms running, 21 wakeups:");
		add("    +3m15s184ms 21 wakes 21 alarms, last -18s817ms:");
		add("      *walarm*:com.android.bluetooth.btservice.action.ALARM_WAKEUP");
		add("  u0a1:com.android.providers.calendar +2m22s315ms running, 2 wakeups:")
		add("    +2m22s315ms 2 wakes 2 alarms, last -15m48s354ms:");
		      *walarm*:com.android.providers.calendar.intent.CalendarProvider2
		  u0a12:com.google.android.gms +3m4s145ms running, 31 wakeups:
		    +2m27s979ms 3 wakes 3 alarms, last -13m21s7ms:
		      *walarm*:package com.google.android.gms.auth.trustagent.trustlet.CONNECTIO
		N_ALARM
		    +1m25s181ms 2 wakes 2 alarms, last -8m7s354ms:
		      *walarm*:com.google.android.intent.action.SEND_IDLE
		    +1m1s42ms 6 wakes 6 alarms, last -6m31s231ms:
		      *walarm*:ALARM_WAKEUP_ACTIVITY_DETECTION
		    +32s892ms 2 wakes 2 alarms, last -6m31s231ms:
		      *walarm*:ALARM_WAKEUP_BURST_COLLECTION_TRIGGER
		    +1s33ms 15 wakes 15 alarms, last -31s64ms:
		      *walarm*:ALARM_WAKEUP_LOCATOR
		    +18ms 1 wakes 1 alarms, last -34s554ms:
		      *walarm*:com.google.android.gms.gcm.HEARTBEAT_ALARM
		    +5ms 2 wakes 2 alarms, last -13m17s194ms:
		      *walarm*:com.google.android.gms/com.google.android.libraries.social.mediam
		onitor.MediaMonitorIntentService
		  u0a22:com.android.vending +1m21s187ms running, 5 wakeups:
		    +1m21s141ms 1 wakes 1 alarms, last -15m33s738ms:
		      *walarm*:com.android.vending/com.google.android.finsky.receivers.FlushLogs
		Receiver
		    +36ms 3 wakes 3 alarms, last -13m3s454ms:
		      *walarm*:com.android.vending/com.google.android.finsky.services.ContentSyn
		cService
		    +34ms 1 wakes 1 alarms, last -15m56s941ms:
		      *walarm*:com.android.vending/com.google.android.finsky.services.DailyHygie
		ne
		  u0a27:com.android.systemui +1m40s74ms running, 3 wakeups:
		    +1m40s74ms 3 wakes 3 alarms, last -8m33s288ms:
		      *walarm*:com.android.internal.policy.impl.PhoneWindowManager.DELAYED_KEYGU
		ARD
		  u0a53:com.google.android.talk +2m5s430ms running, 1 wakeups:
		    +2m5s430ms 1 wakes 1 alarms, last -16m28s887ms:
		      *walarm*:com.google.android.apps.hangouts.RENEW_ACCOUNT_REGISTRATION
		  u0a59:com.google.android.music +1m42s170ms running, 3 wakeups:
		    +1m42s170ms 3 wakes 3 alarms, last -14m48s814ms:
		      *walarm*:com.google.android.music.leanback.AUTO_CACHE_ALARM
		  u0a66:com.google.android.deskclock +335ms running, 0 wakeups:
		    +335ms 0 wakes 1 alarms, last -6m31s231ms:
		      *alarm*:com.android.deskclock.ON_QUARTER_HOUR
		  u0a81:com.google.android.keep +34ms running, 1 wakeups:
		    +34ms 1 wakes 1 alarms, last -15m56s941ms:
		      *walarm*:com.google.android.keep.intent.action.LOG_REMINDERS
		  u0a102:com.whatsapp +2m5s462ms running, 4 wakeups:
		    +2m5s453ms 3 wakes 3 alarms, last -8m39s121ms:
		      *walarm*:com.whatsapp.messaging.MessageService.CLIENT_PINGER_ACTION
		    +2m5s403ms 0 wakes 1 alarms, last -16m28s887ms:
		      *alarm*:com.whatsapp.action.HOURLY_CRON
		    +2m5s371ms 0 wakes 1 alarms, last -16m28s887ms:
		      *alarm*:com.whatsapp.action.UPDATE_NTP
		    +9ms 1 wakes 1 alarms, last -7m1s117ms:
		      *walarm*:com.whatsapp.messaging.MessageService.LOGOUT_ACTION
		  u0a103:com.devexpert.weather +2m39s978ms running, 0 wakeups:
		    +2m39s978ms 0 wakes 17 alarms, last -31s64ms:
		      *alarm*:com.devexpert.weather.pfx.WAKEUP
		  u0a120:ch.threema.app +109ms running, 1 wakeups:
		    +109ms 1 wakes 1 alarms, last -11m35s657ms:
		      *walarm*:ch.threema.app/.receivers.AlarmManagerBroadcastReceiver
		  u0a124:com.touchtype.swiftkey +32s864ms running, 0 wakeups:
		    +32s864ms 0 wakes 1 alarms, last -17m26s737ms:
		      *alarm*:com.touchtype.ACTION_SCHEDULED_JOB
		  u0a136:com.skype.raider +2m33s539ms running, 0 wakeups:
		    +2m33s539ms 0 wakes 3 alarms, last -13m3s454ms:
		      *alarm*:com.skype.raider/com.skype.android.service.ContactsAlarmReceiver
		  u0a160:com.levelup.touiteur +52ms running, 4 wakeups:
		    +52ms 4 wakes 4 alarms, last -14m31s33ms:
		      *walarm*:com.levelup.touiteur.mute.LIST
		  u0a195:com.vito.lux +325ms running, 0 wakeups:
		    +325ms 0 wakes 44 alarms, last -8m39s121ms:
		      *alarm*:com.vito.lux/.CompatibilityCheck
		  u0a203:com.sonelli.juicessh +524ms running, 2 wakeups:
		    +524ms 1 wakes 1 alarms, last -10m34s544ms:
		      *walarm*:com.sonelli.juicessh.action.EC2LINK
		    +495ms 1 wakes 1 alarms, last -10m34s544ms:
		      *walarm*:com.sonelli.juicessh.action.CLOUDSYNC
		  u0a247:com.facebook.katana +1m27s285ms running, 1 wakeups:
		    +1m27s285ms 1 wakes 1 alarms, last -15m43s485ms:
		      *walarm*:com.facebook.common.executors.WakingExecutorService.ACTION_ALARM.
		com.facebook.katana.Mqtt_Wakeup
		  u0a250:mobi.drupe.app +1m59s454ms running, 3 wakeups:
		    +1m58s157ms 2 wakes 2 alarms, last -16m18s126ms:
		      *walarm*:mobi.drupe.app/.receivers.RetentionReceiver
		    +1m37s675ms 1 wakes 1 alarms, last -15m56s941ms:
		      *walarm*:mobi.drupe.app/.receivers.StatPeriodicReceiver
		  u0a293:de.amazon.mShop.android +166ms running, 0 wakeups:
		    +133ms 0 wakes 7 alarms, last -3m31s127ms:
		      *alarm*:de.amazon.mShop.android/com.amazon.avod.syncservice.MShopSyncServiceProxy
		    +26ms 0 wakes 1 alarms, last -13m31s364ms:
		      *alarm*:com.amazon.mas.client.pdiservice.PdiService.cleanup
		    +26ms 0 wakes 1 alarms, last -13m31s364ms:
		      *alarm*:com.amazon.mas.client.tokenrefresh.TokenRefreshService.refresh.ddi
		.token
		    +15ms 0 wakes 1 alarms, last -9m10s157ms:
		      *alarm*:com.amazon.venezia.notification.FLUSH_NOTIFICATION_CACHE
*/



}






