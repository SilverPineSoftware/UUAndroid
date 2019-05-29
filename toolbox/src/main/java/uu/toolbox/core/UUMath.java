package uu.toolbox.core;

/**
 * Useful set of math helper methods
 *
 */
@SuppressWarnings("unused")
public class UUMath
{
    public static final double METERS_PER_KILOMETER = 1000;
    public static final double KILOMETERS_PER_MILE = 1.6093440;

    public static double metersToMiles(final double meters)
    {
        return kilometersToMiles(meters / METERS_PER_KILOMETER);
    }

    public static double kilometersToMiles(final double kilometers)
    {
        return (kilometers / KILOMETERS_PER_MILE);
    }
}
