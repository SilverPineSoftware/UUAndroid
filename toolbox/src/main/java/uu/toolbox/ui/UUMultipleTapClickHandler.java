package uu.toolbox.ui;

import android.support.annotation.NonNull;
import android.view.View;

import uu.toolbox.core.UUThread;

/**
 * View click handler that only triggers after an arbitrary number of taps within a certain time period
 */
public class UUMultipleTapClickHandler implements View.OnClickListener
{
    private int tapCount = 0;
    private long consecutiveTapStartTimestamp = 0;

    private long requiredTaps;
    private long tapTimeout;
    private Runnable triggerHandler;

    public UUMultipleTapClickHandler(final int requiredTaps, final long timeout, @NonNull final Runnable triggerHandler)
    {
        this.requiredTaps = requiredTaps;
        this.tapTimeout = timeout;
        this.triggerHandler = triggerHandler;
    }

    @Override
    public void onClick(View v)
    {
        long now = System.currentTimeMillis();
        long lapseSinceStart = now - consecutiveTapStartTimestamp;

        if ((consecutiveTapStartTimestamp == 0) || (lapseSinceStart > tapTimeout))
        {
            consecutiveTapStartTimestamp = now;
            tapCount = 1;
        }
        else
        {
            tapCount ++;
        }

        if (tapCount >= requiredTaps)
        {
            UUThread.runOnMainThread(triggerHandler);
            tapCount = 0;
        }
    }
}
