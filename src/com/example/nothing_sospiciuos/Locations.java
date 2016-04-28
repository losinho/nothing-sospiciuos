package com.example.nothing_sospiciuos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Locations {
	public static final int MAXLOACTIONS = 10;
	public LatLng[] locs = new LatLng[MAXLOACTIONS];
	public Double[] rad = new Double[MAXLOACTIONS];
	String FILENAME = "Locations";
	public int num;
	private SQLiteDatabase database;
	  private SqlHelper dbHelper;
	  private String[] allColumns = { SqlHelper.COLUMN_LAT,
			  SqlHelper.COLUMN_LONG,SqlHelper.COLUMN_RADIUS };
	
	
	public Locations(Context context) {
		locs = new LatLng[MAXLOACTIONS];
		rad = new Double[MAXLOACTIONS];
		num = 0 ;
		dbHelper = new SqlHelper(context);
	}
	
	public Locations(Context context, int action) {
		
		locs = new LatLng[MAXLOACTIONS];
		rad = new Double[MAXLOACTIONS];
		num = 0;
		dbHelper = new SqlHelper(context);
		database = dbHelper.getReadableDatabase();
		try{
			Cursor cursor = database.query(SqlHelper.TABLE_NAME,
			        allColumns, null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
		    	double lat = cursor.getDouble(cursor.getColumnIndex(allColumns[0]));
		    	double lon = cursor.getDouble(cursor.getColumnIndex(allColumns[1]));
		    	locs[num] = new LatLng(lat, lon);
		    	rad[num] = cursor.getDouble(cursor.getColumnIndex(allColumns[2]));
	
		    	num ++;
		     	cursor.moveToNext();
			}
		}catch (Exception e) {
			Log.e(MainActivity.tag,e.toString());
		}
	}
	
	public int saveLocation(LatLng location,Double radius,Context context){
		if ( num < MAXLOACTIONS){
			database = dbHelper.getWritableDatabase();
			locs[num] = location;
			rad[num] = radius;
			num ++;
			ContentValues values = new ContentValues();
			values.put(allColumns[0], location.latitude);
			values.put(allColumns[1], location.longitude);
			values.put(allColumns[2], radius);
			database.insert(SqlHelper.TABLE_NAME, null, values);
			database.close();
			return (num-1);
		}
		else{
			return -1;
		}
	}
	
	public int deleteLocations(){
		locs = new LatLng[MAXLOACTIONS];
		rad = new Double[MAXLOACTIONS];
		num = 0 ;
		database = dbHelper.getWritableDatabase();
		database.delete(SqlHelper.TABLE_NAME, "1", null);
		database.close();
		
		return 1;
		
	}
	

}
