package com.example.customdailingscreen;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CustomPhoneStateListener extends PhoneStateListener {

    //private static final String TAG = "PhoneStateChanged";
    Context context; //Context to make Toast if required 
    static Activity incomingCallActivity;
    
    static String TAG = "CUSTOMDAILINGSCREEN";
    
    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        
//        Log.d("PhoneState", "test "+ incomingNumber);
//        Toast.makeText(context, "test" , Toast.LENGTH_LONG).show();
        
        
        switch (state) {
        case TelephonyManager.CALL_STATE_IDLE:
            //when Idle i.e no call
        	incomingCallActivity.finish();
        	Log.d(TAG, "idle "+ incomingNumber);
            Toast.makeText(context, "Phone state Idle", Toast.LENGTH_LONG).show();
            break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
            //when Off hook i.e in call
            //Make intent and start your service here
        	Log.d(TAG, "offhook "+ incomingNumber);
            Toast.makeText(context, "Phone state Off hook", Toast.LENGTH_LONG).show();
            break;
        case TelephonyManager.CALL_STATE_RINGING:
            //when Ringing
        	Log.d(TAG, "call from "+ incomingNumber);
            Toast.makeText(context, "Phone state Ringing -->> " + incomingNumber, Toast.LENGTH_LONG).show();
            final String number = incomingNumber;
            new Handler().postDelayed(new Runnable() {   

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(context, IncomingCallActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, number);
                    context.startActivity(intent);
                }
            }, 2000);
            break;
        default:
//        	Log.d("PhoneState", "test "+ incomingNumber);
        
            break;
        }
    }
}