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


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

import com.asksven.android.common.nameutils.UidInfo;
import com.asksven.android.common.nameutils.UidNameResolver;
import com.asksven.android.common.utils.DateUtils;
import com.asksven.android.system.AndroidVersion;



/**
 * A proxy to the non-public API BatteryStats
 * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.3_r1/android/os/BatteryStats.java/?v=source
 * @author sven
 *
 */
public class BatteryStatsProxy
{
	/*
	 * Instance of the BatteryStatsImpl
	 */
	private Object m_Instance = null;
	@SuppressWarnings("rawtypes")
	private Class m_ClassDefinition = null;
	
	private static final String TAG = "BatteryStatsProxy";
	/*
	 * The UID stats are kept here as their methods / data can not be accessed
	 * outside of this class due to non-public types (Uid, Proc, etc.)
	 */
	private SparseArray<? extends Object> m_uidStats = null;
	
	/** 
	 * An instance to the UidNameResolver 
	 */
	private UidNameResolver m_nameResolver;
	private static BatteryStatsProxy m_proxy = null;
	
	public static BatteryStatsProxy getInstance(Context ctx)
	{
		if (m_proxy == null)
		{
			m_proxy = new BatteryStatsProxy(ctx);
		}
		
		return m_proxy;
	}
	
	public void invalidate()
	{
		m_proxy = null;
	}
	
    /**
	 * Default cctor
	 */
	private BatteryStatsProxy(Context context)
	{
		/*
		 * As BatteryStats is a service we need to get a binding using the IBatteryStats.Stub.getStatistics()
		 * method (using reflection).
		 * If we would be using a public API the code would look like:
		 * @see com.android.settings.fuelgauge.PowerUsageSummary.java 
		 * protected void onCreate(Bundle icicle) {
         *  super.onCreate(icicle);
		 *	
         *  mStats = (BatteryStatsImpl)getLastNonConfigurationInstance();
		 *
         *  addPreferencesFromResource(R.xml.power_usage_summary);
         *  mBatteryInfo = IBatteryStats.Stub.asInterface(
         *       ServiceManager.getService("batteryinfo"));
         *  mAppListGroup = (PreferenceGroup) findPreference("app_list");
         *  mPowerProfile = new PowerProfile(this);
    	 * }
		 *
		 * followed by
		 * private void load() {
         *	try {
         *   byte[] data = mBatteryInfo.getStatistics();
         *   Parcel parcel = Parcel.obtain();
         *   parcel.unmarshall(data, 0, data.length);
         *   parcel.setDataPosition(0);
         *   mStats = com.android.internal.os.BatteryStatsImpl.CREATOR
         *           .createFromParcel(parcel);
         *   mStats.distributeWorkLocked(BatteryStats.STATS_SINCE_CHARGED);
         *  } catch (RemoteException e) {
         *   Log.e(TAG, "RemoteException:", e);
         *  }
         * }
		 */
		
		m_nameResolver = new UidNameResolver();
		
		try
		{
	          ClassLoader cl = context.getClassLoader();
	          
	          m_ClassDefinition = cl.loadClass("com.android.internal.os.BatteryStatsImpl");
	          
	          // get the IBinder to the "batteryinfo" service
	          @SuppressWarnings("rawtypes")
			  Class serviceManagerClass = cl.loadClass("android.os.ServiceManager");
	          
	          // parameter types
	          @SuppressWarnings("rawtypes")
			  Class[] paramTypesGetService= new Class[1];
	          paramTypesGetService[0]= String.class;
	          
	          @SuppressWarnings("unchecked")
			  Method methodGetService = serviceManagerClass.getMethod("getService", paramTypesGetService);
	          
	          // parameters
	          Object[] paramsGetService= new Object[1];
	          paramsGetService[0] = "batteryinfo";
	     
	          Log.i(TAG, "invoking android.os.ServiceManager.getService(\"batteryinfo\")");
	          IBinder serviceBinder = (IBinder) methodGetService.invoke(serviceManagerClass, paramsGetService); 

	          Log.i(TAG, "android.os.ServiceManager.getService(\"batteryinfo\") returned a service binder");
	          // now we have a binder. Let's us that on IBatteryStats.Stub.asInterface
	          // to get an IBatteryStats
	          // Note the $-syntax here as Stub is a nested class
	          @SuppressWarnings("rawtypes")
			  Class iBatteryStatsStub = cl.loadClass("com.android.internal.app.IBatteryStats$Stub");

	          //Parameters Types
	          @SuppressWarnings("rawtypes")
			  Class[] paramTypesAsInterface= new Class[1];
	          paramTypesAsInterface[0]= IBinder.class;

	          @SuppressWarnings("unchecked")
			  Method methodAsInterface = iBatteryStatsStub.getMethod("asInterface", paramTypesAsInterface);

	          // Parameters
	          Object[] paramsAsInterface= new Object[1];
	          paramsAsInterface[0] = serviceBinder;
	          	          
	          Log.i(TAG, "invoking com.android.internal.app.IBatteryStats$Stub.asInterface");
	          Object iBatteryStatsInstance = methodAsInterface.invoke(iBatteryStatsStub, paramsAsInterface);
	          
	          // and finally we call getStatistics from that IBatteryStats to obtain a Parcel
	          @SuppressWarnings("rawtypes")
			  Class iBatteryStats = cl.loadClass("com.android.internal.app.IBatteryStats");
	          
	          @SuppressWarnings("unchecked")
	          Method methodGetStatistics = iBatteryStats.getMethod("getStatistics");
	          
	          Log.i(TAG, "invoking getStatistics");
	          byte[] data = (byte[]) methodGetStatistics.invoke(iBatteryStatsInstance);
	          
	          Log.i(TAG, "retrieving parcel");
	          
	          Parcel parcel = Parcel.obtain();
	          parcel.unmarshall(data, 0, data.length);
	          parcel.setDataPosition(0);
	          
	          @SuppressWarnings("rawtypes")
			  Class batteryStatsImpl = cl.loadClass("com.android.internal.os.BatteryStatsImpl");

	          Log.i(TAG, "reading CREATOR field");
	          Field creatorField = batteryStatsImpl.getField("CREATOR");
	          
	          // From here on we don't need reflection anymore
	          @SuppressWarnings("rawtypes")
			  Parcelable.Creator batteryStatsImpl_CREATOR = (Parcelable.Creator) creatorField.get(batteryStatsImpl); 
	          
	          m_Instance = batteryStatsImpl_CREATOR.createFromParcel(parcel);        
	    }
		catch( Exception e )
		{
			Log.e("TAG", "An exception occured in BatteryStatsProxy(). Message: " + e.getMessage());
	    	m_Instance = null;
	    }    
	}
	
	/**
	 * Returns true if the proxy could not be initialized properly
	 * @return true if the proxy wasn't initialized
	 */
	public boolean initFailed()
	{
		return m_Instance == null;
	}
	
	/**
     * Returns the total, last, or current battery realtime in microseconds.
     *
     * @param curTime the current elapsed realtime in microseconds.
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long computeBatteryRealtime(long curTime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("computeBatteryRealtime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(curTime);
          params[1]= new Integer(iStatsType);

			ret = (Long) method.invoke(m_Instance, params);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current battery realtime in microseconds.
     *
     * @param curTime the current elapsed realtime in microseconds.
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getBatteryRealtime(long curTime)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[1];
          paramTypes[0]= long.class;
         

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getBatteryRealtime", paramTypes);

          //Parameters
          Object[] params= new Object[1];
          params[0]= new Long(curTime);


          ret= (Long) method.invoke(m_Instance, params);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current battery uptime in microseconds.
     *
     * @param curTime the current elapsed realtime in microseconds.
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long computeBatteryUptime(long curTime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("computeBatteryUptime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(curTime);
          params[1]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current screen on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getScreenOnTime(long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getScreenOnTime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(batteryRealtime);
          params[1]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current phone on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getPhoneOnTime(long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getPhoneOnTime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(batteryRealtime);
          params[1]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current wifi on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getWifiOnTime(long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getWifiOnTime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(batteryRealtime);
          params[1]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);
          Log.i("TAG", "getWifiOnTime with params " + params[0] + " and " + params[1] +  " returned " + ret);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current wifi on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getGlobalWifiRunningTime(long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getGlobalWifiRunningTime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(batteryRealtime);
          params[1]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);
          Log.i("TAG", "getGlobalWifiRunningTime with params " + params[0] + " and " + params[1] +  " returned " + ret);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the total, last, or current wifi running time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getWifiRunningTime(Context context, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

    	this.collectUidStats();
		if (m_uidStats != null)
		{
	        try
	        {
				
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");

				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					@SuppressWarnings("rawtypes")
		        	Class[] paramTypes= new Class[2];
		        	paramTypes[0]= long.class;
		        	paramTypes[1]= int.class;          
	
		        	@SuppressWarnings("unchecked")
		        	Method method = iBatteryStatsUid.getMethod("getWifiRunningTime", paramTypes);
	
		        	//Parameters
		        	Object[] params= new Object[2];
		        	params[0]= new Long(batteryRealtime);
		        	params[1]= new Integer(iStatsType);
		        	
		        	ret += (Long) method.invoke(myUid, params);
		        	
		        	Log.i(TAG, "getWifiRunningTime with params " + params[0] + " and " + params[1] + " returned " + ret);
		        	
	    	
		        }
	        }
	        catch( IllegalArgumentException e )
	        {
	        	Log.e(TAG, "getWifiRunning threw an IllegalArgumentException: " + e.getMessage());
	            throw e;
	        }
	        catch( Exception e )
	        {
	        	Log.e(TAG, "getWifiRunning threw an Exception: " + e.getMessage());
	            ret = new Long(0);
	        }
		}
        return ret;
	}

	/**
     * Returns the total, last, or current wifi lock time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getFullWifiLockTime(Context context, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

    	this.collectUidStats();
		if (m_uidStats != null)
		{
	        try
	        {
				
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");

				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					@SuppressWarnings("rawtypes")
		        	Class[] paramTypes= new Class[2];
		        	paramTypes[0]= long.class;
		        	paramTypes[1]= int.class;          
	
		        	@SuppressWarnings("unchecked")
		        	Method method = iBatteryStatsUid.getMethod("getFullWifiLockTime", paramTypes);
	
		        	//Parameters
		        	Object[] params= new Object[2];
		        	params[0]= new Long(batteryRealtime);
		        	params[1]= new Integer(iStatsType);
	
		        	ret += (Long) method.invoke(myUid, params);
	    	
		        }
	        }
	        catch( IllegalArgumentException e )
	        {
	            throw e;
	        }
	        catch( Exception e )
	        {
	            ret = new Long(0);
	        }
		}
        return ret;
	}

	/**
     * Returns the total, last, or current wifi scanning time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getScanWifiLockTime(Context context, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

    	this.collectUidStats();
		if (m_uidStats != null)
		{
	        try
	        {
				
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");

				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					@SuppressWarnings("rawtypes")
		        	Class[] paramTypes= new Class[2];
		        	paramTypes[0]= long.class;
		        	paramTypes[1]= int.class;          
	
		        	@SuppressWarnings("unchecked")
		        	Method method = iBatteryStatsUid.getMethod("getScanWifiLockTime", paramTypes);
	
		        	//Parameters
		        	Object[] params= new Object[2];
		        	params[0]= new Long(batteryRealtime);
		        	params[1]= new Integer(iStatsType);
	
		        	ret += (Long) method.invoke(myUid, params);
	    	
		        }
	        }
	        catch( IllegalArgumentException e )
	        {
	            throw e;
	        }
	        catch( Exception e )
	        {
	            ret = new Long(0);
	        }
		}
        return ret;
	}

	/**
     * Returns the total, last, or current wifi multicast time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getWifiMulticastTime(Context context, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

    	this.collectUidStats();
		if (m_uidStats != null)
		{
	        try
	        {
				
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");

				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					@SuppressWarnings("rawtypes")
		        	Class[] paramTypes= new Class[2];
		        	paramTypes[0]= long.class;
		        	paramTypes[1]= int.class;          
	
		        	@SuppressWarnings("unchecked")
		        	Method method = iBatteryStatsUid.getMethod("getWifiMulticastTime", paramTypes);
	
		        	//Parameters
		        	Object[] params= new Object[2];
		        	params[0]= new Long(batteryRealtime);
		        	params[1]= new Integer(iStatsType);
	
		        	ret += (Long) method.invoke(myUid, params);
	    	
		        }
	        }
	        catch( IllegalArgumentException e )
	        {
	            throw e;
	        }
	        catch( Exception e )
	        {
	            ret = new Long(0);
	        }
		}
        return ret;
	}

	/**
     * Returns the time in microseconds the phone has been running with the given data connection type.
     *
     * @params dataType the given data connection type (@see http://www.netmite.com/android/mydroid/donut/frameworks/base/core/java/android/os/BatteryStats.java)
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getPhoneDataConnectionTime(int dataType, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[3];
          paramTypes[0]= int.class;
          paramTypes[1]= long.class;
          paramTypes[2]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getPhoneDataConnectionTime", paramTypes);

          //Parameters
          Object[] params= new Object[3];
          params[0]= new Integer(dataType);
          params[1]= new Long(batteryRealtime);
          params[2]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);
          Log.i("TAG", "getPhoneDataConnectionTime with params " + params[0] + ", " + params[1] + "and " + params[2] + " returned " + ret);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

	/**
     * Returns the time in microseconds the phone has been running with the given signal strength.
     *
     * @params signalStrength the given data connection type (@see http://www.netmite.com/android/mydroid/donut/frameworks/base/core/java/android/os/BatteryStats.java)
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getPhoneSignalStrengthTime(int signalStrength, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[3];
          paramTypes[0]= int.class;
          paramTypes[1]= long.class;
          paramTypes[2]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getPhoneDataConnectionTime", paramTypes);

          //Parameters
          Object[] params= new Object[3];
          params[0]= new Integer(signalStrength);
          params[1]= new Long(batteryRealtime);
          params[2]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);
          Log.i("TAG", "getPhoneSignalStrengthTime with params " + params[0] + ", " + params[1] + "and " + params[2] + " returned " + ret);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

    /**
     * Returns the total, last, or current audio on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getAudioTurnedOnTime(Context context, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

    	this.collectUidStats();
		if (m_uidStats != null)
		{
	        try
	        {
				
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");

				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					@SuppressWarnings("rawtypes")
		        	Class[] paramTypes= new Class[2];
		        	paramTypes[0]= long.class;
		        	paramTypes[1]= int.class;          
	
		        	@SuppressWarnings("unchecked")
		        	Method method = iBatteryStatsUid.getMethod("getAudioTurnedOnTime", paramTypes);
	
		        	//Parameters
		        	Object[] params= new Object[2];
		        	params[0]= new Long(batteryRealtime);
		        	params[1]= new Integer(iStatsType);
	
		        	ret += (Long) method.invoke(myUid, params);
	    	
		        }
	        }
	        catch( IllegalArgumentException e )
	        {
	            throw e;
	        }
	        catch( Exception e )
	        {
	            ret = new Long(0);
	        }
		}
        return ret;
	}

	/**
     * Returns the total, last, or current video on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getVideoTurnedOnTime(Context context, long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

    	this.collectUidStats();
		if (m_uidStats != null)
		{
	        try
	        {
				
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");

				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					@SuppressWarnings("rawtypes")
		        	Class[] paramTypes= new Class[2];
		        	paramTypes[0]= long.class;
		        	paramTypes[1]= int.class;          
	
		        	@SuppressWarnings("unchecked")
		        	Method method = iBatteryStatsUid.getMethod("getVideoTurnedTime", paramTypes);
	
		        	//Parameters
		        	Object[] params= new Object[2];
		        	params[0]= new Long(batteryRealtime);
		        	params[1]= new Integer(iStatsType);
	
		        	ret += (Long) method.invoke(myUid, params);
	    	
		        }
	        }
	        catch( IllegalArgumentException e )
	        {
	            throw e;
	        }
	        catch( Exception e )
	        {
	            ret = new Long(0);
	        }
		}
        return ret;
	}

    /**
     * Returns the total, last, or current bluetooth on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getBluetoothOnTime(long batteryRealtime, int iStatsType)
	{
    	Long ret = new Long(0);

        try
        {
          //Parameters Types
          @SuppressWarnings("rawtypes")
          Class[] paramTypes= new Class[2];
          paramTypes[0]= long.class;
          paramTypes[1]= int.class;          

          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("getBluetoothOnTime", paramTypes);

          //Parameters
          Object[] params= new Object[2];
          params[0]= new Long(batteryRealtime);
          params[1]= new Integer(iStatsType);

          ret= (Long) method.invoke(m_Instance, params);

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = new Long(0);
        }

        return ret;

	
	}

    /**
     * Return whether we are currently running on battery.
     */	    
	public boolean getIsOnBattery(Context context)
	{
    	boolean ret = false;

        try
        { 
	    	@SuppressWarnings("unchecked")	
	    	Method method = m_ClassDefinition.getMethod("getIsOnBattery");

        	Boolean oRet = (Boolean) method.invoke(m_Instance);
        	ret = oRet.booleanValue();

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = false;
        }    
        
        return ret;
	}
    
    /**
     * Returns the current battery percentage level if we are in a discharge cycle, otherwise
     * returns the level at the last plug event.
     */
    public int getDischargeCurrentLevel()
    {
    	int ret = 0;

        try
        {
        	@SuppressWarnings("unchecked")
        	Method method = m_ClassDefinition.getMethod("getDischargeCurrentLevel");

        	Integer oRet = (Integer) method.invoke(m_Instance);
        	ret = oRet.intValue();

        }
        catch( IllegalArgumentException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            ret = 0;
        }    
        
        return ret;
    }
	
	/**
     * Initalizes the collection of history items
     */
    public boolean startIteratingHistoryLocked()
	{
    	Boolean ret = false;

        try
        {
          @SuppressWarnings("unchecked")
		  Method method = m_ClassDefinition.getMethod("startIteratingHistoryLocked");

          ret= (Boolean) method.invoke(m_Instance);

        }
        catch( IllegalArgumentException e )
        {
        	Log.e("TAG", "An exception occured in startIteratingHistoryLocked(). Message: " + e.getMessage() + ", cause: " + e.getCause().getMessage());
            throw e;
        }
        catch( Exception e )
        {
            ret = false;
        }

        return ret;

	
	}
    
	/**
	 * Collect the UidStats using reflection and store them  
	 */
    @SuppressWarnings("unchecked")
	private void collectUidStats()
    {
        try
        {
        	Method method = m_ClassDefinition.getMethod("getUidStats");
        	
        	m_uidStats = (SparseArray<? extends Object>) method.invoke(m_Instance);	
        	
        }
        catch( IllegalArgumentException e )
        {
        	Log.e("TAG", "An exception occured in collectUidStats(). Message: " + e.getMessage() + ", cause: " + e.getCause().getMessage());
            throw e;
        }
        catch( Exception e )
        {
        	m_uidStats = null;
        }    

    }
	
	/**
	 * Obtain the wakelock stats as a list of Wakelocks (@see com.asksven.android.common.privateapiproxies.Wakelock}
	 * @param context a Context
	 * @param iWakeType a type of wakelock @see com.asksven.android.common.privateapiproxies.BatteryStatsTypes 
	 * @param iStatType a type of stat @see com.asksven.android.common.privateapiproxies.BatteryStatsTypes
	 * @return a List of Wakelock s
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Wakelock> getWakelockStats(Context context, int iWakeType, int iStatType, int iWlPctRef) throws Exception
	{
		// type checks
		boolean validTypes = (BatteryStatsTypes.assertValidWakeType(iWakeType)
				&& BatteryStatsTypes.assertValidStatType(iStatType)
				&& BatteryStatsTypes.assertValidWakelockPctRef(iWlPctRef));
		if (!validTypes)
		{
			Log.e("TAG", "Invalid WakeType or StatType");
			throw new Exception("Invalid WakeType of StatType");
		}
		
		ArrayList<Wakelock> myStats = new ArrayList<Wakelock>();
		
		this.collectUidStats();
		if (m_uidStats != null)
		{
			long uSecBatteryTime = this.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, iStatType);
			long uSecAwakeTime = this.computeBatteryUptime(SystemClock.elapsedRealtime() * 1000, iStatType);
			long uSecScreenOnTime =this.getScreenOnTime(uSecBatteryTime, iStatType);
			 
            try
            {			
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");
				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
					// Process wake lock usage
					Method methodGetWakelockStats = iBatteryStatsUid.getMethod("getWakelockStats");

					// Map of String, BatteryStats.Uid.Wakelock
					Map<String, ? extends Object> wakelockStats = (Map<String, ? extends Object>)  methodGetWakelockStats.invoke(myUid);
					
					Method methodGetUid	= iBatteryStatsUid.getMethod("getUid");
					Integer uid 		= (Integer) methodGetUid.invoke(myUid);
					
					long wakelockTime = 0;
					int wakelockCount = 0;
							            
			        // Map of String, BatteryStats.Uid.Wakelock
		            for (Map.Entry<String, ? extends Object> wakelockEntry : wakelockStats.entrySet())
		            {
		                // BatteryStats.Uid.Wakelock
		            	Object wakelock = wakelockEntry.getValue();

		            	@SuppressWarnings("rawtypes")
						Class batteryStatsUidWakelock = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid$Wakelock");

						//Parameters Types
						@SuppressWarnings("rawtypes")
						Class[] paramTypesGetWakeTime= new Class[1];
						paramTypesGetWakeTime[0]= int.class;    

						Method methodGetWakeTime = batteryStatsUidWakelock.getMethod("getWakeTime", paramTypesGetWakeTime);
						
						
						//Parameters
						Object[] paramsGetWakeTime= new Object[1];

						// Partial wake locks BatteryStatsTypes.WAKE_TYPE_PARTIAL 
						// are the ones that should normally be of interest but
						// WAKE_TYPE_PARTIAL, WAKE_TYPE_FULL, WAKE_TYPE_WINDOW
		                // are possible
						paramsGetWakeTime[0]= new Integer(iWakeType);
						
						// BatteryStats.Timer
						Object wakeTimer = methodGetWakeTime.invoke(wakelock, paramsGetWakeTime);
						if (wakeTimer != null)
						{
			            	@SuppressWarnings("rawtypes")
							Class iBatteryStatsTimer = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Timer");

							//Parameters Types
							@SuppressWarnings("rawtypes")
							Class[] paramTypesGetTotalTimeLocked= new Class[2];
							paramTypesGetTotalTimeLocked[0]= long.class;
							paramTypesGetTotalTimeLocked[1]= int.class;    

							Method methodGetTotalTimeLocked = iBatteryStatsTimer.getMethod("getTotalTimeLocked", paramTypesGetTotalTimeLocked);
														
							//Parameters
							Object[] paramsGetTotalTimeLocked= new Object[2];
							paramsGetTotalTimeLocked[0]= new Long(uSecBatteryTime);
							paramsGetTotalTimeLocked[1]= new Integer(iStatType);
							
							Long wake = (Long) methodGetTotalTimeLocked.invoke(wakeTimer, paramsGetTotalTimeLocked);
//							Log.d(TAG, "Wakelocks inner: Process = " + wakelockEntry.getKey() + " wakelock [s] " + wake);
							wakelockTime += wake;

							//Parameters Types
							@SuppressWarnings("rawtypes")
							Class[] paramTypesGetCountLocked= new Class[1];
							paramTypesGetCountLocked[0]= int.class;    

							Method methodGetCountLocked = iBatteryStatsTimer.getMethod("getCountLocked", paramTypesGetCountLocked);
														
							//Parameters
							Object[] paramsGetCountLocked= new Object[1];
							paramsGetCountLocked[0]= new Integer(iStatType);

							Integer count = (Integer) methodGetCountLocked.invoke(wakeTimer, paramsGetCountLocked);
//							Log.d(TAG, "Wakelocks inner: Process = " + wakelockEntry.getKey() + " count " + count);
							wakelockCount += count;

		                }
						else
						{
							Log.d(TAG, "Wakelocks: Process = " + wakelockEntry.getKey() + "with no Timer spotted");
						}
						// convert so milliseconds
						wakelockTime /= 1000;
						
						long uSec = 0;
						switch (iWlPctRef)
						{
							case 0:
								uSec = uSecBatteryTime;
								break;
							case 1:
								uSec = uSecAwakeTime;
								break;
							case 2:
								uSec = (uSecAwakeTime - uSecScreenOnTime);
								break;
						}
						
						Wakelock myWl = new Wakelock(iWakeType, wakelockEntry.getKey(), wakelockTime, uSec / 1000, wakelockCount);
						
						// opt for lazy loading: do no populate UidInfo, just uid. UidInfo will be fetched on demand
						myWl.setUid(uid);
						myStats.add(myWl);

//						Log.d(TAG, "Wakelocks: Process = " + wakelockEntry.getKey() + " wakelock [s] " + wakelockTime + ", count " + wakelockCount);
		            }
		        }
            }
            catch( Exception e )
            {
            	Log.e("TAG", "An exception occured in getWakelockStats(). Message: " + e.getMessage() + ", cause: " + e.getCause().getMessage());
                throw e;
            }
		}	
		return myStats;
	}

	/**
	 * Obtain the wakelock stats as a list of Wakelocks (@see com.asksven.android.common.privateapiproxies.Wakelock}
	 * @param context a Context
	 * @param iWakeType a type of wakelock @see com.asksven.android.common.privateapiproxies.BatteryStatsTypes 
	 * @param iStatType a type of stat @see com.asksven.android.common.privateapiproxies.BatteryStatsTypes
	 * @return a List of Wakelock s
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<KernelWakelock> getKernelWakelockStats(Context context, int iStatType, int iWlPctRef, boolean bAlternate) throws Exception
	{
		// type checks
		boolean validTypes = (BatteryStatsTypes.assertValidStatType(iStatType)
				&& BatteryStatsTypes.assertValidWakelockPctRef(iWlPctRef));
		if (!validTypes)
		{
			Log.e("TAG", "Invalid WakeType or StatType");
			throw new Exception("Invalid WakeType or StatType");
		}
		
		Log.d(TAG, "getWakelockStats was called with params "
				+"[iStatType] = " + iStatType
				+ "[iWlPctRef] = " + iWlPctRef);
		
		ArrayList<KernelWakelock> myStats = new ArrayList<KernelWakelock>();
		
		long uSecBatteryTime = this.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, iStatType);
		long msSinceBoot = SystemClock.elapsedRealtime();
		 
        try
        {			
			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class iBatteryStats = cl.loadClass("com.android.internal.os.BatteryStatsImpl");

			// Process wake lock usage
			Method methodGetKernelWakelockStats = iBatteryStats.getMethod("getKernelWakelockStats");
			
			Class classSamplingTimer = cl.loadClass("com.android.internal.os.BatteryStatsImpl$SamplingTimer");

			Field currentReportedCount  		= classSamplingTimer.getDeclaredField("mCurrentReportedCount");
			Field currentReportedTotalTime  	= classSamplingTimer.getDeclaredField("mCurrentReportedTotalTime");
			Field unpluggedReportedCount  		= classSamplingTimer.getDeclaredField("mUnpluggedReportedCount");
			Field unpluggedReportedTotalTime  	= classSamplingTimer.getDeclaredField("mUnpluggedReportedTotalTime");
			Field inDischarge  					= classSamplingTimer.getDeclaredField("mInDischarge");
			Field trackingReportedValues  		= classSamplingTimer.getDeclaredField("mTrackingReportedValues");
			
			currentReportedCount.setAccessible(true);
			currentReportedTotalTime.setAccessible(true);
			unpluggedReportedCount.setAccessible(true);
			unpluggedReportedTotalTime.setAccessible(true);
			inDischarge.setAccessible(true);
			trackingReportedValues.setAccessible(true);
			
			//Parameters
			Object[] params= new Object[1];


			// Map of String, BatteryStatsImpl.SamplingTimer
			Map<String, ? extends Object> kernelWakelockStats = (Map<String, ? extends Object>)  methodGetKernelWakelockStats.invoke(m_Instance);

					            
	        // Map of String, BatteryStats.Uid.Wakelock
            for (Map.Entry<String, ? extends Object> wakelockEntry : kernelWakelockStats.entrySet())
            {
                // BatteryStats.SamplingTimer
            	String wakelockName = wakelockEntry.getKey();
            	Object samplingTimer = wakelockEntry.getValue();
            	
            	params[0]= samplingTimer;
            	
            	// read private fields
            	Integer currentReportedCountVal 	= (Integer) currentReportedCount.get(params[0]);
            	Long currentReportedTotalTimeVal 	= (Long) currentReportedTotalTime.get(params[0]);
            	
            	Integer unpluggedReportedCountVal 	= (Integer) unpluggedReportedCount.get(params[0]);
            	Long unpluggedReportedTotalTimeVal 	= (Long) unpluggedReportedTotalTime.get(params[0]);
            	
            	Boolean inDischargeVal 				= (Boolean) inDischarge.get(params[0]);
            	Boolean trackingReportedValuesVal 	= (Boolean) trackingReportedValues.get(params[0]);
            	
            	Log.d(TAG, "Kernel wakelock '" + wakelockEntry.getKey() + "'"
            			+ " : reading fields from SampleTimer: " 
            			+ " [currentReportedCountVal] = " + currentReportedCountVal
            			+ " [currentReportedTotalTimeVal] = " + currentReportedTotalTimeVal
            			+ " [unpluggedReportedCountVal] = " + unpluggedReportedCountVal
            			+ " [mUnpluggedReportedTotalTimeVal] = " + unpluggedReportedTotalTimeVal
            			+ " [mInDischarge] = " + inDischargeVal
            			+ " [mTrackingReportedValues] = " + trackingReportedValuesVal);
            	
//            	
//            	@SuppressWarnings("rawtypes")
//				Class batteryStatsSamplingTimerClass = cl.loadClass("com.android.internal.os.BatteryStatsImpl$SamplingTimer");

				//Parameters Types
				@SuppressWarnings("rawtypes")
				Class[] paramTypesGetTotalTimeLocked= new Class[2];
				paramTypesGetTotalTimeLocked[0]= long.class;
				paramTypesGetTotalTimeLocked[1]= int.class;

				//Parameters
				Object[] paramGetTotalTimeLocked= new Object[2];
				paramGetTotalTimeLocked[0]= new Long(uSecBatteryTime);
				paramGetTotalTimeLocked[1]= new Integer(iStatType);
				

				Method methodGetTotalTimeLocked = classSamplingTimer
						.getMethod("getTotalTimeLocked", paramTypesGetTotalTimeLocked);

				//Parameters Types
				@SuppressWarnings("rawtypes")
				Class[] paramTypesGetCountLocked= new Class[1];
				paramTypesGetCountLocked[0]= int.class;

				//Parameters
				Object[] paramGetCountLocked= new Object[1];
				paramGetCountLocked[0]= new Integer(iStatType);

				Method methodGetCountLocked = classSamplingTimer
						.getMethod("getCountLocked", paramTypesGetCountLocked);
					
				
				Long wake = (Long) methodGetTotalTimeLocked.invoke(samplingTimer, paramGetTotalTimeLocked);
				
				Integer count = (Integer) methodGetCountLocked.invoke(samplingTimer, paramGetCountLocked);
				
				Log.d(TAG, "Kernel wakelock: " + wakelockEntry.getKey() + " wakelock [s] " + wake / 1000
						+ " count " + count);

				// return the data depending on the method 
				if (!bAlternate)
				{
					KernelWakelock myWl = new KernelWakelock(wakelockEntry.getKey(), wake / 1000, uSecBatteryTime / 1000, count);
					myStats.add(myWl);	
				}
				else
				{
					KernelWakelock myWl = new KernelWakelock(
							wakelockEntry.getKey(),
							unpluggedReportedTotalTimeVal / 1000,
							msSinceBoot,
							unpluggedReportedCountVal);
					myStats.add(myWl);
				}	
            }
        }
        catch( Exception e )
        {
        	Log.e("TAG", "An exception occured in getKernelWakelockStats(). Message: " + e.getMessage() + ", cause: " + e.getCause().getMessage());
            throw e;
        }

        return myStats;
	}

	/**
	 * Obtain the process stats as a list of Processes (@see com.asksven.android.common.privateapiproxies.Process}
	 * @param context a Context
	 * @param iStatType a type of stat @see com.asksven.android.common.privateapiproxies.BatteryStatsTypes
	 * @return a List of Process es
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Process> getProcessStats(Context context, int iStatType) throws Exception
	{
		// type checks
		boolean validTypes = BatteryStatsTypes.assertValidStatType(iStatType);
		if (!validTypes)
		{
			Log.e("TAG", "Invalid WakeType or StatType");
			throw new Exception("Invalid StatType");
		}
		
		ArrayList<Process> myStats = new ArrayList<Process>();
		
		this.collectUidStats();
		if (m_uidStats != null)
		{
            try
            {			
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");
				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);
	            
	            	Method methodGetProcessStats = iBatteryStatsUid.getMethod("getProcessStats");
	            	
	            	Method methodGetUid	= iBatteryStatsUid.getMethod("getUid");
					Integer uid 		= (Integer) methodGetUid.invoke(myUid);
					
					// Map of String, BatteryStats.Uid.Proc
					Map<String, ? extends Object> processStats = (Map<String, ? extends Object>)  methodGetProcessStats.invoke(myUid);
					
					if (processStats.size() > 0)
					{
					    for (Map.Entry<String, ? extends Object> ent : processStats.entrySet())
					    {
					    	Log.d(TAG, "Process name = " + ent.getKey());
						    // Object is a BatteryStatsTypes.Uid.Proc
						    Object ps = ent.getValue();
							@SuppressWarnings("rawtypes")
							Class batteryStatsUidProc = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid$Proc");

							//Parameters Types
							@SuppressWarnings("rawtypes")
							Class[] paramTypesGetXxxTime= new Class[1];
							paramTypesGetXxxTime[0]= int.class; 
							
							Method methodGetUserTime 	= batteryStatsUidProc.getMethod("getUserTime", paramTypesGetXxxTime);
							Method methodGetSystemTime 	= batteryStatsUidProc.getMethod("getSystemTime", paramTypesGetXxxTime);
							Method methodGetStarts 		= batteryStatsUidProc.getMethod("getStarts", paramTypesGetXxxTime);
	
							//Parameters
							Object[] paramsGetXxxTime= new Object[1];
							paramsGetXxxTime[0]= new Integer(iStatType);
							
							Long userTime = (Long) methodGetUserTime.invoke(ps, paramsGetXxxTime);
							Long systemTime = (Long) methodGetSystemTime.invoke(ps, paramsGetXxxTime);
							Integer starts = (Integer) methodGetStarts.invoke(ps, paramsGetXxxTime);
							
							Log.d(TAG, "UserTime = " + userTime);
							Log.d(TAG, "SystemTime = " + systemTime);
							Log.d(TAG, "Starts = " + starts);
							
							// take only the processes with CPU time
							if ((userTime + systemTime) > 1000)
							{
								Process myPs = new Process(ent.getKey(), userTime, systemTime, starts);
								// opt for lazy loading: do no populate UidInfo, just uid. UidInfo will be fetched on demand
								myPs.setUid(uid);
								// try resolving names
								String myName = m_nameResolver.getLabel(context, ent.getKey());
								
								myStats.add(myPs);
							}
							else
							{
								Log.d(TAG, "Process " + ent.getKey() + " was discarded (CPU time =0)");
							}
					    }		
		            }
		        }
            }
            catch( Exception e )
            {
            	Log.e("TAG", "An exception occured in getProcessStats(). Message: " + e.getMessage() + ", cause: " + e.getCause().getMessage());
                throw e;
            }
		}	
		return myStats;
	}

	/**
	 * Obtain the network usage stats as a list of NetworkUsages (@see com.asksven.android.common.privateapiproxies.NetworkUsage}
	 * @param context a Context
	 * @param iStatType a type of stat @see com.asksven.android.common.privateapiproxies.BatteryStatsTypes
	 * @return a List of NetworkUsage s
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<NetworkUsage> getNetworkUsageStats(Context context, int iStatType) throws Exception
	{
		// type checks
		boolean validTypes = BatteryStatsTypes.assertValidStatType(iStatType);
		if (!validTypes)
		{
			Log.e("TAG", "Invalid WakeType or StatType");
			throw new Exception("Invalid StatType");
		}
		
		ArrayList<NetworkUsage> myStats = new ArrayList<NetworkUsage>();
		
		this.collectUidStats();
		if (m_uidStats != null)
		{
            try
            {			
				ClassLoader cl = context.getClassLoader();
				@SuppressWarnings("rawtypes")
				Class iBatteryStatsUid = cl.loadClass("com.android.internal.os.BatteryStatsImpl$Uid");
				int NU = m_uidStats.size();
		        for (int iu = 0; iu < NU; iu++)
		        {
		        	// Object is an instance of BatteryStats.Uid
		            Object myUid = m_uidStats.valueAt(iu);

					//Parameters Types
					@SuppressWarnings("rawtypes")
					Class[] paramTypesGetTcpBytesXxx= new Class[1];
					paramTypesGetTcpBytesXxx[0]= int.class; 

	            	Method methodGetTcpBytesReceived 	= iBatteryStatsUid.getMethod("getTcpBytesReceived", paramTypesGetTcpBytesXxx);
	            	Method methodGetTcpBytesSent 		= iBatteryStatsUid.getMethod("getTcpBytesSent", paramTypesGetTcpBytesXxx);

					//Parameters
					Object[] paramGetTcpBytesXxx = new Object[1];
					paramGetTcpBytesXxx[0]= new Integer(iStatType);
					
					Long tcpBytesReceived 	= (Long) methodGetTcpBytesReceived.invoke(myUid, paramGetTcpBytesXxx);
					Long tcpBytesSent		= (Long) methodGetTcpBytesSent.invoke(myUid, paramGetTcpBytesXxx);

					Method methodGetUid	= iBatteryStatsUid.getMethod("getUid");
					Integer uid 		= (Integer) methodGetUid.invoke(myUid);
					
					Log.d(TAG, "Uid = " + uid + ": received:" + tcpBytesReceived + ", sent: " + tcpBytesSent);

					NetworkUsage myData = new NetworkUsage(uid, tcpBytesReceived, tcpBytesSent);
					// try resolving names
					UidInfo myInfo = m_nameResolver.getNameForUid(context, uid);
					myData.setUidInfo(myInfo);
					myStats.add(myData);
		        }
            }
            catch( Exception e )
            {
                throw e;
            }
		}	
		return myStats;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<HistoryItem> getHistory(Context context) throws Exception
	{
		
		ArrayList<HistoryItem> myStats = new ArrayList<HistoryItem>();
	        	 
        try
        {			
			ClassLoader cl = context.getClassLoader();
			@SuppressWarnings("rawtypes")
			Class classHistoryItem = cl.loadClass("android.os.BatteryStats$HistoryItem");
												   
			
			// get constructor
			Constructor cctor = classHistoryItem.getConstructor();
			
			Object myHistoryItem = cctor.newInstance();

			// prepare the method call for getNextHistoryItem
			//Parameters Types
			@SuppressWarnings("rawtypes")
			Class[] paramTypes= new Class[1];
			paramTypes[0]= classHistoryItem;


			@SuppressWarnings("unchecked")
			Method methodNext = m_ClassDefinition.getMethod("getNextHistoryLocked", paramTypes);

			//Parameters
			Object[] params= new Object[1];

			// initalize hist and iterate like this
			// if (stats.startIteratingHistoryLocked()) {
            // final HistoryItem rec = new HistoryItem();
            // while (stats.getNextHistoryLocked(rec)) {
			
			// read the time of query for history
	        Long statTimeRef = Long.valueOf(this.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000,
	                BatteryStatsTypes.STATS_SINCE_CHARGED));
	        statTimeRef = System.currentTimeMillis(); 
	        
	        Log.d(TAG, "Reference time (" + statTimeRef + ": " + DateUtils.format(DateUtils.DATE_FORMAT_NOW, statTimeRef));
	        // statTimeLast stores the timestamp of the last sample
	        Long statTimeLast = Long.valueOf(0);
	        
			if (this.startIteratingHistoryLocked())
			{
				params[0]= myHistoryItem;
				Boolean bNext = (Boolean) methodNext.invoke(m_Instance, params);
				while (bNext)
				{
					// process stats: create HistoryItems from params
					Field timeField 				= classHistoryItem.getField("time"); 			// long
					
					
					Field cmdField 					= classHistoryItem.getField("cmd"); 			// byte
					Byte cmdValue = (Byte) cmdField.get(params[0]);
					
					// process only valid items
					byte updateCmd = 0;
					
					// ICS has a different implementation of HistoryItems constants
					if (AndroidVersion.isIcs())
					{
						updateCmd = HistoryItemIcs.CMD_UPDATE;
					}
					else
					{
						updateCmd = HistoryItem.CMD_UPDATE;
					}
					
					if (cmdValue == updateCmd)
					{
				        Field batteryLevelField 		= classHistoryItem.getField("batteryLevel"); 	// byte
				        Field batteryStatusField 		= classHistoryItem.getField("batteryStatus"); 	// byte
				        Field batteryHealthField 		= classHistoryItem.getField("batteryHealth"); 	// byte
				        Field batteryPlugTypeField 		= classHistoryItem.getField("batteryPlugType"); // byte
				        
				        Field batteryTemperatureField 	= classHistoryItem.getField("batteryTemperature"); // char
				        Field batteryVoltageField 		= classHistoryItem.getField("batteryVoltage"); 	// char
				        
				        Field statesField 				= classHistoryItem.getField("states"); 			// int
				        
				        // retrieve all values
				        @SuppressWarnings("rawtypes")
				        Long timeValue = (Long) timeField.get(params[0]);
				        
				        // store values only once
				        if (!statTimeLast.equals(timeValue))
				        {
					        Byte batteryLevelValue = (Byte) batteryLevelField.get(params[0]);
					        Byte batteryStatusValue = (Byte) batteryStatusField.get(params[0]);
					        Byte batteryHealthValue = (Byte) batteryHealthField.get(params[0]);
					        Byte batteryPlugTypeValue = (Byte) batteryPlugTypeField.get(params[0]);
					        
					        String batteryTemperatureValue = String.valueOf(batteryTemperatureField.get(params[0]));
					        String batteryVoltageValue = String.valueOf(batteryVoltageField.get(params[0]));
					        
					        Integer statesValue = (Integer) statesField.get(params[0]);

					        HistoryItem myItem = null;
					        
					        // ICS has a different implementation of HistoryItems constants
							if (AndroidVersion.isIcs())
							{
								myItem = new HistoryItemIcs(timeValue, cmdValue, batteryLevelValue,
						        		batteryStatusValue, batteryHealthValue, batteryPlugTypeValue,
						        		batteryTemperatureValue, batteryVoltageValue, statesValue);
							}
							else
							{
								myItem = new HistoryItem(timeValue, cmdValue, batteryLevelValue,
						        		batteryStatusValue, batteryHealthValue, batteryPlugTypeValue,
						        		batteryTemperatureValue, batteryVoltageValue, statesValue);
							}
							
					        myStats.add(myItem);
					        Log.d(TAG, "Added HistoryItem " + myItem.toString());

				        }
					    // overwrite the time of the last sample
				        statTimeLast = timeValue;

					}
					else
					{
						Log.d(TAG, "Skipped item");
					}
					
					bNext = (Boolean) methodNext.invoke(m_Instance, params);
				}
				
				// norm the time of each sample
				// stat time last is the number of millis since
				// the stats is being collected
				// the ref time is a full plain time (with date)
				Long offset = statTimeRef - statTimeLast;
				Log.d(TAG, "Reference time (" + statTimeRef + ")" + DateUtils.format(DateUtils.DATE_FORMAT_NOW, statTimeRef));
				
				Log.d(TAG, "Last sample (" + statTimeLast + ")" + DateUtils.format(DateUtils.DATE_FORMAT_NOW, statTimeLast));
				
				Log.d(TAG, "Correcting all HistoryItem times by an offset of (" + offset + ")" + DateUtils.formatDuration(offset * 1000));
				for (int i=0; i < myStats.size(); i++)
				{
					myStats.get(i).setOffset(offset);
				}
			}
        }
        catch( Exception e )
        {
        	Log.e("TAG", "An exception occured in getHistory(). Message: " + e.getMessage() + ", cause: " + e.getCause().getMessage());
            throw e;
        }
			
		return myStats;
	}

}