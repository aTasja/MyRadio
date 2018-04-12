package ashatova.myradio;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import ashatova.myradio.database.RadioContract;

/**
 * This activity interacts with the user to coordinate query operations
 * on the RadioProvider using radio stations.
 */
public class AboutActivity extends Activity  {

    /**
     * Debugging tag used by the Android logger.
     */
    public static final String TAG = "myLOG";


    /**
     *Cursor field used for read-write access to the result set returned by a database query.
     */
    private Cursor mCursor;

    /**
     * ListView field for displaying database.
     */
    private ListView mLv;

    /**
     * Use Intent field to send intents to EditActivity/
     */
    Intent intent;

    /**
     * Hook method called when a new activity is created.  One time
     * initialization code goes here, e.g., initializing views.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view for this Activity.
        setContentView(R.layout.activity_aboutapp);
        //Log.d(TAG, "+-+-+DataActivity on create");
    }

    /**
     * Callback method invoked When the activity enters the Started state.
     * Display on screen database information.
     */
    @Override
    public  void onStart(){

        //Log.d(TAG, "+-+-+ Data Activity onStart()");
        super.onStart();

        // Cache ListView field for displaying database.
        mLv = findViewById(R.id.radioList);

        try{
            Uri uri = RadioContract.RadioEntry.CONTENT_URI;
            mCursor = this.getContentResolver().query(uri, null, null, null, null);
            //Log.d(TAG, "+-+-+mCursor");

            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(getApplicationContext(),
                    R.layout.line,
                    mCursor,
                    RadioContract.RadioEntry.ALL_COLUMNS,
                    new int[] {R.id._id, R.id.title, R.id.uri},
                    0);

            mLv.setAdapter(listAdapter);

            // Register a listeners to locate when the user
            // hits radio.
            // onItemClick methods starts EditActivity with passed id radio in extra.
            mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> mLv, View view, int position, long id) {
                    //Log.d(TAG, "position = " + position + " id = " + id);
                    intent = new Intent(AboutActivity.this, EditActivity.class);
                    intent.putExtra("whatRadio", (int)id);
                    getApplicationContext().startActivity(intent);
                }
            });

        }catch(SQLiteException e){
            Toast.makeText(this, getResources().getString(R.string.databaseUnavailable), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called before the activity is destroyed. This is the final call that the activity receives.
     * Close Cursor.
      */
    @Override
    public void onDestroy(){
        super.onDestroy();
        mCursor.close();
    }
}

