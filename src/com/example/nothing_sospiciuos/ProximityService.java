package com.example.nothing_sospiciuos;

import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ProximityService extends Service {

	private Locations locations;
	private Context context;
	private ProximityIntentReceiver mybroadcast;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
	    context = getApplicationContext();
	    mybroadcast = new ProximityIntentReceiver();
	    IntentFilter filter = new IntentFilter(LocationActivity.PROX_ALERT_INTENT);
	    registerReceiver(mybroadcast, filter );
	    locations = new Locations(context,1);
	    if (locations.num > 0){
			LocationManager location_service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			for (int i = 0; i < locations.num; i++) {
				Log.d(MainActivity.tag, "Adding Locations");
				Intent loc_intent = new Intent(this,ProximityIntentReceiver.class);
				loc_intent.setAction(LocationActivity.PROX_ALERT_INTENT);
				loc_intent.putExtra("ID", i);
				PendingIntent pendingIntenLocationt =  PendingIntent.getBroadcast(context, 0, loc_intent, 0);
				location_service.addProximityAlert(locations.locs[i].latitude,locations.locs[i].longitude, locations.rad[i].floatValue(), -1, pendingIntenLocationt);
			}
				
		} else{
			stopSelf();
		}
	}
	
	@Override
	public void onDestroy() {
	    Toast.makeText(this, "Proximity Service Stopped", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
	    Toast.makeText(this, "Proximity Service Started", Toast.LENGTH_LONG).show();
	    //IntentFilter filter = new IntentFilter(proximitys);
	    //registerReceiver(mybroadcast,filter);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(MainActivity.tag, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
	
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

}
