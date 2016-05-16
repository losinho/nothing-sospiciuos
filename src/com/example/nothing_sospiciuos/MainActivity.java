package com.example.nothing_sospiciuos;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;






public class MainActivity extends Activity{ 
	public static final String COPYRIGHT = "\u00a9";
	public static final String PREFS_NAME = "MyPrefs";
	public static final String tag = "Elta_APP";
	public static final String SH_PR_ICN_SIZE = "icn_size";
	public static final String SH_PR_IS_NOTI = "isnotification";
	public static final String SH_PR_APP_VERSION = "app_version";
	public static final String SH_PR_VER = "version";
	public static final String SH_PR_DATE = "date";
	public static final String SH_PR_APP = "isapp";
	static final int ACTIVATION_REQUEST = 47;
	private static String update;
	private static String fake_version;
	SharedPreferences appSharedPreferences;
	DevicePolicyManager devicePolicyManager;
    ComponentName mAdminName;
    Intent intent;
    TelephonyManager tm;

    //boolean notification;
    boolean appState;
    //int icon_draw;
    MyNotificationManager notiManager;
    final Context context = MainActivity.this;
    public String versionMsg;

    

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = MainActivity.this;
        appSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        setContentView(R.layout.activity_main);
        notiManager = new MyNotificationManager(context);
        //notification = appSharedPreferences.getBoolean(SH_PR_IS_NOTI, true);
        appState = appSharedPreferences.getBoolean(SH_PR_APP, false);
        //icon_draw = appSharedPreferences.getInt(SH_PR_ICN_SIZE, R.drawable.icon36);
        
        
        /*final CheckBox cb = (CheckBox) findViewById(R.id.checkBoxnotification);
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					setNotification(true);
				}
				else
					setNotification(false);
				saveAll();
			}
        }
		);  
        cb.setChecked(notification);*/
        final ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButton1);

        /*setVersion(appSharedPreferences.getString(SH_PR_VER, "1.3"));
        setUpdate(appSharedPreferences.getString(SH_PR_DATE, "20-10-12"));*/

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, DeviceAdminRec.class);   
        startDeviceAdmin( );
        
        
        
        tb.setChecked(appState);
        tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				boolean state = ((ToggleButton) buttonView).isChecked();
				if (state)
				{
					
					notiManager.createNotification(context);
					devicePolicyManager.setCameraDisabled(mAdminName, true);
					Toast.makeText(context, "Camera disabled", Toast.LENGTH_SHORT).show();
					appState = true;
					
				}
				else
				{
					notiManager.clearNotification();
		        	devicePolicyManager.setCameraDisabled(mAdminName, false);
		        	Toast.makeText(context, "Camera enabled", Toast.LENGTH_SHORT).show();
		        	appState = false;
				}
				saveAll();
			}
        }
		);
        saveAll();

        
	}
        	

	public void startDeviceAdmin()
	{
		 if (!devicePolicyManager.isAdminActive(mAdminName))
	        {
	        	intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
	            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
	                    "Just Do it :)");
	            startActivityForResult(intent, ACTIVATION_REQUEST);
//	            Log.i(tag, "admin intent start");
	        }
	}
	

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                        Log.i(tag, "Administration enabled!");
                } else {
                        Log.i(tag, "Administration enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
	}

	
	
/*	public void ButtonOnClick(View v) {
	    switch (v.getId()) {
	      case R.id.adminDisableButton:
	    	alertDialog();
//	    	devicePolicyManager.removeActiveAdmin(mAdminName);
	        break;
	      }
	}*/
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    public void alertDialog(){
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// set title
		alertDialogBuilder.setTitle("Your Title");
 
			// set dialog message
		alertDialogBuilder
			.setMessage("Admin will be disabled and Camera Enabled.Continue?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					devicePolicyManager.setCameraDisabled(mAdminName, false);
//			    	Toast.makeText(this, "Camera Enabled", Toast.LENGTH_SHORT).show();
			    	devicePolicyManager.removeActiveAdmin(mAdminName);
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
			alertDialog.show();
	}

    protected void onPause(){
        super.onPause();
        saveAll();
       
     }
    
    
    public void onStop() {
    	super.onStop();
    	saveAll();

	}



	public static String getUpdate() {
		return update;
	}



	public static void setUpdate(String update) {
		MainActivity.update = update;
	}



	public static String getVersion() {
		return fake_version;
	}
	
	public static void setVersion(String version) {
		MainActivity.fake_version = version;
	}
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_about:
	        try{
	        	PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(),0);
	        	String version = pInfo.versionName;	   
		        String msg = "FTS Ver " + version + "\n" + COPYRIGHT + "Losinho";
		        showAlertWindow(msg);
	        }
	        catch (NameNotFoundException e) {
	   					Log.d(tag, "No version");
	   			}
	        break;
	    case R.id.menu_settings:
	    {
	    	Intent mapIntent = new Intent(context, LocationActivity.class);
	    	mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    	startActivity(mapIntent);
	    }
	    	
	    }
	    return true;
	}
	

	
	public void saveAll()
	{
		// We need an Editor object to make preference changes.
	       SharedPreferences.Editor editor = appSharedPreferences.edit();
	       //editor.putBoolean(SH_PR_IS_NOTI, notification);
	       editor.putBoolean(SH_PR_APP, appState);
	       //editor.putInt(SH_PR_ICN_SIZE, icon_draw);
	       editor.putString(SH_PR_VER, fake_version);
	       // Commit the edits!
	       editor.commit();
	}
	
/*	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(this, v);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.actions, popup.getMenu());
	    popup.show();
	    popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
	    	public boolean onMenuItemClick(MenuItem item){
	    		switch (item.getItemId()) {
		        case R.id.size_1:
		            icon_draw = R.drawable.icon24;
		            saveAll();
		            return true;
		        case R.id.size_2:
		        	icon_draw = R.drawable.icon36;
		        	saveAll();
		            return true;
		        case R.id.size_3:
		        	icon_draw = R.drawable.icon48;
		        	saveAll();
		            return true;
		        default:
		        	
		            return false;
		       
	    		}
	    		
	    	}
	    	
	    }
	    );
	    saveAll();
	    
	}*/
	public void showAlertWindow(String msg)
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(this);  
    	ad.setCancelable(true); // This blocks the 'BACK' button
        ad.setPositiveButton("Ok", new OnClickListener(){
        	public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();				
			}			        	
        }
        );
        ad.setMessage(msg);  
        ad.show();  
	}
	
}
