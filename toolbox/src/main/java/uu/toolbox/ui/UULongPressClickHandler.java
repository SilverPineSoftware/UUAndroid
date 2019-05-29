package uu.toolbox.ui;

import android.view.MotionEvent;
import android.view.View;

import uu.toolbox.core.UURandom;
import uu.toolbox.core.UUThread;
import uu.toolbox.core.UUTimer;
import uu.toolbox.logging.UULog;

public class UULongPressClickHandler implements View.OnTouchListener
{
    private long triggerDuration;
    private final String timerId;
    private int lastMotionEvent;

    public UULongPressClickHandler(final long duration)
    {
        this.triggerDuration = duration;
        timerId = UURandom.randomUuidString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        lastMotionEvent = event.getAction();
        UULog.debug(getClass(), "onTouch", "Touch Event: " + lastMotionEvent);

        switch (lastMotionEvent)
        {
            case MotionEvent.ACTION_DOWN:
            {
                if (UUTimer.findActiveTimer(timerId) == null)
                {
                    UUTimer.startTimer(timerId, triggerDuration, null, (timer, userInfo) ->
                    {
                        UUThread.runOnMainThread(v::performClick);
                    });
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
                break;

            default:
            {
                UUTimer.cancelActiveTimer(timerId);
                break;
            }
        }

        return true;
    }

}
