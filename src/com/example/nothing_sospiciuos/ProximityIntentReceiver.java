package com.example.nothing_sospiciuos;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {

	private SharedPreferences appSharedPreferences;
	private MyNotificationManager notiManager;
	private DevicePolicyManager devicePolicyManager;
	ComponentName mAdminName;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(MainActivity.tag, "ProximityIntentReceiver");
		Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
		appSharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		notiManager = new MyNotificationManager(context);
		devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminName = new ComponentName(context, DeviceAdminRec.class);
		boolean appState = appSharedPreferences.getBoolean(MainActivity.SH_PR_APP, false);
		if (entering && !appState){
			notiManager.createNotification(context);
			devicePolicyManager.setCameraDisabled(mAdminName, true);
			SharedPreferences.Editor editor = appSharedPreferences.edit();
			editor.putBoolean(MainActivity.SH_PR_APP, true);
			editor.commit();
			Log.d(MainActivity.tag, "Entering");
		} else if (!entering && appState){
			notiManager.clearNotification();
        	devicePolicyManager.setCameraDisabled(mAdminName, false);
        	SharedPreferences.Editor editor = appSharedPreferences.edit();
			editor.putBoolean(MainActivity.SH_PR_APP, false);
			editor.commit();
			Log.d(MainActivity.tag, "Exiting");
		}

	}

}
