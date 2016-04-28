package com.example.nothing_sospiciuos;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.common.*;


import android.app.IntentService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ReceiveTransitionsIntentService extends IntentService {

	private SharedPreferences appSharedPreferences;
	private MyNotificationManager notiManager;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName mAdminName;
	private static final String TAG = "Service";
	
	public ReceiveTransitionsIntentService() {
		super("ReceiveTransitionsIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Context context = getApplicationContext();
		appSharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		notiManager = new MyNotificationManager(context);
		devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(context, DeviceAdminRec.class);
		boolean appState = appSharedPreferences.getBoolean(MainActivity.SH_PR_APP, false);
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            int errorMessage = geofencingEvent.getErrorCode();
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        
		Log.d(MainActivity.tag, TAG + " transition type:" + Integer.toString(geofenceTransition));
	 	Log.d(MainActivity.tag, TAG +" transition type:" + Boolean.toString(appState));
	 	if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER && !appState)
	 	{
			notiManager.createNotification(context);
			devicePolicyManager.setCameraDisabled(mAdminName, true);
			SharedPreferences.Editor editor = appSharedPreferences.edit();
			editor.putBoolean(MainActivity.SH_PR_APP, true);
			editor.commit();
			Log.d(MainActivity.tag, "Entering");
		} 
	 	else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT && appState)
	 	{
			notiManager.clearNotification();
        	devicePolicyManager.setCameraDisabled(mAdminName, false);
        	SharedPreferences.Editor editor = appSharedPreferences.edit();
			editor.putBoolean(MainActivity.SH_PR_APP, false);
			editor.commit();
			Log.d(MainActivity.tag, "Exiting");
		}


	}

}
