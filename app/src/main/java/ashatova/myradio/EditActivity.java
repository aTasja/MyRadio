package ashatova.myradio;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;

import ashatova.myradio.database.RadioContract;
import ashatova.myradio.database.RadioUtils;

/**
 * This activity interacts with the user to coordinate update operations
 * on the RadioProvider using radio stations.
 */
public class EditActivity extends Activity{

    /**
     * Debugging tag used by the Android logger.
     */
    public static final String TAG = "myLOG";

    /**
     * Use RadioUtils class to organize radio fields.
     */
    RadioUtils radio;

    /**
     * EditText field for entering new radio title.
     */
    EditText newTitle;

    /**
     * EditText field for entering new radio uri.
     */
    EditText newUri;


    /**
     * Hook method called when a new activity is created.  One time
     * initialization code goes here, e.g., initializing views.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view for this Activity.
        setContentView(R.layout.activity_edit);

        // get  radio number from intent and bind the number to particular radio instance
        int whatRadio = getIntent().getIntExtra("whatRadio", 0);
        //Log.d(TAG, "what radio = " + whatRadio);
        switch (whatRadio){
            case 1:
                radio = RadioActivity.radio1;
                break;
            case 2:
                radio = RadioActivity.radio2;
                break;
            case 3:
                radio = RadioActivity.radio3;
                break;
        }

        // Cache the EditText that holds the title entered by the user.
        newTitle = findViewById(R.id.enterTitle);
        newTitle.setHint(radio.getTitle());
        //Log.d(TAG, "TITLE = " + radio.getId());

        // Cache the EditText that holds the uri entered by the user.
        newUri = findViewById(R.id.enterUri);
        newUri.setHint(radio.getURI());
        //Log.d(TAG, "URI = " + radio.getURI());

        // Register a listener to locate  when the user
        // hits save button.
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //store title and uri as strings and pass them to updateRadio method
                final String title = newTitle.getText().toString();
                final String uri = newUri.getText().toString();
                updateRadio(radio, title, uri);
            }
        });
    }

    /**
     * Method validate URL of new radio station
     * @param url - ULR of new radio startion
     * @return - true/false
     */
    public boolean validateUrl(String url){
        return Patterns.WEB_URL.matcher(url).matches();
    }

    /**
     * Method update title and uri of particular radio. Make toast when the operation has been completed.
     * After all finish the Activity.
     * @param radio - RadioUtil instance
     * @param title - new title od radio
     * @param url - new uri of radio
     */
    public void updateRadio(RadioUtils radio, String title, String url){

        if (validateUrl(url)) {
            ContentValues values = new ContentValues();
            values.clear();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, title);
            values.put(RadioContract.RadioEntry.RADIO_URI, url);
            getApplicationContext().getContentResolver().update(RadioContract.RadioEntry.CONTENT_URI,
                    values,
                    RadioContract.RadioEntry._ID + "=" + radio.getId(),
                    null);
            //Log.d(TAG, "New Radio added to database - " + "title = " + title + " uri = " + uri + " id = " + Long.toString(radio.getId()));
            Toast.makeText(this, R.string.newRadioSaved, Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Toast.makeText(this, R.string.urlNotValid, Toast.LENGTH_SHORT).show();
        }

    }
}
