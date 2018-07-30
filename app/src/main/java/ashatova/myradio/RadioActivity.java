package ashatova.myradio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.util.Log;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;

import ashatova.myradio.database.RadioContract;
import ashatova.myradio.database.RadioUtils;

/**
 * This activity interacts with the user to start radio playing and
 * coordinate insert and query operations on the RadioProvider
 * using radio stations.
 */
public class RadioActivity extends Activity{

    /**
     * Use array list to collect all three radio station fields
     */
    public static ArrayList<RadioUtils> radioInstances;

    /**
     * Use RadioUtils object each radio.
     */
    public RadioUtils radio1;
    public RadioUtils radio2;
    public RadioUtils radio3;

    /**
     * Use progress bar while radio connecting
     */
    private ProgressBar progressBar;

    /**
     * Use TextView to show when internet connection lost
     */

    TextView internetConn;

    /**
     * RadioUtils object that stores fields of playing radio
     */
    public RadioUtils PLAYING = null;

    /**
     * Use Intent for intent construction and sending
     */
    Intent intent;

    /**
     * Use boolean for restoring connection.
     * If false - do not need reconnect.
     * If true - need reconnect.
     */
    private boolean connectionLost;

    /**
     * Debugging tag used by the Android logger.
     */
    public static final String TAG = "myLOG";

    /**
     * Use BroadcastReceiver instances for receiving intents
     */
    private BroadcastReceiver mReceiver = null;
    private BroadcastReceiver mNetworkReceiver = null;

    /**
     * Hook method called when a new activity is created.  One time
     * initialization code goes here, e.g., initializing views.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");


        setContentView(R.layout.activity_radio);

        // Cache TextView for info about Internet connectivity
        internetConn = findViewById(R.id.internet);
        internetConn.setVisibility(TextView.INVISIBLE);

        //add to database 3 radio stations
        initiateRadioStations();

        // Initialize a new BroadcastReceiver instance for local intents from RadioService.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Display a notification that radio has been connected.
                Toast.makeText(context, PLAYING.getTitle() + getResources().getString(R.string.isConnected),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "local intent received -- " + PLAYING.getTitle() + " is connected");

                // Update button text colors and hide progress bar
                buttonsProgressBarConnected(PLAYING);
            }
        };

        // Register Local Broadcast receiver - use to receive messages from service
        IntentFilter serviceFilter = new IntentFilter("RADIO CONNECTED");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, serviceFilter);

        //Initialize a new BroadcastReceiver instance for detecting internet connection of device
        // and restarting service when connection is restored.
        mNetworkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Network connectivity change");

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                    if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                        Log.d(TAG, "Network " + ni.getTypeName() + " connected");
                        if (connectionLost) {
                            internetConn.setVisibility(TextView.INVISIBLE);
                            Toast.makeText(context, getResources().getString(R.string.internetRestored) + ni.getTypeName(), Toast.LENGTH_SHORT).show();
                            connectionLost = false;
                            if (PLAYING != null) {
                                onStartService(PLAYING);
                            }
                        } else {
                            if (PLAYING != null) {
                                Toast.makeText(context, getResources().getString(R.string.internetRestored) + ni.getTypeName(), Toast.LENGTH_SHORT).show();
                                onStopService(PLAYING);
                                onStartService(PLAYING);
                            }
                        }
                    } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                        connectionLost = true;
                        Log.d(TAG, "There's no network connectivity");
                        internetConn.setVisibility(TextView.VISIBLE);
                        if (PLAYING != null) {
                            onStopService(PLAYING);
                        }
                    }
                }
            }
        };

        // Register Broadcast receiver to get network status changes
        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        // this is the constant value android.net.conn.CONNECTIVITY_CHANGE
        registerReceiver(mNetworkReceiver, netFilter);

        /*
         * Use PhoneStateListener instances for listening to phone state
         */
        TelephonyManager mIncomingCallsReceiver = (TelephonyManager)getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
            if (mIncomingCallsReceiver != null) {
                mIncomingCallsReceiver.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String incomingNumber) {
                        //Log.d(TAG, "PHONE STATE RADIO broadcast received = " + state);
                        switch (state) {
                            case TelephonyManager.CALL_STATE_IDLE:
                                if (PLAYING != null) {
                                    onStartService(PLAYING);
                                }
                                break;
                            case TelephonyManager.CALL_STATE_RINGING:
                                if (PLAYING != null) {
                                    onStopService(PLAYING);
                                }
                                break;
                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
            }
    }//close onCreate

    /**
     * When the activity enters the Started state, the system invokes this callback.
     * We need always updated information on the screen, so we initialize all filds in this method.
     */
    @Override
    protected void onResume(){
        super.onResume();

        Log.d(TAG, "onResume");

        // Get radio stations from database, get radio1, radio2 and radio3 with all fields inside
        updateRadioInstances();

        // Cache buttons for radio stations and set text on it
        radio1.radioButton = findViewById(R.id.radio1);
        radio1.radioButton.setText(radio1.getTitle());

        radio2.radioButton = findViewById(R.id.radio2);
        radio2.radioButton.setText(radio2.getTitle());

        radio3.radioButton = findViewById(R.id.radio3);
        radio3.radioButton.setText(radio3.getTitle());

        // Cache progress bar - use it while radio connecting
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        // Cache TextView fot Exit button
        final TextView exitButton = findViewById(R.id.exit);

        // Cache TextView for About and Edit button
        final TextView aboutButton = findViewById(R.id.about);



        // Register a listener to locate  when the user hits radio button.
        radio1.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleService(radio1);
            }
        });
        radio2.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleService(radio2);
            }
        });
        radio3.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleService(radio3);
            }
        });

        // Register a listener to locate  when the user hits About and Edit button.
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RadioActivity.this,
                        AboutActivity.class));
            }
        });

        // Register a listener to locate  when the user hits Exit button.
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();

            }
        });
    }

    /**
     * When App first started the method add to database three radio stations and their URLs:
     * Radio Iskatel - http://iskatel.hostingradio.ru:8015/iskatel-128.mp3
     * Nashe Radio - http://nashe1.hostingradio.ru/nashespb128.mp3
     * Piter FM - http://cdn.radiopiterfm.ru/piterfm
     */
    public void initiateRadioStations() {
        //Log.d(TAG, "initiateRadioStations()");

        Uri uri = RadioContract.RadioEntry.CONTENT_URI;
        Cursor mCursor = this.getContentResolver().query(uri, null, null, null, null);

        if(mCursor != null && !mCursor.moveToFirst()){
            //Log.d(TAG, "Database is empty. SAVING");

            ContentValues values = new ContentValues();
            values.clear();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, getResources().getString(R.string.radio1Title));
            values.put(RadioContract.RadioEntry.RADIO_URI, getResources().getString(R.string.radio1URL));
            getApplicationContext().getContentResolver().insert(RadioContract.RadioEntry.CONTENT_URI, values);

            values.clear();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, getResources().getString(R.string.radio2Title));
            values.put(RadioContract.RadioEntry.RADIO_URI, getResources().getString(R.string.radio2URL));
            getApplicationContext().getContentResolver().insert(RadioContract.RadioEntry.CONTENT_URI, values);

            values.clear();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, getResources().getString(R.string.radio3Title));
            values.put(RadioContract.RadioEntry.RADIO_URI, getResources().getString(R.string.radio3URL));
            getApplicationContext().getContentResolver().insert(RadioContract.RadioEntry.CONTENT_URI, values);

        }else{
            Log.d(TAG, "Cannot add to database - database is full");
        }
        if (mCursor != null) {
            mCursor.close();
        }
    }

    /**
     * Method gets radio stations from database and save it
     * - in ArrayList radioInstances.
     * - by names radio1, radio2, radio3
     */
    public void updateRadioInstances(){
        //Log.d(TAG, "updateRadioInstances()");
        Uri uri = RadioContract.RadioEntry.CONTENT_URI;
        Cursor mCursor = this.getContentResolver().query(uri, null, null, null, RadioContract.RadioEntry._ID + " ASC");
        radioInstances = new ArrayList<>(3);

        if(mCursor != null && mCursor.moveToFirst()) {
            //Log.d(TAG, "Database is full - displaying");
            do {
                RadioUtils radio = RadioUtils.fromCursor(mCursor);

                radioInstances.add(radio);

                //String title = radio.getTitle();
                //String radioUri = radio.getURI();
                ///String radioId = Long.toString(radio.getId());
                //Log.d(TAG, "title = " + title + " uri = " + radioUri + " radioID = " + radioId);

            } while (mCursor.moveToNext());
            radio1 = radioInstances.get(0);
            radio2 = radioInstances.get(1);
            radio3 = radioInstances.get(2);
        }else{
            Log.d(TAG, "Cannot show database. DATABASE IS EMPTY");
        }
        if (mCursor != null){
            mCursor.close();
        }
    }

    /**
     * Method is called by radio button, starts and stops PlayService.
     * @param radio - RadioUtils instance that contains all fields of selected radio
     */
    public void handleService(RadioUtils radio){
        //Log.d(TAG, "handleService + "+radio.getTitle());

        if (PLAYING != null){

            if (PLAYING.getTitle().equals(radio.getTitle())) {     // user has pressed button of the playing radio
                onStopService(PLAYING);
                PLAYING = null;
            }else {                               //user has pressed button of not playing radio
                onStopService(PLAYING);
                PLAYING = radio;
                onStartService(PLAYING);
            }

        }else {                     // user has pressed button when any radio is not playing
            PLAYING = radio;
            onStartService(PLAYING);
        }
    }

    /**
     * Method start PlayService by passing radio URL
      * @param radio - RadioUtils instance that contains all fields of selected radio
     */
    public void onStartService(RadioUtils radio){
        Log.d(TAG, "onStartService + "+radio.getTitle());

        // update buttons and show progress bar
        buttonsProgressBarLoading(radio);

        //intent = new Intent(this, PlayService.class);
        intent = new Intent(this, MediaPlayerService.class);
        intent.putExtra("url", radio.getURI());
        intent.putExtra("title", radio.getTitle());

        startService(intent);
    }

    /**
     * Method stops PlayService and MediaPlayer.
     */
    public void onStopService(RadioUtils radio){
        Log.d(TAG, "onStopService");

        // update color text of button of playing radio
        //radio.getButton().setTextColor(getResources().getColor(R.color.black));
        buttonsProgressBarDisconnected();

        // Display a notification that radio has been stopped.
        Toast.makeText(this, radio.getTitle() + getResources().getString(R.string.isStopped), Toast.LENGTH_SHORT).show();

        //stop RadioService
        stopService(new Intent(this, MediaPlayerService.class));
    }

    /**
     * Method starts when user push one of the radio stations.
     * It change visibility of progress bar, disable not selected radio stations
     * and set red color of text on selected radio button.
     * @param radio - RadioUtils instance that contains all fields of selected radio
     */
    public void buttonsProgressBarLoading(RadioUtils radio){
        Log.d(TAG, "buttonsProgressBarLoading");
        // start a long operation, make progress bar visible
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // make the buttons of other radio stations inactive, and the playing radio station is red
        for (RadioUtils oneRad: radioInstances){
            if (oneRad.getButton() == radio.getButton()){
                oneRad.getButton().setTextColor(ContextCompat.getColor(this, R.color.red));
            }else{
                oneRad.getButton().setTextColor(ContextCompat.getColor(this, R.color.gray));
                oneRad.getButton().setEnabled(false);
            }
        }
    }

    /**
     * Method starts when selected radio connected.
     * it enables not playing radio buttons and change colors of text on them black.
     * Also makes invisible progress bar
     */
    public void buttonsProgressBarConnected(RadioUtils radio){
        Log.d(TAG, "buttonsProgressBarConnected  = " + radio.getTitle());
        // make progress bar invisible
        progressBar.setVisibility(View.GONE);

        for(RadioUtils oneRad:radioInstances){
            // make buttons of not playing radio stations active and their text color black
            if(oneRad.getButton() != PLAYING.getButton()){
                oneRad.getButton().setTextColor(ContextCompat.getColor(this, R.color.black));
                oneRad.getButton().setEnabled(true);
            // make the text color of button of playing radio red
            }else{
                radio.getButton().setTextColor(ContextCompat.getColor(this, R.color.red));
            }
        }
    }

    /**
     * Method starts when selected stop radio.
     * It enables all radio buttons and change colors of text on them black.
     * Also makes invisible progress bar.
     */
    public void buttonsProgressBarDisconnected(){
        Log.d(TAG, "buttonsProgressBarDisconnected");
        // make progress bar invisible
        progressBar.setVisibility(View.GONE);

        // make all buttons of radio stations active and their text color black
        for (RadioUtils oneRad : radioInstances) {
            oneRad.getButton().setTextColor(ContextCompat.getColor(this, R.color.black));
            oneRad.getButton().setEnabled(true);
        }
    }

    /**
     * Called when user push Exit button.
     * It stops PlayService, unregisters BroadcastReceivers and close App.     *
     */
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        // unregister BroadcastReceivers
        if (mReceiver != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mReceiver = null;
        }

        if (mNetworkReceiver != null){
            unregisterReceiver(mNetworkReceiver);
            mNetworkReceiver = null;
        }

        // stop service if radio is playing
        if (PLAYING != null){
            onStopService(PLAYING);
            PLAYING = null;
        }

        Toast.makeText(getApplicationContext(), R.string.onExit, Toast.LENGTH_SHORT).show();
    }
}
