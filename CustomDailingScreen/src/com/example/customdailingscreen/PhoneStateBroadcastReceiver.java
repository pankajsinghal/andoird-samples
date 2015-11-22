package com.example.customdailingscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver{

	static TelephonyManager telephonyManager;
	static CustomPhoneStateListener customPhoneStateListener;
    @Override
    public void onReceive(Context context, Intent intent) {
    	
//    	Log.d("PhoneState", "test 1");
//        Toast.makeText(context, "test1" , Toast.LENGTH_LONG).show();
//    	Log.d("PhoneState", "created receiver");
        if (customPhoneStateListener==null)
        	customPhoneStateListener = new CustomPhoneStateListener(context);
        if (telephonyManager==null){
        	telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        	telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
//        Log.d("PhoneState", "test 2");
//        Toast.makeText(context, "test2" , Toast.LENGTH_LONG).show();
        
    }
    /*public void onDestroy() {
    	Log.d("PhoneState", "destroyed receiver");
        telephonyManager.listen(customPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }*/
}