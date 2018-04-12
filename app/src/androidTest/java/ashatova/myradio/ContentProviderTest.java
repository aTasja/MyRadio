package ashatova.myradio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ashatova.myradio.database.DBOpenHelper;
import ashatova.myradio.database.RadioContract;
import ashatova.myradio.database.RadioUtils;


/**
 * This class tests the content provider for the My Radio sample application.
 */
public class ContentProviderTest{

    // Contains a reference to context under test.
    private Context testContext;

    // Contains a reference to DBOpenHelper class under test.
    private DBOpenHelper dbOpenHelper;

    // Contains an SQLite database, used as test data
    private SQLiteDatabase tdb;

    // Contains a reference to the content resolver for the provider under test.
    private ContentResolver testContentResolver;

    // Reference to uri that used to test
    private Uri testUri;

    // Debugging tag used by the Android logger.
    private String TAG = "myLOG";

    // Contains the test data, radio station to insert, update and query

    //Radio 1 - will be inserted in empty database and tested if it was done successfully
    private String hTitle1 = "Радио Маяк";
    private String hURL1 = "https://icecast-vgtrk.cdnvideo.ru/mayakfm_aac_64kbps";

    //Radio 2 - will be inserted in empty database and tested if title from query equals Radio 2 title
    private String hTitle2 = "BBC Radio";
    private String hURL2 = "http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio1_mf_p ";

    // Radio 3 - will be used to update Radio 2 by Radio 3 and tested if Radio 3 title equals to title from query
    private String hTitle3 = "test title3";
    private String hURL3 = "http://testURI ";


    /*
     * Sets up the test environment before each test method. Creates a content resolver,
     * gets the provider under test, and creates a new database for the provider.
     */
    @Before
    public void setUp() {

        // Get context for tests
        testContext = InstrumentationRegistry.getTargetContext();

        // Initialize db
        dbOpenHelper = new DBOpenHelper( testContext );
        try {
            tdb = dbOpenHelper.getWritableDatabase();  // open database for write
        } catch (SQLException ex) {
            Log.d(TAG, "Error opening database for write");
            ex.printStackTrace();
        }
        // Gets the resolver for this test.
        testContentResolver = testContext.getContentResolver();
    }

    /**
     * Radio 1 - will be inserted and tested if it was done successfully
     */
    @Test
    public void insertRowTest() {
        // Assuming database created successfully
        // Let's try adding a record of rad

        try {

            ContentValues values = new ContentValues();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, hTitle1);
            values.put(RadioContract.RadioEntry.RADIO_URI, hURL1);

            testUri = testContentResolver.insert( RadioContract.RadioEntry.CONTENT_URI, values );

        } catch (Exception e) {
            Log.d(TAG, "Error inserting record into database");
            e.printStackTrace();
        }

        // insert should return a non-null Uri
        Assert.assertNotNull(testUri);
    }

    /**
     * Radio 2 - will be inserted in empty database
     * and tested if title from query equals to Radio 2 title.
     * Radio 3 - will be used to update Radio 2 by Radio 3
     * and tested if title of Radio 3 equals to title from query.
     */
    @Test
    public void insertRowandQueryTest() {
        Cursor testCursor;
        ArrayList<RadioUtils> radioArray;

        // delete database in case there is one out there from previous tests.
        testContext.deleteDatabase(RadioContract.RadioEntry.DATABASE_NAME);

        //  insert radio record
        try {
            ContentValues values = new ContentValues();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, hTitle2);
            values.put(RadioContract.RadioEntry.RADIO_URI, hURL2);

            testContentResolver.insert( RadioContract.RadioEntry.CONTENT_URI, values );

        } catch (Exception e) {
            e.printStackTrace();
        }

        // get stuff ready for query

        String rowQueryString = RadioContract.RadioEntry.RADIO_TITLE + " = ?";
        String args[] = { hTitle2 };
        radioArray = new ArrayList<>();

        try {
            testCursor = testContentResolver.query(RadioContract.RadioEntry.CONTENT_URI,
                    RadioContract.RadioEntry.ALL_COLUMNS, rowQueryString, args, null);

            // Get database stable records
            if (testCursor != null && testCursor.moveToFirst()) {
                do {
                    RadioUtils radio = RadioUtils.fromCursor(testCursor);
                    radioArray.add(radio);
                } while (testCursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG, "Exception   - " + e);
        }

        RadioUtils testInsertedRadio = radioArray.get(0);

        // Record we inserted should equal record we queried.
        // Let's verify it by checking the radio title.
        junit.framework.Assert.assertEquals(hTitle2, testInsertedRadio.getTitle());

        //*****************************************************************
        // Update radio record in the same database

        try {
            ContentValues values = new ContentValues();
            values.put(RadioContract.RadioEntry.RADIO_TITLE, hTitle3);
            values.put(RadioContract.RadioEntry.RADIO_URI, hURL3);

            testContentResolver.update( RadioContract.RadioEntry.CONTENT_URI,
                                        values,
                                        RadioContract.RadioEntry._ID + "=" + testInsertedRadio.getId(),
                                        null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // get stuff ready for query

        rowQueryString = RadioContract.RadioEntry.RADIO_TITLE + " = ?";
        String args2[] = { hTitle3 };
        radioArray = new ArrayList<>();

        try {
            testCursor = testContentResolver.query(RadioContract.RadioEntry.CONTENT_URI,
                    RadioContract.RadioEntry.ALL_COLUMNS, rowQueryString, args2, null);

            // Get database stable records

            if (testCursor != null && testCursor.moveToFirst()) {
                do {
                    RadioUtils radio = RadioUtils.fromCursor(testCursor);
                    radioArray.add(radio);

                } while (testCursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG, "Exception   - " + e);
        }
        RadioUtils testUpdatedRadio = radioArray.get(0);

        // Record we updated should equal record we queried.
        //  Let's verify it by checking the radio title.
        junit.framework.Assert.assertEquals(hTitle3, testUpdatedRadio.getTitle());
    }

    @After
    public void tearDown(){
        // cleanup
        tdb.close();
        testContext.deleteDatabase(RadioContract.RadioEntry.DATABASE_NAME);
    }
}


