package com.example.nothing_sospiciuos;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends Activity implements OnMapReadyCallback, OnMapLongClickListener,
	ConnectionCallbacks,OnConnectionFailedListener,ResultCallback<Result>, LocationListener{

	private Context context;
	public static final String PROX_ALERT_INTENT = "PROXIMITY_ALERT";
	private GoogleMap map;
	LocationManager location_service;
	private LatLng clicked_Location;
	private Double clicked_Radius;
	private Circle circle;
	private Circle[] circles = new  Circle[10];
	private Locations locations;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) throws SQLException {	
		super.onCreate(savedInstanceState);
		context = getApplicationContext() ;
		try{
			locations = new Locations(context, 1);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			location_service = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			setContentView(R.layout.map_layout);
			if ((mGoogleApiClient == null) && servicesConnected()) {
			    mGoogleApiClient = new GoogleApiClient.Builder(this)
			        .addConnectionCallbacks(this)
			        .addOnConnectionFailedListener(this)
			        .addApi(LocationServices.API)
			        .build();
			}
			mLocationRequest = new LocationRequest();
	        mLocationRequest.setInterval(100000);
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

	        mLocationRequest.setFastestInterval(10);
			MapFragment mapFragment = (MapFragment) getFragmentManager()
				    .findFragmentById(R.id.map);
				mapFragment.getMapAsync(this);
	    }
		catch (Exception e) {
			Log.e(MainActivity.tag, e.toString());
			Toast.makeText(context, "Error occured", Toast.LENGTH_LONG).show();
		}
	}
	
	public void onMapLongClick(LatLng point) {
		 clicked_Location = point;	 
		 final EditText radius_edit = (EditText) findViewById(R.id.editTextRadius);
		 clicked_Radius = Double.parseDouble(radius_edit.getText().toString())*1000;
		 if (circle != null){
			 circle.remove();
		 }
		 CircleOptions circle_option = new CircleOptions()
	     .center(point)
	     .radius(Double.parseDouble(radius_edit.getText().toString())*1000)
	     .strokeColor(Color.RED)
	     .fillColor(Color.TRANSPARENT);
		 circle = map.addCircle(circle_option);
		 
		 
	}
	
	public void saveLocation(View view)
	{
		if (clicked_Location != null){
			int id = locations.saveLocation(clicked_Location, clicked_Radius,context);
			if (id < 0)
			{
				Toast.makeText(context, "Could not add location. Empty previous locations.", Toast.LENGTH_SHORT).show();
				return;
			}else{
				
				Geofence geo = new Geofence.Builder()
	            .setRequestId("GeoFence" +  Integer.toString(id))
	            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
	                    Geofence.GEOFENCE_TRANSITION_EXIT)
	            .setCircularRegion(clicked_Location.latitude, clicked_Location.longitude, clicked_Radius.floatValue())
	            .setExpirationDuration(Geofence.NEVER_EXPIRE)
	            .build();	
				LocationServices.GeofencingApi.addGeofences(
		                mGoogleApiClient,
		                getGeofencingRequest(geo),
		                getPendingIntent(id)
		        ).setResultCallback(this);
				drawCircles();
				circle.remove();
				circle = null;
				clicked_Location = null;
			}
		}else{
			Toast.makeText(context, "Please enter location", Toast.LENGTH_SHORT).show();
		}
	}
	
	private GeofencingRequest getGeofencingRequest(Geofence geo) {
		List<Geofence> geos = new ArrayList<Geofence>();
		geos.add(geo);
	    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
	    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
	    builder.addGeofences(geos);
	    return builder.build();
	}
	
	public void deleteLocation(View view){
		for (int i = 0; i < locations.num; i++) {
			circles[i].remove();
			LocationServices.GeofencingApi.removeGeofences(
		            mGoogleApiClient,
		            // This is the same pending intent that was used in addGeofences().
		            getPendingIntent(i)
		    ).setResultCallback(this); // Result processed in onResult().
		}
		
		circles =  new  Circle[10];
		
		 if (circle != null){
			 circle.remove();
		 }
		
		 locations.deleteLocations();
		 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	onBackPressed();
	    }
	    return true;
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e(MainActivity.tag, "error Connected");
		
	}

	public void onConnected(Bundle arg0) {
		Log.d(MainActivity.tag, "Connected");
		 if(servicesConnected()) {
	            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	        }
/*		Location cur_location = loc_client.getLastLocation();
		LatLng my_latlon = new LatLng(cur_location.getLatitude(), cur_location.getLongitude());
		map.addMarker(new MarkerOptions()
        .position(my_latlon));
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(my_latlon, 13));
		if (geos != null){
			Intent loc_intent = new Intent(context,ReceiveTransitionsIntentService.class);
			PendingIntent pendingIntenLocationt =  PendingIntent.getService(context, 0, loc_intent, PendingIntent.FLAG_UPDATE_CURRENT);
			loc_client.addGeofences(geos, pendingIntenLocationt, this);
			geos = null;
		}
		 */
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	
	public void drawCircles(){
		
		for (int i = 0; i < locations.num; i++) {
			if (circles[i] != null){
				circles[i].remove();
			}
			CircleOptions circle_option = new CircleOptions()
		    .center(locations.locs[i])
		    .radius(locations.rad[i])
		    .strokeColor(Color.GREEN)
		    .fillColor(Color.TRANSPARENT);
	    	circles[i] = map.addCircle(circle_option);	
		}
	}
	
	 private boolean servicesConnected() {
	        // Check that Google Play services is available
	        int resultCode =
	                GooglePlayServicesUtil.
	                        isGooglePlayServicesAvailable(context);
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
	        	Toast.makeText(context, "Can not connect to google play service", Toast.LENGTH_LONG).show();
	            return false;
	        }
	    }

	public void onMapReady(GoogleMap arg0) {
		map = arg0;
		map.setOnMapLongClickListener(this);
		drawCircles();
	}

	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	private PendingIntent getPendingIntent(int id)
	{
		Intent loc_intent = new Intent(context,ReceiveTransitionsIntentService.class);
		PendingIntent pendingIntenLocationt =  PendingIntent.getService(context, id,
				loc_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntenLocationt;
	}
	@Override
	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	@Override
	protected void onStop() {
		LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	public void onResult(Result result) {
		// TODO Auto-generated method stub
		
	}

	public void onLocationChanged(Location location) {
		Log.d("Location Update", "CHANGED");
        if (location.distanceTo(mCurrentLocation) > 500){
			mCurrentLocation = location;
	        
	        MarkerOptions options = new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(),
	        		mCurrentLocation.getLongitude()));	
	        map.addMarker(options);
	        CameraUpdate camup = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),
	        		mCurrentLocation.getLongitude()),10);
	        map.moveCamera(camup);
        }
		
	}
	
	

}
