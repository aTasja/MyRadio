package ashatova.myradio.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This contract defines the metadata for the App,
 * including the provider's access URIs and its "database" constants.
 */
public final class RadioContract {

    public static final class RadioEntry implements BaseColumns {

        /*
         * This ContentProvider's unique identifier.
         */
        public static final String AUTHORITY = "ashatova.myradio.radioprovider";

        /*
         * Name of the database table.
         */
        public static final String TABLE_NAME = "radio_table";

        /**
         * Database name.
         */
        public static final String DATABASE_NAME = "ashatova__myradio_radio_db";

        /**
         * Database version number, which is updated with each schema
         * change.
         */
        public static final int DATABASE_VERSION = 1;

        /*
         * Use AUTHORITY and TABLE_NAME to create the unique URI for Acronym
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME );

        /*
         * The MIME type of the {@link #CONTENT_URI} for a list of radio stations.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        /*
         * The MIME type of the {@link #CONTENT_URI} for a single radio station.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;


        /**
         * Columns to display.
         */
        public final static String _ID = BaseColumns._ID;
        public final static String RADIO_TITLE = "title";
        public final static String RADIO_URI = "uri";
        public final static String[] ALL_COLUMNS = {_ID, RADIO_TITLE, RADIO_URI};

        /**
         * Return a Uri that points to the row containing a given id.
         * @param id row id
         * @return Uri URI for the specified row id
         */
        public static Uri buildUri(Long id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}