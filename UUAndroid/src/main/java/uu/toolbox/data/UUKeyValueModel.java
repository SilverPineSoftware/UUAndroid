package uu.toolbox.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uu.toolbox.core.UUObject;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

/**
 * UUKeyValueModel is a simple UUDataModel to store rows of key/value data, such as application
 * config or preferences.  This is helpful when applications wish to use SQLite instead of
 * SharedPreferences.
 */
@UUSqlTable()
@SuppressWarnings({"unused", "WeakerAccess"})
public class UUKeyValueModel implements UUDataModel
{
    @UUSqlColumn(primaryKey = true)
    private String key;

    @UUSqlColumn()
    @SuppressWarnings("unused")
    private final String className = null;

    @UUSqlColumn()
    private Object value;

    public UUKeyValueModel()
    {

    }

    public UUKeyValueModel(@NonNull final String key, @Nullable final Object value)
    {
        this.key = key;
        this.value = value;
    }

    @Nullable
    private String valueClass()
    {
        if (value == null)
        {
            return null;
        }

        return value.getClass().getName();
    }

    @Nullable
    private String valueToString()
    {
        if (value == null)
        {
            return null;
        }

        if (value instanceof byte[])
        {
            return UUString.byteToBase64String((byte[])value);
        }

        return value.toString();
    }

    @Nullable
    private Class<?> storageClass(@Nullable final Class<?> actualClass)
    {
        if (actualClass == null)
        {
            return null;
        }

        if (actualClass == byte[].class ||
            actualClass == Boolean.class)
        {
            return String.class;
        }

        return actualClass;
    }

    @Nullable
    private Object parseValue(@NonNull final Class<?> actualClass, @Nullable final Object value)
    {
        if (value == null)
        {
            return null;
        }

        if (value instanceof String)
        {
            if (actualClass == byte[].class)
            {
                return UUString.stringToBase64Bytes((String) value);
            }
            else if (actualClass == Boolean.class)
            {
                return Boolean.parseBoolean((String)value);
            }
        }

        return value;
    }

    @NonNull
    @Override
    public ContentValues getContentValues(int version)
    {
        ContentValues cv = new ContentValues();
        UUContentValues.putString(cv, "key", key);
        UUContentValues.putString(cv, "class_name", valueClass());
        UUContentValues.putString(cv, "value", valueToString());
        return cv;
    }

    @Override
    public void fillFromCursor(@NonNull Cursor cursor)
    {
        key = UUCursor.safeGetString(cursor, "key");
        value = null;

        String className = UUCursor.safeGetString(cursor, "class_name");
        if (className != null)
        {

            try
            {
                Class<?> clazz = Class.forName(className);
                if (clazz != null)
                {
                    Class<?> storageClass = storageClass(clazz);
                    if (storageClass != null)
                    {
                        Object val = UUCursor.safeGet(storageClass, cursor, "value", null);
                        value = parseValue(clazz, val);
                    }
                }
            }
            catch (Exception ex)
            {
                UULog.debug(getClass(), "fillFromCursor", ex);
            }
        }
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getValueAsString()
    {
        return UUObject.safeCast(String.class, value);
    }

    public byte[] getValueAsBlob()
    {
        return UUObject.safeCast(byte[].class, value);
    }

    public Long getValueAsLong()
    {
        return UUObject.safeCast(Long.class, value);
    }

    public Integer getValueAsInteger()
    {
        return UUObject.safeCast(Integer.class, value);
    }

    public Short getValueAsShort()
    {
        return UUObject.safeCast(Short.class, value);
    }

    public Byte getValueAsByte()
    {
        return UUObject.safeCast(Byte.class, value);
    }

    public Double getValueAsDouble()
    {
        return UUObject.safeCast(Double.class, value);
    }

    public Float getValueAsFloat()
    {
        return UUObject.safeCast(Float.class, value);
    }

    public Boolean getValueAsBoolean()
    {
        return UUObject.safeCast(Boolean.class, value);
    }
}
