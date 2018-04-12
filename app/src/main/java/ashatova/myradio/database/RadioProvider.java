package ashatova.myradio.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ashatova.myradio.database.RadioContract;
import ashatova.myradio.database.RadioContract.RadioEntry;
import ashatova.myradio.database.DBOpenHelper;


public class RadioProvider extends ContentProvider {

    /**
     * Debugging tag used by the Android logger.
     */
    public static final String TAG = "myLOG";

    /**
     * Use DBOpenHelper to manage database creation and version
     * management.
     */
    private DBOpenHelper mDBOpenHelper;

    /**
     * Context for the Content Provider.
     */
    private Context mContext;

    //URI matcher code for the content URI for the radio table
    private static final int RADIOS = 100;
    //URI matcher code for the content URI for a single radio in the radio table
    private static final int RADIO_ID = 101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(RadioEntry.AUTHORITY,RadioEntry.TABLE_NAME, RADIOS);
        sUriMatcher.addURI(RadioEntry.AUTHORITY,RadioEntry.TABLE_NAME + "/#",RADIO_ID);
    }

    /**
     * Return true if successfully started.
     */
    @Override
    public boolean onCreate() {
        //Log.d(TAG, "RadioProvider onCreate");
        mContext = getContext();

        // Select the concrete implementor.
        // Create the DBOpenHelper.
        mDBOpenHelper = new DBOpenHelper(mContext);
        return true;
    }

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each
     * URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (sUriMatcher.match(uri)) {
            case RADIOS:
                return RadioEntry.CONTENT_LIST_TYPE;
            case RADIO_ID:
                return RadioEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }

    /**
     * Method called to handle insert requests from client apps.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        //Log.d(TAG, "RadioProvider insert " + uri.toString());
        if (sUriMatcher.match(uri) != RADIOS)
            throw new IllegalArgumentException("Unknown URI " + uri);

        SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();
        long rowID = db.insert(RadioContract.RadioEntry.TABLE_NAME, null, contentValues);
        Uri resultUri = ContentUris.withAppendedId(RadioEntry.CONTENT_URI, rowID);

        //Notifies registered observers that a row was inserted.
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(resultUri, null);
        }
        return resultUri;
    }

    /*
     * Method called to handle query requests from client
     * applications.
     */
    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;

        // Match the id returned by UriMatcher to query appropriate rows.
        switch (sUriMatcher.match(uri)) {
            case RADIOS:
                cursor = mDBOpenHelper.getWritableDatabase().query
                        (RadioEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case RADIO_ID:
                long _id = ContentUris.parseId(uri);
                cursor = mDBOpenHelper.getReadableDatabase().query
                        (RadioEntry.TABLE_NAME,
                                projection,
                                RadioContract.RadioEntry._ID + " = ?",
                                new String[]{String.valueOf(_id)},
                                null,
                                null,
                                sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return cursor;
    }

    /**
     * Method called to handle update requests from client
     * applications.
     */
    @Override
    public int update(@NonNull Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {
        int returnCount;
        final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match update rows.
        switch (sUriMatcher.match(uri)) {
            case RADIOS:
                returnCount = db.update(RadioEntry.TABLE_NAME,
                        cvs,
                        selection,
                        selectionArgs);
                break;
            case RADIO_ID:
                returnCount =  db.update(RadioEntry.TABLE_NAME,
                        cvs,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        if (returnCount > 0)
            // Notifies registered observers that row(s) were updated.
            mContext.getContentResolver().notifyChange(uri,null);
        return returnCount;
    }

    /**
     * Method called to handle delete requests from client
     * applications. This method is not needed in the app.
     */
    @Override
    public int delete(@NonNull Uri uri,
                      String selection,
                      String[] selectionArgs) {
        /*int returnCount;
        final SQLiteDatabase db = mDBOpenHelper.getWritableDatabase();

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match delete rows.
        switch (sUriMatcher.match(uri)) {
            case RADIOS:
                returnCount = db.delete(RadioEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case RADIO_ID:
                returnCount =  db.delete(RadioEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (selection == null || returnCount > 0)
            // Notifies registered observers that row(s) were deleted.
            mContext.getContentResolver().notifyChange(uri,null);

        return returnCount;*/
        return 0;
    }


}
