package uu.toolbox.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *
 * Facade to wrap SQLiteStatement
 *  
 */
public interface UUSQLiteStatement
{
    void bindString(final int index, @NonNull final String value);
    void bindLong(final int index, final long value);
    void bindDouble(final int index, final double value);
    void bindBlob(final int index, @NonNull final byte[] value);
    void bindNull(final int index);
    void execute();
    long executeInsert();
    int executeUpdateDelete();
    void clearBindings();
    void close();

    default void safeBind(final int index, @Nullable final String value)
    {
        if (value == null)
        {
            bindNull(index);
        }
        else
        {
            bindString(index, value);
        }
    }

    default void safeBind(final int index, @Nullable final Long value)
    {
        if (value == null)
        {
            bindNull(index);
        }
        else
        {
            bindLong(index, value);
        }
    }

    default void safeBind(final int index, @Nullable final Double value)
    {
        if (value == null)
        {
            bindNull(index);
        }
        else
        {
            bindDouble(index, value);
        }
    }

    default void safeBind(final int index, @Nullable final byte[] value)
    {
        if (value == null)
        {
            bindNull(index);
        }
        else
        {
            bindBlob(index, value);
        }
    }

    default void safeBind(final int index, @Nullable final Object value)
    {
        if (value == null)
        {
            bindNull(index);
        }
        else
        {
            if (value instanceof String)
            {
                bindString(index, (String)value);
            }
            else if (value instanceof byte[])
            {
                bindBlob(index, (byte[])value);
            }
            else if (value instanceof Double)
            {
                bindDouble(index, (double)value);
            }
            else if (value instanceof Float)
            {
                bindDouble(index, ((Float)value).doubleValue());
            }
            else if (value instanceof Long)
            {
                bindLong(index, (long)value);
            }
            else if (value instanceof Integer)
            {
                bindLong(index, ((Integer)value).longValue());
            }
            else if (value instanceof Boolean)
            {
                bindLong(index, ((Boolean)value) ? 1 : 0);
            }
            else
            {
                bindNull(index);
            }
        }
    }
}
