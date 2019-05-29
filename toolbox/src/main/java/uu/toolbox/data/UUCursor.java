package uu.toolbox.data;

import android.database.Cursor;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

/**
 * A set of helper methods for the Cursor class
 */
@SuppressWarnings("unused")
public final class UUCursor
{
    public static short safeGetShort(final Cursor cursor, final Object column)
    {
        return safeGetShort(cursor, column, (short)0);
    }

    public static short safeGetShort(final Cursor cursor, final Object column, final short defaultValue)
    {
        Short objResult = safeGetShortObject(cursor, column, null);
        return (objResult != null ? objResult : defaultValue);
    }

    public static Short safeGetShortObject(final Cursor cursor, final Object column)
    {
        return safeGetShortObject(cursor, column, null);
    }

    public static Short safeGetShortObject(final Cursor cursor, final Object column, final Short defaultValue)
    {
        Short result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getShort(index);
            }
        }

        return result;
    }

    public static int safeGetInt(final Cursor cursor, final Object column)
    {
        return safeGetInt(cursor, column, 0);
    }

    public static int safeGetInt(final Cursor cursor, final Object column, final int defaultValue)
    {
        Integer objResult = safeGetIntObject(cursor, column, null);
        return (objResult != null ? objResult : defaultValue);
    }

    public static Integer safeGetIntObject(final Cursor cursor, final Object column)
    {
        return safeGetIntObject(cursor, column, null);
    }

    public static Integer safeGetIntObject(final Cursor cursor, final Object column, final Integer defaultValue)
    {
        Integer result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getInt(index);
            }
        }

        return result;
    }

    public static long safeGetLong(final Cursor cursor, final Object column)
    {
        return safeGetLong(cursor, column, 0L);
    }

    public static long safeGetLong(final Cursor cursor, final Object column, final long defaultValue)
    {
        Long objResult = safeGetLongObject(cursor, column, null);
        return (objResult != null ? objResult : defaultValue);
    }

    public static Long safeGetLongObject(final Cursor cursor, final Object column)
    {
        return safeGetLongObject(cursor, column, null);
    }

    public static Long safeGetLongObject(final Cursor cursor, final Object column, final Long defaultValue)
    {
        Long result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getLong(index);
            }
        }

        return result;
    }

    public static boolean safeGetBoolean(final Cursor cursor, final Object column)
    {
        return safeGetBoolean(cursor, column, false);
    }

    public static boolean safeGetBoolean(final Cursor cursor, final Object column, final boolean defaultValue)
    {
        Boolean objResult = safeGetBooleanObject(cursor, column, null);
        return (objResult != null ? objResult : defaultValue);
    }

    public static Boolean safeGetBooleanObject(final Cursor cursor, final Object column)
    {
        return safeGetBooleanObject(cursor, column, null);
    }

    public static Boolean safeGetBooleanObject(final Cursor cursor, final Object column, final Boolean defaultValue)
    {
        Boolean result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = (cursor.getInt(index) == 1);
            }
        }

        return result;
    }

    public static float safeGetFloat(final Cursor cursor, final Object column)
    {
        return safeGetFloat(cursor, column, 0.0f);
    }

    public static float safeGetFloat(final Cursor cursor, final Object column, final float defaultValue)
    {
        Float objResult = safeGetFloatObject(cursor, column, null);
        return (objResult != null ? objResult : defaultValue);
    }

    public static Float safeGetFloatObject(final Cursor cursor, final Object column)
    {
        return safeGetFloatObject(cursor, column, null);
    }

    public static Float safeGetFloatObject(final Cursor cursor, final Object column, final Float defaultValue)
    {
        Float result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getFloat(index);
            }
        }

        return result;
    }

    public static double safeGetDouble(final Cursor cursor, final Object column)
    {
        return safeGetDouble(cursor, column, (double)0.0f);
    }

    public static double safeGetDouble(final Cursor cursor, final Object column, final Double defaultValue)
    {
        Double objResult = safeGetDoubleObject(cursor, column, null);
        return (objResult != null ? objResult : defaultValue);
    }

    public static Double safeGetDoubleObject(final Cursor cursor, final Object column)
    {
        return safeGetDoubleObject(cursor, column, null);
    }

    public static Double safeGetDoubleObject(final Cursor cursor, final Object column, final Double defaultValue)
    {
        Double result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getDouble(index);
            }
        }

        return result;
    }

    public static byte[] safeGetBlob(final Cursor cursor, final Object column)
    {
        return safeGetBlob(cursor, column, null);
    }

    public static byte[] safeGetBlob(final Cursor cursor, final Object column, final byte[] defaultValue)
    {
        byte[] result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getBlob(index);
            }
        }

        return result;
    }

    public static String safeGetString(final Cursor cursor, final Object column)
    {
        return safeGetString(cursor, column, null);
    }

    public static String safeGetString(final Cursor cursor, final Object column, final String defaultValue)
    {
        String result = defaultValue;

        if (cursor != null && column != null)
        {
            int index = cursor.getColumnIndex(column.toString());
            if (index >= 0 && !cursor.isNull(index))
            {
                result = cursor.getString(index);
            }
        }

        return result;
    }

    /**
     * Safely gets a column value based on the cursor field type
     *
     * @param cursor a database cursor
     * @param column column name to get
     * @param defaultValue the default value
     *
     * @return an Object of type String, Long, Double, byte[], or null
     */
//    @Nullable
//    public static Object safeGet(
//            @NonNull final Cursor cursor,
//            @NonNull final Object column,
//            @Nullable final Object defaultValue)
//    {
//        int index = cursor.getColumnIndex(column.toString());
//        return safeGet(cursor, index, defaultValue);
//    }

    /**
     * Safely gets a column value based on the cursor field type
     *
     * @param cursor a database cursor
     * @param index index to get
     * @param defaultValue the default value
     *
     * @return an Object of type String, Long, Double, byte[], or null
     */

    @Nullable
    public static Object safeGet(
        @NonNull final Cursor cursor,
        @IntRange(from=0) final int index,
        @Nullable final Object defaultValue)
    {
        Object result;

        try
        {
            int colType = cursor.getType(index);
            switch (colType)
            {
                case Cursor.FIELD_TYPE_INTEGER:
                {
                    result = cursor.getLong(index);
                    break;
                }

                case Cursor.FIELD_TYPE_FLOAT:
                {
                    result = cursor.getDouble(index);
                    break;
                }

                case Cursor.FIELD_TYPE_STRING:
                {
                    result = cursor.getString(index);
                    break;
                }

                case Cursor.FIELD_TYPE_NULL:
                {
                    result = null;
                    break;
                }

                case Cursor.FIELD_TYPE_BLOB:
                {
                    result = cursor.getBlob(index);
                    break;
                }

                default:
                {
                    result = defaultValue;
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUCursor.class, "safeGet", ex);
            result = defaultValue;
        }

        return result;
    }

    @Nullable
    public static <T> Object safeGet(
            @NonNull Class<T> fieldType,
            @NonNull final Cursor cursor,
            @NonNull final Object column,
            @Nullable final T defaultValue)
    {
        try
        {
            if (fieldType == long.class)
            {
                return safeGetLong(cursor, column, defaultValue != null ? long.class.cast(defaultValue) : 0L);
            }
            else if (fieldType == int.class)
            {
                return safeGetInt(cursor, column, defaultValue != null ? int.class.cast(defaultValue) : 0);
            }
            else if (fieldType == short.class)
            {
                return safeGetShort(cursor, column, defaultValue != null ? short.class.cast(defaultValue) : (short)0);
            }
            else if (fieldType == byte.class)
            {
                int val = safeGetInt(cursor, column, defaultValue != null ? int.class.cast(defaultValue) : 0);
                return (byte)val;
            }
            else if (fieldType == float.class)
            {
                return safeGetFloat(cursor, column, defaultValue != null ? float.class.cast(defaultValue) : 0.0f);
            }
            else if (fieldType == double.class)
            {
                return safeGetDouble(cursor, column, defaultValue != null ? double.class.cast(defaultValue) : (double)0.0f);
            }
            else if (fieldType == boolean.class)
            {
                return safeGetBoolean(cursor, column, defaultValue != null ? boolean.class.cast(defaultValue) : false);
            }
            else if (fieldType == char.class)
            {
                int val = safeGetInt(cursor, column, defaultValue != null ? int.class.cast(defaultValue) : 0);
                return (char)((byte)val);
            }
            else if (fieldType == Long.class)
            {
                return safeGetLongObject(cursor, column, (Long)defaultValue);
            }
            else if (fieldType == Integer.class)
            {
                return safeGetIntObject(cursor, column, (Integer) defaultValue);
            }
            else if (fieldType == Short.class)
            {
                return safeGetShortObject(cursor, column, (Short)defaultValue);
            }
            else if (fieldType == Byte.class)
            {
                int val = safeGetInt(cursor, column, defaultValue != null ? int.class.cast(defaultValue) : 0);
                return (byte)val;
            }
            else if (fieldType == Float.class)
            {
                return safeGetFloatObject(cursor, column, (Float)defaultValue);
            }
            else if (fieldType == Double.class)
            {
                return safeGetDoubleObject(cursor, column, (Double)defaultValue);
            }
            else if (fieldType == Boolean.class)
            {
                return safeGetBooleanObject(cursor, column, (Boolean) defaultValue);
            }
            else if (fieldType == Character.class)
            {
                int val = safeGetInt(cursor, column, defaultValue != null ? int.class.cast(defaultValue) : 0);
                return (char)((byte)val);
            }
            else if (fieldType == String.class)
            {
                return UUCursor.safeGetString(cursor, column);
            }
            else if (Byte[].class == fieldType)
            {
                byte[] val = UUCursor.safeGetBlob(cursor, column);
                if (val != null)
                {
                    Byte[] tmp = new Byte[val.length];
                    for (int i = 0; i < tmp.length; i++)
                    {
                        tmp[i] = val[i];
                    }

                    return tmp;
                }
            }
            else if (byte[].class == fieldType)
            {
                return UUCursor.safeGetBlob(cursor, column, (byte[])defaultValue);
            }
            else if (fieldType.isEnum())
            {
                String stringVal = safeGetString(cursor, column);
                if (UUString.isNotEmpty(stringVal))
                {
                    return Enum.valueOf((Class<Enum>) fieldType, stringVal);
                }
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUDataModel.class, "getField", ex);
        }

        return defaultValue;
    }
}
