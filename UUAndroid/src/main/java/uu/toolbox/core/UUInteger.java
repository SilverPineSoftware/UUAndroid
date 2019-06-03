package uu.toolbox.core;

import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;

/**
 * Useful set of methods for manipulating Integers
 *
 */
@SuppressWarnings("unused")
public class UUInteger
{
    /**
     * Safely parses a string into an integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Integer.parseInt, or the default value
     */
    public static int safeParse(@Nullable final String s, final int defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Integer.parseInt(s);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParse", ex);
        }

        return defaultVal;
    }

    /**
     * Safely parses a string into an integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @param radix the number radix to use
     * @return the result of Integer.parseInt, or the default value
     */
    public static int safeParse(@Nullable final String s, final int radix, final int defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Integer.parseInt(s, radix);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParse", ex);
        }

        return defaultVal;
    }

    /**
     * Safely parses a string into an Integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @return the result of Integer.parseInt, or the default value
     */
    @Nullable
    public static Integer safeParseAsInteger(@Nullable final String s, @Nullable final Integer defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Integer.parseInt(s);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParseAsInteger", ex);
        }

        return defaultVal;
    }

    /**
     * Safely parses a string into an integer
     *
     * @param s the string to parse
     * @param defaultVal the default value is an exception is thrown
     * @param radix the number radix to use
     * @return the result of Integer.parseInt, or the default value
     */
    @Nullable
    public static Integer safeParseAsInteger(final String s, final int radix, final Integer defaultVal)
    {
        try
        {
            if (UUString.isNotEmpty(s))
            {
                return Integer.parseInt(s, radix);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUInteger.class, "safeParseAsInteger", ex);
        }

        return defaultVal;
    }

    /**
     * Safely checks two Integer's for equality
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return true if they are equal, false if not
     */
    public static boolean areEqual(final Integer lhs, final Integer rhs)
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
            return (lhs.longValue() == rhs.longValue());
        }
    }

    /**
     * Safely compares two Integer's
     *
     * @param lhs the left hand side to check
     * @param rhs the right hand side to check
     * @return comparison result, -1, 0, or 1
     */
    public static int compare(@Nullable final Integer lhs, @Nullable final Integer rhs)
    {
        int left = lhs != null ? lhs : 0;
        int right = rhs != null ? rhs : 0;

        if (left == right)
        {
            return 0;
        }
        else
        {
            return (left < right) ? -1 : 1;
        }
    }

    /**
     * Evaluates two values to determine if one contains bits from the other
     *
     * @param value the source value to check
     * @param mask the mask of bits to check
     * @return true if the bits are found in the value, false otherwise
     */
    public static boolean isBitSet(final int value, final int mask)
    {
        return ((value & mask) == mask);
    }
}
