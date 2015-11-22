package com.example.customdailingscreen;
import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class IncomingCallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            Log.d(CustomPhoneStateListener.TAG, "flag2");
            CustomPhoneStateListener.incomingCallActivity = this;
            // After this line, the code is not executed in Android 4.1 (Jelly Bean) only/*

            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

            Log.d(CustomPhoneStateListener.TAG, "flagy");

            setContentView(R.layout.activity_main);

            Log.d(CustomPhoneStateListener.TAG, "flagz");

            String number = getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            TextView text = (TextView) findViewById(R.id.name);
            text.setText("Incoming call from " + number);
        } 
        catch (Exception e) {
            Log.d(CustomPhoneStateListener.TAG,"exception: "+ e.toString());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}