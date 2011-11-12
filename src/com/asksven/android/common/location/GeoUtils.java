/**
 * 
 */
package com.asksven.android.common.location;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

/**
 * @author sven
 *
 */
public class GeoUtils
{
	private static final String TAG = "GeoUtils";

	public static String getNearestCity(Context ctx, Location loc)
	{
		Address address = getGeoData(ctx, loc);
		String strRet = "";
		if (address != null)
		{	
			strRet = address.getLocality();
	    }
	    return strRet;
	}

	public static Address getGeoData(Context ctx, Location loc)
	{
		Geocoder myGeocoder = new Geocoder(ctx);
		Address address = null;
		try
		{
			List<Address> list = myGeocoder.getFromLocation(
					loc.getLatitude(),
					loc.getLongitude(), 1);
	        if (list != null & list.size() > 0)
	        {
	            address = list.get(0);
	        }
		}
		catch (Exception e)
		{
			Log.e(TAG, "Failed while retrieving nearest city");
		}
	    return address;
	}
}
