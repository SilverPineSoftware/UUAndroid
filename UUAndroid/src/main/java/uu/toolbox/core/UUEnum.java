package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UUEnum
{
    @NonNull
    public static <T extends Enum<T>> T fromString(@NonNull final Class<T> type, @Nullable final String string, @NonNull T defaultValue)
    {
        T val = fromString(type, string);
        if (val == null)
        {
            val = defaultValue;
        }

        return val;
    }

    @Nullable
    public static <T extends Enum<T>> T fromString(@NonNull final Class<T> type, @Nullable final String string)
    {
        if (string == null)
        {
            return null;
        }

        if (!type.isEnum())
        {
            return null;
        }

        try
        {
            return Enum.valueOf(type, string);
        }
        catch (Exception ex)
        {
            return null;
            // Eat it
        }
    }
}
