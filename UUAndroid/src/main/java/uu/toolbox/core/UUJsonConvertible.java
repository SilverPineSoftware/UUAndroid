package uu.toolbox.core;

import android.os.Parcel;
import android.util.JsonReader;
import android.util.Pair;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import uu.toolbox.logging.UULog;

public interface UUJsonConvertible
{
    default void fillFromJson(@NonNull JSONObject json)
    {
        try
        {
            Field[] fields = getClass().getDeclaredFields();
            for (Field field : fields)
            {
                field.setAccessible(true);

                UUJsonField fieldAnnotation = field.getAnnotation(UUJsonField.class);
                if (fieldAnnotation != null)
                {
                    String key = field.getName();
                    if  (UUString.isNotEmpty(key))
                    {
                        Class fieldType = field.getType();

                        boolean parsed = tryGetNative(this, json, field, fieldType, key);
                        if (parsed)
                        {
                            continue;
                        }

                        parsed = tryGetNativeArray(this, json, field, fieldType, key);
                        if (parsed)
                        {
                            continue;
                        }

//                        if (Byte[].class == fieldType || byte[].class == fieldType)
//                        {
//                            //columnType = UUSqlColumn.Type.BLOB.toString();
//                        }
//
                        /*if (ArrayList.class == fieldType)
                        {
                            JSONArray arr = UUJson.safeGetJsonArray(json, key);

                            Type elementType = null;
                            Type genericType = field.getGenericType();
                            if (genericType instanceof ParameterizedType)
                            {
                                ParameterizedType pt = (ParameterizedType)genericType;
                                Type[] ptArgs = pt.getActualTypeArguments();
                                if (ptArgs.length == 1)
                                {
                                    elementType = ptArgs[0];
                                }
                            }

                            UULog.debug(getClass(), "fillFromJson", "Field "  +  key + ", element type: " + elementType);

                        }
                        else
                        {*/
                            UULog.debug(getClass(), "fillFromJson", "Ignoring type: " + fieldType.getName());
                        //}
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "fillFromJson", ex);
        }
    }

    static boolean tryGetNative(
        @NonNull final UUJsonConvertible obj,
        @NonNull final JSONObject json,
        @NonNull final Field field,
        @NonNull final Class fieldType,
        @NonNull final String key) throws Exception
    {
        if (String.class == fieldType)
        {
            field.set(obj, getStringField(json, key));
        }
        else if (Long.class == fieldType)
        {
            field.set(obj,  getLongObjectField(json, key));
        }
        else if (long.class == fieldType)
        {
            field.setLong(obj,  getLongField(json, key));
        }
        else if (Integer.class == fieldType)
        {
            field.set(obj,  getIntegerObjectField(json, key));
        }
        else if (int.class == fieldType)
        {
            field.setInt(obj,  getIntegerField(json, key));
        }
        else if (Short.class == fieldType)
        {
            field.set(obj,  getShortObjectField(json, key));
        }
        else if (short.class == fieldType)
        {
            field.setShort(obj,  getShortField(json, key));
        }
        else if (Byte.class == fieldType)
        {
            field.set(obj,  getByteObjectField(json, key));
        }
        else if (byte.class == fieldType)
        {
            field.setByte(obj,  getByteField(json, key));
        }
        else if (Boolean.class == fieldType)
        {
            field.set(obj,  getBoolObjectField(json, key));
        }
        else if (boolean.class == fieldType)
        {
            field.set(obj,  getBoolField(json, key));
        }
        else if (Double.class == fieldType)
        {
            field.set(obj, getDoubleObjectField(json, key));
        }
        else if (double.class == fieldType)
        {
            field.setDouble(obj, getDoubleField(json, key));
        }
        else if (Float.class == fieldType)
        {
            field.set(obj, getFloatObjectField(json, key));
        }
        else if (float.class == fieldType)
        {
            field.setFloat(obj, getFloatField(json, key));
        }
        else
        {
            return false;
        }

        return true;
    }

    static boolean tryGetNativeArray(
        @NonNull final UUJsonConvertible obj,
        @NonNull final JSONObject json,
        @NonNull final Field field,
        @NonNull final Class fieldType,
        @NonNull final String key) throws Exception
    {
        if (ArrayList.class == fieldType)
        {
            Type elementType = null;
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType)
            {
                ParameterizedType pt = (ParameterizedType)genericType;
                Type[] ptArgs = pt.getActualTypeArguments();
                if (ptArgs.length == 1)
                {
                    elementType = ptArgs[0];
                }
            }

            if (elementType != null)
            {
                if (String.class == elementType)
                {
                    field.set(obj, getStringArrayField(json, key));
                }
                else if (Long.class == elementType)
                {
                    field.set(obj,  getLongArrayField(json, key));
                }
                else if (Integer.class == elementType)
                {
                    field.set(obj,  getIntegerArrayField(json, key));
                }
                else if (Short.class == elementType)
                {
                    field.set(obj,  getShortArrayField(json, key));
                }
                else if (Byte.class == elementType)
                {
                    field.set(obj,  getByteArrayField(json, key));
                }
                else if (Boolean.class == elementType)
                {
                    field.set(obj,  getBoolArrayField(json, key));
                }
                else if (Double.class == elementType)
                {
                    field.set(obj, getDoubleArrayField(json, key));
                }
                else if (Float.class == elementType)
                {
                    field.set(obj, getFloatArrayField(json, key));
                }
                else
                {
                    Object o = Class.forName(((Class) elementType).getName()).newInstance();
                    if (o instanceof UUJsonConvertible)
                    {
                        UUJsonConvertible jsonConvertible = (UUJsonConvertible)o;
                        field.set(obj, getJsonConvertibleArrayField(jsonConvertible.getClass(), json, key));
                        return true;
                    }

                    UULog.debug(UUJsonConvertible.class, "tryGetNativeArray", "Ignoring type: " + elementType);
                    return false;
                }

                return true;
            }

            //UULog.debug(getClass(), "fillFromJson", "Field "  +  key + ", element type: " + elementType);

        }

        return false;
    }

    @Nullable
    static String getStringField(final JSONObject json, final String key)
    {
        String result = UUJson.safeGetString(json, key);
        if (result == null)
        {
            String snakeKey = UUString.toSnakeCase(key);
            if (!UUString.areEqual(key, snakeKey))
            {
                result = UUJson.safeGetString(json, snakeKey);
            }
        }

        return result;
    }

    @Nullable
    static Long getLongObjectField(final JSONObject json, final String key)
    {
        Long result = UUJson.safeGetLongObject(json, key);
        if (result == null)
        {
            String snakeKey = UUString.toSnakeCase(key);
            if (!UUString.areEqual(key, snakeKey))
            {
                result = UUJson.safeGetLongObject(json, snakeKey);
            }
        }

        return result;
    }

    static long getLongField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetLong(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetLong(json, snakeKey);
        }

        return 0L;
    }

    @Nullable
    static Integer getIntegerObjectField(final JSONObject json, final String key)
    {
        Integer result = UUJson.safeGetIntObject(json, key);
        if (result == null)
        {
            String snakeKey = UUString.toSnakeCase(key);
            if (!UUString.areEqual(key, snakeKey))
            {
                result = UUJson.safeGetIntObject(json, snakeKey);
            }
        }

        return result;
    }

    static int getIntegerField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetInt(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetInt(json, snakeKey);
        }

        return 0;
    }

    @Nullable
    static Short getShortObjectField(final JSONObject json, final String key)
    {
        Integer tmp = getIntegerObjectField(json, key);
        if (tmp == null)
        {
            return null;
        }

        return tmp.shortValue();
    }

    static short getShortField(final JSONObject json, final String key)
    {
        int tmp = getIntegerField(json, key);
        return (short)tmp;
    }

    @Nullable
    static Byte getByteObjectField(final JSONObject json, final String key)
    {
        Integer tmp = getIntegerObjectField(json, key);
        if (tmp == null)
        {
            return null;
        }

        return tmp.byteValue();
    }

    static byte getByteField(final JSONObject json, final String key)
    {
        int tmp = getIntegerField(json, key);
        return (byte)tmp;
    }

    @Nullable
    static Boolean getBoolObjectField(final JSONObject json, final String key)
    {
        Boolean result = UUJson.safeGetBoolObject(json, key);
        if (result == null)
        {
            String snakeKey = UUString.toSnakeCase(key);
            if (!UUString.areEqual(key, snakeKey))
            {
                result = UUJson.safeGetBoolObject(json, snakeKey);
            }
        }

        return result;
    }

    static boolean getBoolField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetBool(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetBool(json, snakeKey);
        }

        return false;
    }

    @Nullable
    static Float getFloatObjectField(final JSONObject json, final String key)
    {
        Float result = UUJson.safeGetFloatObject(json, key);
        if (result == null)
        {
            String snakeKey = UUString.toSnakeCase(key);
            if (!UUString.areEqual(key, snakeKey))
            {
                result = UUJson.safeGetFloatObject(json, snakeKey);
            }
        }

        return result;
    }

    static float getFloatField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetFloat(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetFloat(json, snakeKey);
        }

        return 0.0f;
    }

    @Nullable
    static Double getDoubleObjectField(final JSONObject json, final String key)
    {
        Double result = UUJson.safeGetDoubleObject(json, key);
        if (result == null)
        {
            String snakeKey = UUString.toSnakeCase(key);
            if (!UUString.areEqual(key, snakeKey))
            {
                result = UUJson.safeGetDoubleObject(json, snakeKey);
            }
        }

        return result;
    }

    static double getDoubleField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetDouble(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetDouble(json, snakeKey);
        }

        return 0.0;
    }

    @Nullable
    static ArrayList<String> getStringArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfStrings(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfStrings(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Long> getLongArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfLongs(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfLongs(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Integer> getIntegerArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfIntegers(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfIntegers(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Short> getShortArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfShorts(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfShorts(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Byte> getByteArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfBytes(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfBytes(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Double> getDoubleArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfDoubles(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfDoubles(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Float> getFloatArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfFloats(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfFloats(json, key);
        }

        return null;
    }

    @Nullable
    static ArrayList<Boolean> getBoolArrayField(final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfBooleans(json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfBooleans(json, key);
        }

        return null;
    }

    @Nullable
    static <T extends UUJsonConvertible> ArrayList<T> getJsonConvertibleArrayField(@NonNull final Class<T> type, final JSONObject json, final String key)
    {
        if (UUJson.hasNonNullValueForKey(json, key))
        {
            return UUJson.safeGetArrayOfObjects(type, json, key);
        }

        String snakeKey = UUString.toSnakeCase(key);
        if (!UUString.areEqual(key, snakeKey))
        {
            return UUJson.safeGetArrayOfObjects(type, json, key);
        }

        return null;
    }

    HashMap<Class<?>, Field[]> cachedFields = new HashMap<>();

    static Field[] getFields(@NonNull Class<?> tableClass)
    {
        if (cachedFields.containsKey(tableClass))
        {
            return cachedFields.get(tableClass);
        }

        Field[] fields = tableClass.getDeclaredFields();
        for (Field field: fields)
        {
            field.setAccessible(true);
        }

        cachedFields.put(tableClass, fields);
        return fields;
    }

    HashMap<Class<?>, ArrayList<Pair<Field, UUJsonField>>> cachedAnnotatedFields = new HashMap<>();

    static ArrayList<Pair<Field, UUJsonField>> getAnnotatedFields(@NonNull Class<?> tableClass)
    {
        if (cachedAnnotatedFields.containsKey(tableClass))
        {
            return cachedAnnotatedFields.get(tableClass);
        }

        Field[] fields = getFields(tableClass);
        ArrayList<Pair<Field, UUJsonField>> tmp = new ArrayList<>();
        for (Field field : fields)
        {
            UUJsonField annotation = field.getAnnotation(UUJsonField.class);
            if (annotation != null)
            {
                tmp.add(new Pair<>(field, annotation));
            }
        }

        cachedAnnotatedFields.put(tableClass, tmp);
        return tmp;
    }

    @NonNull
    default JSONObject toJsonObject()
    {
        JSONObject json = new JSONObject();

        try
        {
            ArrayList<Pair<Field, UUJsonField>> annotatedFields = getAnnotatedFields(getClass());
            for (Pair<Field, UUJsonField> annotatedField: annotatedFields)
            {
                Field field = annotatedField.first;
                UUJsonField annotation = annotatedField.second;

                /*
                if (version >= columnAnnotation.existsInVersion())
                {
                    Object fieldVal = field.get(this);
                    if (shouldPutColumn(columnAnnotation, fieldVal))
                    {
                        //UUContentValues.putObject(cv, columnNameForField(field), fieldVal);
                    }
                }*/

                Object fieldVal = field.get(this);
                UUJson.safePut(json, field.getName(), fieldVal);
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "toJsonObject", ex);
        }

        return json;
    }

    /*
    HashMap<Field, String> cachedFieldNames = new HashMap<>();

    @NonNull
    static String getJsonFieldName(@NonNull final Field field, @Nullable UUJsonField annotation)
    {
        String fieldName = cachedFieldNames.get(field);
        if (fieldName != null)
        {
            return fieldName;
        }

        if (annotation == null)
        {
            annotation = field.getAnnotation(UUJsonField.class);
        }

        if (annotation != null)
        {
            fieldName = annotation.name();
        }

        if (UUString.isEmpty(fieldName))
        {
            fieldName = UUString.toSnakeCase(field.getName());
        }

        cachedFieldNames.put(field, fieldName);
        return fieldName;
    }*/

    default void fillFromJsonReader(@NonNull final JsonReader jsonReader)
    {

    }

    default void writeJsonToParcel(Parcel dest, int flags)
    {
        dest.writeString(toJsonObject().toString());
    }

    default void fillJsonFromParcel(final Parcel in)
    {
        try
        {
            String jsonString = in.readString();
            JSONObject json = new JSONObject(jsonString);
            fillFromJson(json);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "fillFromParcel", ex);
        }
    }
}