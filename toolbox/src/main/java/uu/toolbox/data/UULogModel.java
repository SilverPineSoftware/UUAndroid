package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * UULogModel is a table representation of an android logcat method call. This is helpful
 * when applications need to write logging statements to a database rather than logcat.
 */
@UUSqlTable()
@SuppressWarnings({"unused", "WeakerAccess"})
public class UULogModel implements UUDataModel
{
    public static final String TIMESTAMP_COLUMN = "timestamp";
    public static final String LEVEL_COLUMN = "level";
    public static final String TAG_COLUMN = "tag";
    public static final String MESSAGE_COLUMN = "message";

    @UUSqlColumn(name = TIMESTAMP_COLUMN)
    private long timestamp;

    @UUSqlColumn(name = LEVEL_COLUMN)
    private long level;

    @UUSqlColumn(name = TAG_COLUMN)
    private String tag;

    @UUSqlColumn(name = MESSAGE_COLUMN)
    private String message;

    public long getTimestamp()
    {
        return timestamp;
    }

    public long getLevel()
    {
        return level;
    }

    public String getTag()
    {
        return tag;
    }

    public String getMessage()
    {
        return message;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setLevel(long level)
    {
        this.level = level;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataModel
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Optimization Note -- UUDataModel's default implementation uses reflection to fill content
    // values, which can be slow when processing a large number of records, so for this class we
    // use a manual implementation to skip all the reflection.
    //
    @NonNull
    @Override
    public ContentValues getContentValues(final int version)
    {
        ContentValues cv = new ContentValues();
        UUContentValues.putLong(cv, TIMESTAMP_COLUMN, timestamp);
        UUContentValues.putLong(cv, LEVEL_COLUMN, level);
        UUContentValues.putString(cv, TAG_COLUMN, tag);
        UUContentValues.putString(cv, MESSAGE_COLUMN, message);
        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        timestamp = UUCursor.safeGetLong(cursor, TIMESTAMP_COLUMN);
        level = UUCursor.safeGetLong(cursor, LEVEL_COLUMN);
        tag = UUCursor.safeGetString(cursor, TAG_COLUMN);
        message = UUCursor.safeGetString(cursor, MESSAGE_COLUMN);
    }
}
