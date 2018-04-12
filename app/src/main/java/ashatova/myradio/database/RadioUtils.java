package ashatova.myradio.database;


import android.widget.TextView;
import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * A simple POJO that stores information about Radio stations.
 */
public class RadioUtils {

    /**
     * Id of the radio in the map.
     */
    private final long mId;

    /*
     * Radio title.
     */
    private String radioTitle;

    /*
     * Radio uri.
     */
    private String radioURI;

    /*
     * Radio button.
     */
    public TextView radioButton;


    /**
     * Constructor initializes all the fields.
     */
    public RadioUtils(long id,
                    String title,
                    String uri) {
        mId = id;
        radioTitle = title;
        radioURI = uri;
    }

    /**
     * Constructor initializes all fields from a cursor.
     */
    private RadioUtils(@NonNull Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(RadioContract.RadioEntry._ID));
        radioTitle = cursor.getString(cursor.getColumnIndex(RadioContract.RadioEntry.RADIO_TITLE));
        radioURI = cursor.getString(cursor.getColumnIndex(RadioContract.RadioEntry.RADIO_URI));
    }

    /**
     * Static builder method returns a new radio record from a given cursor.
     */
    public static RadioUtils fromCursor(Cursor cursor) {
        return new RadioUtils(cursor);
    }

    /**
     * @return the id of the radio.
     */
    public long getId() {
        return mId;
    }

    /**
     * @param title - radio title
     */
    public void setTitle(String title){
        radioTitle = title;
    }

    /**
     * @return radio uri
     */
    public String getURI(){
        return radioURI;
    }

    /**
     * @return radio title
     */
    public String getTitle(){
        return radioTitle;
    }

    /**
     * @return radio button
     */
    public TextView getButton(){
        return radioButton;
    }




}
