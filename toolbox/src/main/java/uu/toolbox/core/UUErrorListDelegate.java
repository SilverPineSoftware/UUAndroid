package uu.toolbox.core;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import uu.toolbox.logging.UULog;


/**
 * UUErrorListDelegate
 *
 * Useful Utilities - Callback interface used to deliver a UUError object and a result list from an async operation.
 *
 */
public interface UUErrorListDelegate<T>
{
    void onCompleted(@Nullable final UUError error, @Nullable final ArrayList<T> list);

    static <T extends Object> void safeInvoke(final UUErrorListDelegate<T> delegate, @Nullable final UUError error, @Nullable final ArrayList<T> list)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onCompleted(error, list);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUErrorListDelegate.class, "safeInvoke", ex);
        }
    }

    static <T extends Object> void safeInvokeOnMainThread(final UUErrorListDelegate<T> delegate, @Nullable final UUError error, @Nullable final ArrayList<T> list)
    {
        UUThread.runOnMainThread(() ->
        {
            safeInvoke(delegate, error, list);
        });
    }
}
