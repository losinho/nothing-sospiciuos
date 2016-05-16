package com.example.nothing_sospiciuos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


public class MyNotificationManager{
	private NotificationManager mNotificationManager;
	int notificationID;
	int icon_draw;
	String version;
	String date;
	SharedPreferences appSharedPreferences;
	
	public MyNotificationManager(Context context) {
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);	
		appSharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
		setDate();
		setVersion();
		setNotificationID(0);
		
	}
	public int getNotificationID() {
		return notificationID;
	}
	public void setNotificationID(int notificationID) {
		this.notificationID = notificationID;
	}
		public String getVersion() {
		return version;
	}
	public void setVersion() {
		this.version = appSharedPreferences.getString(MainActivity.SH_PR_VER , "1.3");
	}
	public String getDate() {
		return date;
	}
	public void setDate() {
		this.date = appSharedPreferences.getString(MainActivity.SH_PR_DATE, "20-10-12");
	}
	public void clearNotification() {
		mNotificationManager.cancel(notificationID);
	}
	public void createNotification(Context context) {
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),com.example.nothing_sospiciuos.R.drawable.largeicon);// .R.drawable.largeIcon);
        Notification noti = new Notification.Builder(context)
        .setContentText("מופעל בהתאם למיקום.גרסה " + getVersion())
        .setContentTitle("מערכת לכיבוי מצלמה")
        //.setLargeIcon(largeIcon)
        .setWhen(System.currentTimeMillis())
        .build();
        noti.flags |= Notification.FLAG_NO_CLEAR;
        noti.flags |= Notification.FLAG_ONGOING_EVENT;

        Intent resultIntent = new Intent();
         // The stack builder object will contain an artificial back stack for the
	     // started Activity.
	     // This ensures that navigating backward from the Activity leads out of
	     // your application to the Home screen.
	     TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
	     // Adds the back stack for the Intent (but not the Intent itself)
	     stackBuilder.addParentStack(MainActivity.class);
	     // Adds the Intent that starts the Activity to the top of the stack
	     stackBuilder.addNextIntent(resultIntent);
	     PendingIntent resultPendingIntent =
	             stackBuilder.getPendingIntent(
	                 0,
	                 PendingIntent.FLAG_UPDATE_CURRENT
	             );
	     
	    noti.contentIntent=resultPendingIntent;//setContentIntent(resultPendingIntent);
	    noti.icon = com.example.nothing_sospiciuos.R.drawable.camera_icon;
        mNotificationManager.notify(notificationID, noti);
        Log.i(MainActivity.tag,"Notification Created");
	}
	
}
