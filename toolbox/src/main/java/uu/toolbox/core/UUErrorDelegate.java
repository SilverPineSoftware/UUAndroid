package uu.toolbox.core;

import android.support.annotation.Nullable;

import uu.toolbox.logging.UULog;


/**
 * UUErrorDelegate
 *
 * Useful Utilities - Callback interface used to deliver a UUError object from an async operation.
 *
 */
public interface UUErrorDelegate
{
    void onCompleted(@Nullable final UUError error);

    static void safeInvoke(@Nullable final UUErrorDelegate delegate, @Nullable final UUError error)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onCompleted(error);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUErrorDelegate.class, "safeInvoke", ex);
        }
    }

    static void safeInvokeOnMainThread(@Nullable final UUErrorDelegate delegate, @Nullable final UUError error)
    {
        UUThread.runOnMainThread(() ->
        {
            safeInvoke(delegate, error);
        });
    }
}

