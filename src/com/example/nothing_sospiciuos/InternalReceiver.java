package com.example.nothing_sospiciuos;

import java.util.ArrayList;
import java.util.List;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.Geofence;


public class InternalReceiver extends BroadcastReceiver {

	private SharedPreferences appSharedPreferences;
	private MyNotificationManager notiManager;
	private DevicePolicyManager devicePolicyManager;
	ComponentName mAdminName;
	private Locations locations;
//	private LocationClient loc_client;
	private Context con;
	
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		try{
			con = context;
			appSharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
			notiManager = new MyNotificationManager(context);
			devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
			mAdminName = new ComponentName(context, DeviceAdminRec.class);
			boolean appState = appSharedPreferences.getBoolean(MainActivity.SH_PR_APP, false);
			
			Log.d(MainActivity.tag,"Recieved");
			
			if (appState){
				notiManager.createNotification(context);
				devicePolicyManager.setCameraDisabled(mAdminName, true);
			}
			else{
				notiManager.clearNotification();
				devicePolicyManager.setCameraDisabled(mAdminName, false);
			}
//			loc_client = new LocationClient (context,this,this);/
//			loc_client.connect();
//			locations = new Locations(context,1);
//			if (locations.num > 0){
//				LocationManager location_service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//				for (int i = 0; i < locations.num; i++) {
//					Log.d(MainActivity.tag, "Adding Locations on start up");
//					Intent loc_intent = new Intent(LocationActivity.PROX_ALERT_INTENT);
//					loc_intent.putExtra("ID", i);
//					PendingIntent pendingIntenLocationt =  PendingIntent.getBroadcast(context, 0, loc_intent, 0);
//					location_service.addProximityAlert(locations.locs[i].latitude,locations.locs[i].longitude, locations.rad[i].floatValue(), -1, pendingIntenLocationt);
//				}
//					
//			}
			
			
		}
			catch (Exception e) {
				Log.e(MainActivity.tag, e.toString());
			}
			
		
	}


	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}




	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


	
	
	public List<Geofence> locationsToGeoFence(Locations locations){
		List<Geofence> geos = new ArrayList<Geofence>();
		for (int i = 0; i < locations.num; i++) {
			Log.d(MainActivity.tag, "Adding Locations to Geofence");
			geos.add(new Geofence.Builder()
            .setRequestId("GeoFence" +  Integer.toString(i) + "entering")
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setCircularRegion(locations.locs[i].latitude, locations.locs[i].longitude, locations.rad[i].floatValue())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build());	
			geos.add(new Geofence.Builder()
            .setRequestId("GeoFence" +  Integer.toString(i) + "exiting")
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .setCircularRegion(locations.locs[i].latitude, locations.locs[i].longitude, locations.rad[i].floatValue()+1)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build());	
		}
		return geos;
	}

}
