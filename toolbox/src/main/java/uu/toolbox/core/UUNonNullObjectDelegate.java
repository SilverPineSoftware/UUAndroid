package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;


/**
 * UUErrorDelegate
 *
 * Useful Utilities - Callback interface used to deliver a result from an async operation.
 *
 */
public interface UUNonNullObjectDelegate<T>
{
    void onCompleted(@NonNull final T object);

    static <T extends Object> void safeInvoke(@Nullable final UUNonNullObjectDelegate<T> delegate, @NonNull final T object)
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
            UULog.error(UUNonNullObjectDelegate.class, "safeInvoke", ex);
        }
    }

    static <T extends Object> void safeInvokeOnMainThread(@Nullable final UUNonNullObjectDelegate<T> delegate, @NonNull final T object)
    {
        UUThread.runOnMainThread(() ->
        {
            safeInvoke(delegate, object);
        });
    }
}
