package ashatova.myradio;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Splash screen of app. Ask user READ_PHONE_STATE permission and
 * starts RadioActivity.
 */
public class SplashActivity extends Activity {

    //public static final String TAG = "myLOG";
    public final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;
    boolean requestAsked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestAsked = false;

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            askPermission();

        }else{
            Intent intent = new Intent(this, RadioActivity.class);
            startActivity(intent);
            finish(); }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                //Log.d("myLOG", "onRequestPermissionsResult " + grantResults);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("myLOG", "Permission granted!");
                } else {
                    askPermission();
                }
                Intent intent = new Intent(this, RadioActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void askPermission(){
        if (!requestAsked) {
            requestAsked = true;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }else{
            requestAsked = false;
            Toast.makeText(this, getResources().getString(R.string.permissionRequest), Toast.LENGTH_LONG).show();
            askPermission();
        }
    }

}

