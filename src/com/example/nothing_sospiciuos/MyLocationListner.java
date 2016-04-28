package com.example.nothing_sospiciuos;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListner implements LocationListener  {
	public void onLocationChanged(Location location) {
		Log.d(MainActivity.tag,"Location listner");
//		appSharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
//		devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//		mAdminName = new ComponentName(context, DeviceAdminRec.class);
//		boolean appState = appSharedPreferences.getBoolean(MainActivity.SH_PR_APP, false);
//		Log.i(MainActivity.tag,"On location changed");
//		float[] results = null; 
//		if (locationIn){
//				
//				Location.distanceBetween(location.getLatitude(), location.getLongitude(), locations.locs[locationInNum].latitude,
//						locations.locs[locationInNum].longitude, results);
//				if (results[0] > locations.rad[locationInNum]){
//					locationIn = false;
//					locationInNum = (Integer) null;
////					turnOff();
//				}
//		}else{
//			for (int i = 0; i < locations.num; i++) {
//				Location.distanceBetween(location.getLatitude(), location.getLongitude(), locations.locs[i].latitude,
//						locations.locs[i].longitude, results);
//				if (results[0] < locations.rad[i]){
//					locationIn = true;
//					locationInNum = i;
////					turnOn();
//				}
//			}
//			
//		}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
