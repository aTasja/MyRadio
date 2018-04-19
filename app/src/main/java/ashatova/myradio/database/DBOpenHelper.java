package ashatova.myradio.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import ashatova.myradio.database.RadioContract.RadioEntry;

/**
 * The database helper used by the Radio Content Provider to create
 * and manage its underlying SQLite database.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    /*
     * Debugging tag used by the Android logger.
     */
    private final String TAG = "myLOG";



    /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the database in the
     * application's cache directory, which will be automatically
     * cleaned up by Android if the device runs low on storage space.
     *
     * @param context Any context
     */
    public DBOpenHelper(Context context) {
        super(context,
                RadioEntry.DATABASE_NAME,
                null,
                RadioEntry.DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //SQL statement used to create the Hobbit table.
        final String SQL_CREATE_RADIO_TABLE =
                "CREATE TABLE " + RadioEntry.TABLE_NAME + " (" +
                        RadioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RadioEntry.RADIO_TITLE + " TEXT NOT NULL UNIQUE, " +
                        RadioEntry.RADIO_URI + " TEXT NOT NULL, " +
                        //);"
                        "UNIQUE("+ RadioEntry.RADIO_TITLE +", " + RadioEntry.RADIO_URI +"));";
        // Create the table.
        db.execSQL(SQL_CREATE_RADIO_TABLE);
        //Log.d(TAG, "SQLiteDatabase created");
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database. Existing contents will be lost. ["
                + oldVersion + "]->[" + newVersion + "]");

        // Delete the existing tables.
        db.execSQL("DROP TABLE IF EXISTS " + RadioEntry.TABLE_NAME);
        // Create the new tables.
        onCreate(db);
    }
}
