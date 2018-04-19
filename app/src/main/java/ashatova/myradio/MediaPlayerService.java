package ashatova.myradio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.io.IOException;

/**
 * This MediaPlayerService extends Service and uses a MediaPlayer to
 * play stream radio in the background.  Although it runs in
 * the main thread (which could also be the UI thread if the
 * AndroidManifest.xml file is changed to remove the "android:process"
 * attribute), it implements MediaPlayer.OnPreparedListener to avoid
 * blocking the main thread while a radio is initially streamed.
 */
public class MediaPlayerService
        extends Service
        implements MediaPlayer.OnPreparedListener{
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = "myLOG";

    /**
     * Keep track of whether a radio is currently playing.
     */
    private static boolean mRadioPlaying;

    /**
     * The MediaPlayer that plays a radio in the background.
     */
    private static MediaPlayer mPlayer;

    /**
     * The NOTIFICATION_ID used to handle notifications.
     */
    public static final int NOTIFICATION_ID = 5453;

    /**
     * Use this boolean to check if intent to Activity is sent
     */
    boolean intentToActivityIsSent = false;

    /**
     * Hook method called when a new instance of Service is created.
     * One time initialization code goes here.
     */
    @Override
    public void onCreate() {
        //Log.i(TAG,"Service onCreate() entered");

        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate();

        // Create a MediaPlayer that will play the requested radio.
        mPlayer = new MediaPlayer();

        // Indicate the MediaPlayer will stream the audio.
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }



    /**
     * Hook method called every time startService() is called with an
     * Intent associated with this MusicService.
     */
    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startid) {
        // Extract the URL for the radio to play.
        final String radioURL = intent.getStringExtra("url");
        final String radioTitle = intent.getStringExtra("title");

        showText(radioTitle);

        Log.d(TAG,"Service onStartCommand() entered with radio URL " + radioURL);

        if (mRadioPlaying)
            // Stop playing the current radio.
            stopRadio();

        try {
            // Indicate the URL indicating the radio to play.
            mPlayer.setDataSource(radioURL);

            // Register "this" as the callback when the designated
            // song is ready to play.
            mPlayer.setOnPreparedListener(this);

            // This call doesn't block the UI Thread.
            mPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d(TAG, "Service music player exception : " + e);
        }

        // Restart Service if it shuts down.
        return START_STICKY;
    }

    /**
     * This no-op method is necessary since MusicService is a
     * so-called "Started Service".
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Hook method called back when MediaPlayer is ready to play the
     * song.
     */
    public void onPrepared(MediaPlayer player) {
        Log.i(TAG,"Service onPrepared() entered");

        // Just play the buffered piece of content once, rather than have it loop endlessly.
        player.setLooping(false);

        // Note that radio is now playing.
        mRadioPlaying = true;

        // Start playing the song.
        player.start();

        //send local intent to activity to report the radio has been connected
        if (!intentToActivityIsSent) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("RADIO CONNECTED"));
            intentToActivityIsSent = true;
        }
    }

    /**
     * Stops the MediaPlayer from playing the song.
     */
    private void stopRadio() {
        //Log.i(TAG,"Service stopRadio() entered");

        // Stop playing the radio.
        mPlayer.stop();

        // Reset the state machine of the MediaPlayer.
        mPlayer.reset();

        // Note that no radio is playing.
        mRadioPlaying = false;
    }

    private void showText(final String text){

        Intent intent = new Intent(this, RadioActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                                                (int)System.currentTimeMillis(),
                                                                intent,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(text)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID,  notification);
    }

    /**
     * Hook method called when the MusicService is stopped.
     */
    @Override
    public void onDestroy() {
        Log.i(TAG,"Service onDestroy() entered");

        // Stop playing the song.
        stopRadio();

        // Call up to the super class.
        super.onDestroy();

        //Remove this service from foreground state, allowing it to be killed if more memory is needed.
        stopForeground(true);
        stopSelf();

        //Remove Notification
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null){
                notificationManager.cancel(NOTIFICATION_ID);
            }

    }

}