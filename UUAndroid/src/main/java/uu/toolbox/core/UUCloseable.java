package uu.toolbox.core;

import android.support.annotation.Nullable;

import java.io.Closeable;

import uu.toolbox.logging.UULog;

/**
 * Simple helpers for the Closable interface
 */
public class UUCloseable
{
    private UUCloseable() {}

    public static void safeClose(@Nullable final Closeable closeable)
    {
        try
        {
            if (closeable != null)
            {
                closeable.close();
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUCloseable.class, "safeClose", ex);
        }
    }
}
