package uu.toolbox.core;

import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Floats
 *
 */
@SuppressWarnings("unused")
public class UUFloat
{
    /**
     * Safely parses a string into a float
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Float.parseFloat, or the default value
     */
    public static float safeParse(@Nullable final String s, final float defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Float.parseFloat(s);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUFloat.class, "safeParse", ex);
        }

        return defaultVal;
    }

    /**
     * Safely parses a string into a float
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Float.parseFloat, or the default value
     */
    @Nullable
    public static Float safeParseAsFloat(@Nullable final String s, @Nullable final Float defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Float.parseFloat(s);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUFloat.class, "safeParseAsFloat", ex);
        }

        return defaultVal;
    }

    /**
     * Safely checks two Float's for equality
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return true if they are equal, false if not
     */
    public static boolean areEqual(@Nullable final Float lhs, @Nullable final Float rhs)
    {
        if (lhs == null && rhs == null)
        {
            return true;
        }
        else if (lhs == null || rhs == null)
        {
            return false;
        }
        else
        {
            return (lhs.floatValue() == rhs.floatValue());
        }
    }

    /**
     * Safely compares two Float's
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return comparison result, -1, 0, or 1
     */
    public static int compare(@Nullable final Float lhs, @Nullable final Float rhs)
    {
        float left = lhs != null ? lhs : 0.0f;
        float right = rhs != null ? rhs : 0.0f;

        if (left == right)
        {
            return 0;
        }
        else
        {
            return (left < right) ? -1 : 1;
        }
    }
}
