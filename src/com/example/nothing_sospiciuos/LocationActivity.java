package com.example.nothing_sospiciuos;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback, OnMapLongClickListener{

	private Context context;
	public static final String PROX_ALERT_INTENT = "PROXIMITY_ALERT";
	private GoogleMap map;
	LocationManager location_service;
	private LatLng clicked_Location;
	private Double clicked_Radius;
	private Circle circle;
	private Circle[] circles = new  Circle[10];
	private Locations locations;
	//private LocationClient loc_client;
	private ArrayList<Geofence> geos = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) throws SQLException {	
		super.onCreate(savedInstanceState);
		context = getApplicationContext() ;
		try{
			locations = new Locations(context, 1);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			setContentView(R.layout.map_layout);
			boolean isPlay = servicesConnected();
			if (isPlay)
			{
				location_service = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
/*				loc_client = new LocationClient (this,this,this);
				loc_client.connect();*/
				
			    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				        .getMap();
		    	map.setOnMapLongClickListener(this);
		    	drawCircles();
			}
    		
	    	
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
				geos = new ArrayList<Geofence>();
				geos.add(new Geofence.Builder()
	            .setRequestId("GeoFence" +  Integer.toString(id) + "entering")
	            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
	            .setCircularRegion(clicked_Location.latitude, clicked_Location.longitude, clicked_Radius.floatValue())
	            .setExpirationDuration(Geofence.NEVER_EXPIRE)
	            .build());	
				geos.add(new Geofence.Builder()
	            .setRequestId("GeoFence" +  Integer.toString(id) + "exiting")
	            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
	            .setCircularRegion(clicked_Location.latitude, clicked_Location.longitude, clicked_Radius.floatValue())
	            .setExpirationDuration(Geofence.NEVER_EXPIRE)
	            .build());	
				Intent loc_intent = new Intent(context,ReceiveTransitionsIntentService.class);
				PendingIntent pendingIntenLocationt =  PendingIntent.getService(context, 0, loc_intent, PendingIntent.FLAG_UPDATE_CURRENT);
/*				if (loc_client.isConnected()){
					loc_client.addGeofences(geos, pendingIntenLocationt, this);
					geos = null;
				}
				else{
					loc_client = new LocationClient (this,this,this);
					loc_client.connect();
				}*/
				drawCircles();
				circle.remove();
				circle = null;
				clicked_Location = null;
			}
		}else{
			Toast.makeText(context, "Please enter location", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void deleteLocation(View view){
		for (int i = 0; i < locations.num; i++) {
			circles[i].remove();
			Intent loc_intent = new Intent(PROX_ALERT_INTENT);
			loc_intent.putExtra("ID", i);
			PendingIntent pendingIntenLocationt =  PendingIntent.getBroadcast(getApplicationContext(), 0, loc_intent, 0);
			location_service.removeProximityAlert(pendingIntenLocationt);
			
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

	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		if (circle != null){
			circle.setStrokeColor(Color.GREEN);
		}
		
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
		// TODO Auto-generated method stub
		
	}

}
