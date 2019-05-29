package uu.toolbox.core;

import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;


/**
 * UUErrorDelegate
 *
 * Useful Utilities - Callback interface used to deliver a result from an async operation.
 *
 */
public interface UUObjectDelegate<T>
{
    void onCompleted(@Nullable final T object);

    static <T extends Object> void safeInvoke(@Nullable final UUObjectDelegate<T> delegate, @Nullable final T object)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onCompleted(object);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUObjectDelegate.class, "safeInvoke", ex);
        }
    }

    static <T extends Object> void safeInvokeOnMainThread(@Nullable final UUObjectDelegate<T> delegate, @Nullable final T object)
    {
        UUThread.runOnMainThread(() ->
        {
            safeInvoke(delegate, object);
        });
    }
}
