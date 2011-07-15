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


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;



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
	 * Default cctor
	 */
	public BatteryStatsProxy(Context context)
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
	     
	          IBinder serviceBinder = (IBinder) methodGetService.invoke(serviceManagerClass, paramsGetService); 

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
	          	          
	          Object iBatteryStatsInstance = methodAsInterface.invoke(iBatteryStatsStub, paramsAsInterface);
	          
	          // and finally we call getStatistics from that IBatteryStats to obtain a Parcel
	          @SuppressWarnings("rawtypes")
			  Class iBatteryStats = cl.loadClass("com.android.internal.app.IBatteryStats");
	          
	          @SuppressWarnings("unchecked")
	          Method methodGetStatistics = iBatteryStats.getMethod("getStatistics");
	          byte[] data = (byte[]) methodGetStatistics.invoke(iBatteryStatsInstance);
	          
	          Parcel parcel = Parcel.obtain();
	          parcel.unmarshall(data, 0, data.length);
	          parcel.setDataPosition(0);
	          
	          @SuppressWarnings("rawtypes")
			  Class batteryStatsImpl = cl.loadClass("com.android.internal.os.BatteryStatsImpl");
	          Field creatorField = batteryStatsImpl.getField("CREATOR");
	          
	          // From here on we don't need reflection anymore
	          @SuppressWarnings("rawtypes")
			  Parcelable.Creator batteryStatsImpl_CREATOR = (Parcelable.Creator) creatorField.get(batteryStatsImpl); 
	          
	          m_Instance = batteryStatsImpl_CREATOR.createFromParcel(parcel);        
	    }
		catch( IllegalArgumentException e )
		{
	        m_Instance = null;
	    }
		catch (ClassNotFoundException e)
		{
	    	m_Instance = null;
	    }
		catch( Exception e )
		{
	    	m_Instance = null;
	    }    
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
	public List<Wakelock> getWakelockStats(Context context, int iWakeType, int iStatType) throws Exception
	{
		// type checks
		boolean validTypes = (BatteryStatsTypes.assertValidWakeType(iWakeType)
				&& BatteryStatsTypes.assertValidStatType(iStatType));
		if (!validTypes)
		{
			throw new Exception("Invalid WakeType of StatType");
		}
		
		List<Wakelock> myStats = new Vector<Wakelock>();
		
		this.collectUidStats();
		if (m_uidStats != null)
		{
			long uSecTime = this.computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, iStatType);
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
					
					long wakelockTime = 0;
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
						paramsGetWakeTime[0]= new Integer(BatteryStatsTypes.WAKE_TYPE_PARTIAL);
						
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
							paramsGetTotalTimeLocked[0]= new Long(uSecTime);
							paramsGetTotalTimeLocked[1]= new Integer(BatteryStatsTypes.STATS_SINCE_CHARGED);
							
							Long wake = (Long) methodGetTotalTimeLocked.invoke(wakeTimer, paramsGetTotalTimeLocked);
							Log.d(TAG, "Wakelocks inner: Process = " + wakelockEntry.getKey() + " wakelock [s] " + wake);
							wakelockTime += wake;
		                }
						// convert so milliseconds
						wakelockTime /= 1000;
						
						Wakelock wl = new Wakelock(iWakeType, wakelockEntry.getKey(), wakelockTime);
						myStats.add(wl);
						
						Log.d(TAG, "Wakelocks: Process = " + wakelockEntry.getKey() + " wakelock [s] " + wakelockTime);
		            }
		        }
            }
            catch( Exception e )
            {
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
	public List<Process> getProcessStats(Context context, int iStatType) throws Exception
	{
		// type checks
		boolean validTypes = BatteryStatsTypes.assertValidStatType(iStatType);
		if (!validTypes)
		{
			throw new Exception("Invalid StatType");
		}
		
		List<Process> myStats = new Vector<Process>();
		
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
							
							Method methodGetUserTime = batteryStatsUidProc.getMethod("getUserTime", paramTypesGetXxxTime);
							Method methodGetSystemTime = batteryStatsUidProc.getMethod("getSystemTime", paramTypesGetXxxTime);
	
							//Parameters
							Object[] paramsGetXxxTime= new Object[1];
							paramsGetXxxTime[0]= new Integer(BatteryStatsTypes.STATS_SINCE_CHARGED);
							
							Long userTime = (Long) methodGetUserTime.invoke(ps, paramsGetXxxTime);
							Long systemTime = (Long) methodGetSystemTime.invoke(ps, paramsGetXxxTime);
							
							Log.d(TAG, "UserTime = " + userTime);
							Log.d(TAG, "SystemTime = " + systemTime);
							Process myPs = new Process(ent.getKey(), userTime, systemTime);
							myStats.add(myPs);
					    }		
		            }
		        }
            }
            catch( Exception e )
            {
                throw e;
            }
		}	
		return myStats;
	}

}