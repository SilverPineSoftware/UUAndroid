package uu.toolbox.data;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;

/**
 * A set of wrapper methods to work with ContentValues
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class UUContentValues
{
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Public Static Methods
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets a String value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putString(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final String value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Long value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putLong(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Long value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets an Integer value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putInteger(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Integer value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Short value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putShort(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Short value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Byte value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putByte(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Byte value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Boolean value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putBoolean(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Boolean value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a byte[] value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putData(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final byte[] value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Float value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putFloat(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Float value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Double value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putDouble(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Double value)
    {
        cv.put(key.toString(), value);
    }

    /**
     * Sets a Character value
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putCharacter(@NonNull final ContentValues cv, @NonNull final Object key, @Nullable final Character value)
    {
        if (value == null)
        {
            cv.putNull(key.toString());
        }
        else
        {
            cv.put(key.toString(), (byte)value.charValue());
        }
    }

    /**
     * Sets a value if both the key and value are not null
     *
     * @param cv destination content values
     * @param key the key
     * @param value the value
     */
    public static void putObject(@NonNull final ContentValues cv, @NonNull final Object key, final Object value)
    {
        if (value == null)
        {
            cv.putNull(key.toString());
            return;
        }

        Class fieldType = value.getClass();

        if (fieldType == String.class)
        {
            putString(cv, key, (String)value);
        }
        else if (fieldType == Long.class || fieldType == long.class)
        {
            putLong(cv, key, (Long)value);
        }
        else if (fieldType == Integer.class || fieldType == int.class)
        {
            putInteger(cv, key, (Integer)value);
        }
        else if (fieldType == Short.class || fieldType == short.class)
        {
            putShort(cv, key, (Short)value);
        }
        else if (fieldType == Byte.class || fieldType == byte.class)
        {
            putByte(cv, key, (Byte)value);
        }
        else if (fieldType == Float.class || fieldType == float.class)
        {
            putFloat(cv, key, (Float)value);
        }
        else if (fieldType == Double.class || fieldType == Double.class)
        {
            putDouble(cv, key, (Double)value);
        }
        else if (fieldType == Boolean.class || fieldType == boolean.class)
        {
            putBoolean(cv, key, (Boolean)value);
        }
        else if (fieldType == byte[].class)
        {
            putData(cv, key, (byte[])value);
        }
        else if (fieldType == Byte[].class)
        {
            Byte[] val = (Byte[])value;
            byte[] tmp = new byte[val.length];
            for (int i = 0; i < tmp.length; i++)
            {
                tmp[i] = val[i];
            }

            putData(cv, key, tmp);
        }
        else if (fieldType == Character.class || fieldType == char.class)
        {
            putCharacter(cv, key, (Character)value);
        }
        else if (fieldType.isEnum())
        {
            putString(cv, key, ((Enum)value).name());
        }
        else
        {
            UULog.debug(UUDataModel.class, "putObject", "Field Type " + fieldType + " cannot be coerced into a type to put into ContentValues");
        }
    }
}
