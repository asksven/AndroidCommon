/**
 * 
 */
package com.asksven.android.common.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.asksven.android.common.RootShell;

/**
 * @author sven
 *
 */
public class SystemAppInstaller
{
	static final String TAG = "SystemAppInstaller";
	
	static final String SYSTEM_DIR_4_4		= "/system/priv-app";
	static final String SYSTEM_DIR			= "/system/app";
	
	static final String REMOUNT_SYSTEM_RW 	= "mount -o rw,remount /system";
	static final String REMOUNT_SYSTEM_RO 	= "mount -o ro,remount /system";
	// returns ro or rw
	static final String CHECK_MOUNT_STATE 	= "mount | grep /system | awk '{print $4}' | awk -F\",\" '{print $1}'";
	
	
	public static boolean mountSystemRw()
	{
		if (isSystemRw()) return true;
		
		Log.i(TAG, "Remount system rw");
		RootShell.getInstance().run(REMOUNT_SYSTEM_RW);
		
		return isSystemRw();
		
	}
	
	public static boolean mountSystemRo()
	{
		if (!isSystemRw()) return true;
		
		Log.i(TAG, "Remount system ro");
		RootShell.getInstance().run(REMOUNT_SYSTEM_RO);
		
		return !isSystemRw();
		
	}

	public static boolean isSystemRw()
	{
		boolean ret = false;
		Log.i(TAG, "Checking if system is mounted rw");
		List<String> res = RootShell.getInstance().run(CHECK_MOUNT_STATE);
		if (res.size() > 0)
		{
			Log.i(TAG, "Mount status: " + res.get(0));
			ret = res.get(0).equals("rw");
		}
		
		return ret;
	}
	
	public static boolean isSystemApp(String apk)
	{
		boolean ret = false;
		List<String> res;
		
		String command = "";
		if (Build.VERSION.SDK_INT >= 19)
		{
			command = "ls " + SYSTEM_DIR_4_4 + "/" + apk + "*";
		}
		else
		{
			command = "ls " + SYSTEM_DIR + "/" + apk + "*";
		}

		Log.i(TAG, "Checking if " + apk + " is a system app");
		res = RootShell.getInstance().run(command);
		
		if (res.size() > 0)
		{
			Log.i(TAG, "Command returned "+ res.get(0));
			ret = !res.get(0).contains("No such file or directory");
		}
		
		return ret;
	}
	
	static boolean installAsSystemApp(String apk)
	{
		String command = "";
		if (Build.VERSION.SDK_INT >= 19)
		{
			command = 	"cp /data/app/" + apk + "* " + SYSTEM_DIR_4_4 + " && chmod 644 " + SYSTEM_DIR_4_4 + "/" + apk + "*";
		}
		else
		{
			command = 	"cp /data/app/" + apk + "* " + SYSTEM_DIR + " && chmod 644 " + SYSTEM_DIR + "/" + apk + "*";
		}
		
		Log.i(TAG, "Installing app as system app: " + command);
		RootShell.getInstance().run(command);
		
		return isSystemApp(apk);
	}
	
	static boolean uninstallAsSystemApp(String apk)
	{
		String command = "";
	
		if (Build.VERSION.SDK_INT >= 19)
		{	
			command = "rm " + SYSTEM_DIR_4_4 + "/" + apk + "*";
		}
		else
		{
			command = "rm " + SYSTEM_DIR + "/" + apk + "*";
		}
		
		Log.i(TAG, "Uninstalling system app: " + command);
		RootShell.getInstance().run(command);
		
		return !isSystemApp(apk);
	}

	public static Status install(String apk)
	{
		Status status = new Status();
		
		SystemAppInstaller.mountSystemRw();
		if (SystemAppInstaller.isSystemRw())
		{
			status.add("Mounted system rw");
			SystemAppInstaller.installAsSystemApp(apk);
			status.add("Install as system app");
			if (SystemAppInstaller.isSystemApp(apk))
			{
				SystemAppInstaller.mountSystemRo();
				if (!SystemAppInstaller.isSystemRw())
				{
					status.add("Mounted system ro. Finished");
				}
				else
				{
					status.add("An error while remounting system to ro. Aborted");
					status.m_success = false;
				}
			}
			else
			{
				status.add("An error while installing app. Aborted");
				status.m_success = false;
			}
			
		}
		else
		{
			status.add("An error occured mounting system rw. Aborted");
			status.m_success = false;
		}
		
		return status;
	}
	
	public static Status uninstall(String apk)
	{
		Status status = new Status();
		SystemAppInstaller.mountSystemRw();
		if (SystemAppInstaller.isSystemRw())
		{
			status.add("Mounted system rw");
			SystemAppInstaller.uninstallAsSystemApp(apk);
			status.add("Uninstall as system app");
			if (!SystemAppInstaller.isSystemApp(apk))
			{
				SystemAppInstaller.mountSystemRo();
				if (!SystemAppInstaller.isSystemRw())
				{
					status.add("Mounted system ro. Finished");
				}
				else
				{
					status.add("An error while remounting system to ro. Aborted");
					status.m_success = false;
				}
			}
			else
			{
				status.add("An error while uninstalling app. Aborted");
				status.m_success = false;
			}	
		}
		else
		{
			status.add("An error occured mounting system rw. Aborted");
			status.m_success = false;
		}
		
		return status;
	}
		
	public static class Status
	{
		List<String> m_status = new ArrayList<String>();
		boolean m_success = true;
		
		void add(String text)
		{
			Log.i(TAG, "Status: " + text);
			m_status.add(text);
		}
		
		public boolean success()
		{
			return m_success;
		}
		
		public boolean getSuccess()
		{
			return m_success;
		}
		
		public String toString()
		{
			String ret = "";
			for (int i=0; i < m_status.size(); i++)
			{
				ret += m_status.get(i) + "\n";
			}
			
			return ret;
		}
	}	
}
