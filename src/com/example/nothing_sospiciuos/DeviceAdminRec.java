package com.example.nothing_sospiciuos;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdminRec extends DeviceAdminReceiver {
	    void showToast(Context context, String msg) {
//	        String status = context.getString(R.string.admin_receiver_status, msg);
//	        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
	    }

	    @Override
	    public void onEnabled(Context context, Intent intent) {
	        Toast.makeText(context, "Admin Enabled", Toast.LENGTH_SHORT).show();
	    }

	    @Override
	    public CharSequence onDisableRequested(Context context, Intent intent) {
	        Toast.makeText(context, "Admin Disable Request", Toast.LENGTH_SHORT).show();
	        return " ";
	    }

	    @Override
	    public void onDisabled(Context context, Intent intent) {
	    	Toast.makeText(context, "Admin Disabled", Toast.LENGTH_SHORT).show();
	    }


}
