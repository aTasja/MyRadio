package ashatova.myradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * This Broadcast listens to PHONE STATE and notify RadioActivity about changes:
 * RINGING
 * OFFHOOK
 * IDLE
 */
public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE); // RINGING or OFFHOOK or IDLE
            Log.d("myLOG", "PHONE STATE - " + state);

            //send intent to activity to report phone state
            Intent newIntent = new Intent("phoneStateChange");
            newIntent.putExtra("state", state);
            context.sendBroadcast(newIntent);
            Log.d("myLOG", "PHONE STATE local broadcast sent = " + state);

        }
    }
}