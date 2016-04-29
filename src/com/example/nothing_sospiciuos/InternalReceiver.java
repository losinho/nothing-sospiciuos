package com.example.nothing_sospiciuos;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;


public class InternalReceiver extends BroadcastReceiver implements 
ConnectionCallbacks,OnConnectionFailedListener,ResultCallback<Result> {

ComponentName mAdminName;
private Locations locations;
private Context con;
private GoogleApiClient mGoogleApiClient;
	
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d(MainActivity.tag,"Recieved on boot completed");
		try{
			con = context;
			Log.d(MainActivity.tag,"before Google API client building");
			if ((mGoogleApiClient == null) && servicesConnected()) {
			    mGoogleApiClient = new GoogleApiClient.Builder(con)
			        .addConnectionCallbacks(this)
			        .addOnConnectionFailedListener(this)
			        .addApi(LocationServices.API)
			        .build();
			    Log.d(MainActivity.tag,"Google API client builded");
			}
			locations = new Locations(context,1);
			mGoogleApiClient.connect();
			
			Log.d(MainActivity.tag,"End of internal reciever");
			}
			catch (Exception e) {
				Log.e(MainActivity.tag, e.toString());
			}
			
		
	}


	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}




	public void onDisconnected() {
		Log.d(MainActivity.tag,"Google API disconnect");
		
	}

	public List<Geofence> locationsToGeoFence(Locations locations){
		List<Geofence> geos = new ArrayList<Geofence>();
		for (int i = 0; i < locations.num; i++) {
			Log.d(MainActivity.tag, "Adding Locations to Geofence");
			geos.add(new Geofence.Builder()
            .setRequestId("GeoFence" +  Integer.toString(i) + "entering")
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER 
            		| Geofence.GEOFENCE_TRANSITION_EXIT)
            .setCircularRegion(locations.locs[i].latitude, locations.locs[i].longitude, locations.rad[i].floatValue())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build());	
		}
		return geos;
	}


	public void onResult(Result result) {
		Log.d(MainActivity.tag, result.toString());
		
	}


	public void onConnected(Bundle connectionHint) {
		Log.d(MainActivity.tag, "Google api conected");
		if (locations.num > 0){
			Log.d(MainActivity.tag,"Adding Geofences");
			List<Geofence> geos = locationsToGeoFence(locations);
			LocationServices.GeofencingApi.addGeofences(
	                mGoogleApiClient,
	                getGeofencingRequest(geos),
	                getPendingIntent()
	        ).setResultCallback(this);
		}
		mGoogleApiClient.disconnect();
		

		
	}


	public void onConnectionSuspended(int cause) {
		Log.d(MainActivity.tag, "Google api conected" + Integer.toString(cause));
		
	}
	private boolean servicesConnected() {
        // Check that Google Play services is available
        @SuppressWarnings("deprecation")
		int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(con);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
        	Log.d("Location Updates",
                    "Google Play services is unavailable.");
        	Toast.makeText(con, "Can not connect to google play service", Toast.LENGTH_LONG).show();
            return false;
        }
    }
	
	private PendingIntent getPendingIntent()
	{
		Intent loc_intent = new Intent(con,ReceiveTransitionsIntentService.class);
		PendingIntent pendingIntenLocationt =  PendingIntent.getService(con, 0,
				loc_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntenLocationt;
	}
	
	private GeofencingRequest getGeofencingRequest(List<Geofence> geo) {
	    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
	    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
	    builder.addGeofences(geo);
	    return builder.build();
	}

}
