package uu.toolbox.network;

/**
 * Created by ryandevore on 9/14/16.
 */
public enum UUContentEncoding
{
    UTF8            ("UTF-8");

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String stringVal;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    UUContentEncoding(final String val)
    {
        stringVal = val;
    }

    public final String stringVal()
    {
        return stringVal;
    }

    protected static UUContentEncoding fromString(final String val)
    {
        UUContentEncoding[] list = UUContentEncoding.values();
        for (UUContentEncoding e : list)
        {
            if (e.stringVal().equalsIgnoreCase(val))
            {
                return e;
            }
        }

        return null;
    }
}
