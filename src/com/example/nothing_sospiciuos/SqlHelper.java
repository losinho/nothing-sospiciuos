package com.example.nothing_sospiciuos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "locations.db";
	public static final String COLUMN_LAT= "latitude";
    public static final String COLUMN_LONG = "longitude";
    public static final String COLUMN_RADIUS = "radius";
    public static final String TABLE_NAME = "locations";

 // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
        + TABLE_NAME + "(" + COLUMN_LAT
        + " real not null, " + COLUMN_LONG
        + " real not null, " + COLUMN_RADIUS + " real not null);";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(DATABASE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME +";");
	    onCreate(db);
	}
	
	public SqlHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

}
