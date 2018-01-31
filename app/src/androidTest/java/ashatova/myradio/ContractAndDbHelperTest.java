package ashatova.myradio;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ashatova.myradio.database.DBOpenHelper;
import ashatova.myradio.database.RadioContract;

import static junit.framework.Assert.assertEquals;

/**
 *  ContractAndDbHelperTest class tests the RadioContract class which contains many constants
 *  and records of the Contract Provider and it uses some of the functions
 *  of the DBOpenHelper class which can do actual database table creation.
 */

@RunWith(AndroidJUnit4.class)
public class ContractAndDbHelperTest {

    // Contains a reference to context under test.
    Context testContext;

    // Contains a reference to the content resolver for the provider under test.
    ContentResolver testContentResolver;

    // Contains a reference to DBOpenHelper class under test.
    DBOpenHelper dbOpenHelper;

    // Contains an SQLite database, used as test data
    SQLiteDatabase  tdb;

    // Debugging tag used by the Android logger.
    String TAG = "myLOG";


    // Contains the test data, radio station to insert
    String hTitle = "Радио Маяк";
    String hURL = "https://icecast-vgtrk.cdnvideo.ru/mayakfm_aac_64kbps";

    /*
     * Sets up the test environment before each test method. Creates a content resolver,
     * gets the provider under test, and creates a new database for the provider.
     */
    @Before
    public void setUp() {
        testContext = InstrumentationRegistry.getTargetContext();

        // initialize db
        dbOpenHelper = new DBOpenHelper( testContext );
        try {
            tdb = dbOpenHelper.getWritableDatabase();  // open database for write
        } catch (SQLException ex) {
            Log.d(TAG, "Error opening database for write");
        }
        testContentResolver = testContext.getContentResolver();

    }

    /**
     * Just created database should be empty.
     */
    @Test
    public void databaseEmpty() {

        // get data
        Cursor testCursor = testContentResolver.query(RadioContract.RadioEntry.CONTENT_URI,
                RadioContract.RadioEntry.ALL_COLUMNS, null, null, null);

        // query should return non-null if database created correctly
        Assert.assertNotNull(testCursor);
    }

    /**
     * Test radio will be inserted in empty database and tested if it was done successfully
     */
    @Test
    public void insertSuccessful() {
        // assuming database created successfully
        // Let's try adding a record
        ContentValues testContentValues = new ContentValues();

        // Insert data.
        testContentValues.put( RadioContract.RadioEntry.RADIO_TITLE,    hTitle);
        testContentValues.put( RadioContract.RadioEntry.RADIO_URI, hURL);
        Uri testUri = testContentResolver.insert(RadioContract.RadioEntry.CONTENT_URI, testContentValues );

        // insert should return a non-null Uri
        Assert.assertNotNull(testUri);
    }

    /**
     * Test created by Android.  Used to test if Android Studio had recovered from a meltdown.
     * Figured I'd keep it in for debugging, since I know it works.
     * @throws Exception - exception
    */
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context mContext = InstrumentationRegistry.getTargetContext();
        assertEquals("ashatova.myradio", mContext.getPackageName());
    }

    @After
    public void tearDown() {
        tdb.close();
    }

}

