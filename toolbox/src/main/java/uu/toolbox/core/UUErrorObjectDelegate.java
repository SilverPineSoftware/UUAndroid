package uu.toolbox.core;

import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;


/**
 * UUErrorDelegate
 *
 * Useful Utilities - Callback interface used to deliver a UUError object and a result from an async operation.
 *
 */
public interface UUErrorObjectDelegate<T>
{
    void onCompleted(@Nullable final UUError error, @Nullable final T object);

    static <T extends Object> void safeInvoke(@Nullable final UUErrorObjectDelegate<T> delegate, @Nullable final UUError error, @Nullable final T object)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onCompleted(error, object);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUErrorObjectDelegate.class, "safeInvoke", ex);
        }
    }

    static <T extends Object> void safeInvokeOnMainThread(@Nullable final UUErrorObjectDelegate<T> delegate, @Nullable final UUError error, @Nullable final T object)
    {
        UUThread.runOnMainThread(() ->
        {
            safeInvoke(delegate, error, object);
        });
    }
}
