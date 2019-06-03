package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import uu.toolbox.logging.UULog;

/**
 * A simple named timer that wraps Timer and TimerTask
 */
@SuppressWarnings("unused")
public class UUTimer
{
    private static final boolean LOGGING_ENABLED = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Interfaces
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delegate callback when a timer fires
     */
    public interface TimerDelegate
    {
        /**
         * Delegate method invoked when a timer fires
         *
         * @param timer the timer
         * @param userInfo user info if supplied when starting timer
         */
        void onTimer(@NonNull UUTimer timer, @Nullable Object userInfo);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Data Members
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private @NonNull String timerId = "";
    private @Nullable Object userInfo = null;
    private @Nullable TimerDelegate timerDelegate = null;
    private long interval = 0;
    private boolean repeat = false;
    private @Nullable Runnable runnable;
    private long lastFireTime = 0;

    private static final @NonNull HashMap<String, UUTimer> theActiveTimers = new HashMap<>();

    private static UUWorkerThread workerThread = new UUWorkerThread("UUTimer");

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Data Accessors
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the timer ID
     *
     * @return string timer ID
     */
    public @NonNull String getTimerId()
    {
        return timerId;
    }

    /**
     * Gets the user info associated with the timer
     *
     * @return object
     */
    public @Nullable Object getUserInfo()
    {
        return userInfo;
    }

    /**
     * Gets the last time this timer was fired
     *
     * @return a long
     */
    public long getLastFireTime()
    {
        return lastFireTime;
    }

    /**
     * Gets the interval
     *
     * @return number of milliseconds
     */
    public long getInterval()
    {
        return interval;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a timer.
     *
     * @param timerId timer ID. it is up to callers to enforce unique ID's
     * @param interval timer interval in milliseconds
     * @param repeat whether the timer repeats or fires once.
     * @param userInfo optional user info to pass along with the timer delegate
     * @param delegate the timer callback
     */
    public UUTimer(
        final @NonNull String timerId,
        final long interval,
        final boolean repeat,
        final @Nullable Object userInfo,
        final @Nullable TimerDelegate delegate)
    {
        this.timerId = timerId;
        this.interval = interval;
        this.repeat = repeat;
        this.userInfo = userInfo;
        this.timerDelegate = delegate;

        this.runnable = new Runnable()
        {
            @Override
            public void run()
            {
                safeInvokeRun();
            }
        };
    }

    /**
     * Starts the timer
     */
    public void start()
    {
        addTimer(this);
        safeStartTimer();
    }

    /**
     * Cancels the timer
     */
    public void cancel()
    {
        safeCancelTimer();
        runnable = null;
        removeTimer(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void safeStartTimer()
    {
        try
        {
            if (runnable != null)
            {
                lastFireTime = System.currentTimeMillis();

                if (LOGGING_ENABLED)
                {
                    UULog.debug(getClass(), "safeStartTimer." + timerId, "interval: " + interval + ", expectedFireTime: " + UUDate.formatDate(System.currentTimeMillis() + interval, UUDate.RFC_3999_DATE_TIME_WITH_MILLIS_FORMAT, TimeZone.getDefault()));
                }

                workerThread.postDelayed(runnable, interval);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "safeStartTimer", ex);
        }
    }

    private void safeCancelTimer()
    {
        try
        {
            if (LOGGING_ENABLED)
            {
                UULog.debug(getClass(), "safeCancelTimer." + timerId, "Runnable is " + (runnable != null ? "not null" : "null"));
            }

            if (runnable != null)
            {
                workerThread.removeRunnable(runnable);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "safeCancelTimer", ex);
        }
    }

    private void handlerTimerFired()
    {
        try
        {
            if (timerDelegate != null)
            {
                timerDelegate.onTimer(this, userInfo);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "handlerTimerFired", ex);
        }
        finally
        {
            if (!repeat)
            {
                cancel();
            }
        }
    }

    private void safeInvokeRun()
    {
        try
        {
            if (LOGGING_ENABLED)
            {
                long timeSinceFired = System.currentTimeMillis() - lastFireTime;
                double percentDiff = ((double) interval - (double) timeSinceFired) / (double) interval;

                UULog.debug(getClass(), "safeInvokeRun",
                    "timerId: " + timerId +
                    ", isMainThread: " + UUThread.isMainThread() +
                    ", fired with diff: " + percentDiff +
                    ", ExpectedInterval: " + interval +
                    ", ActualInterval: " + timeSinceFired);
            }

            handlerTimerFired();

            if (repeat)
            {
                safeStartTimer();
            }
            else
            {
                safeCancelTimer();
            }
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "safeInvokeRun", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Class Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static void addTimer(final @NonNull UUTimer timer)
    {
        try
        {
            synchronized (theActiveTimers)
            {
                theActiveTimers.put(timer.getTimerId(), timer);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUTimer.class, "addTimer", ex);
        }
    }

    private static void removeTimer(final @NonNull UUTimer timer)
    {
        try
        {
            synchronized (theActiveTimers)
            {
                theActiveTimers.remove(timer.getTimerId());
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUTimer.class, "removeTimer", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Class Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Finds an active timer by ID
     *
     * @param timerId the timer ID to lookup
     *
     * @return a timer or null if not found
     */
    public static @Nullable UUTimer findActiveTimer(final @NonNull String timerId)
    {
        synchronized (theActiveTimers)
        {
            return theActiveTimers.get(timerId);
        }
    }

    /**
     * Lists all active timers
     *
     * @return a list of UUTimer's
     */
    public static @NonNull ArrayList<UUTimer> listActiveTimers()
    {
        synchronized (theActiveTimers)
        {
            ArrayList<UUTimer> copy = new ArrayList<>();
            for (UUTimer t : theActiveTimers.values())
            {
                copy.add(t);
            }

            return copy;
        }
    }

    /**
     * Fires a named timer
     *
     * @param timerId timer ID
     * @param timeoutMilliseconds timout in milliseconds
     * @param userInfo optional user context
     * @param delegate timer callback
     */
    public static void startTimer(
        final @NonNull String timerId,
        final long timeoutMilliseconds,
        final @Nullable Object userInfo,
        final @NonNull TimerDelegate delegate)
    {
        cancelActiveTimer(timerId);

        if (timeoutMilliseconds > 0)
        {
            UUTimer t = new UUTimer(timerId, timeoutMilliseconds, false, userInfo, delegate);
            t.start();
        }
    }

    /**
     * Cancels a named timer
     *
     * @param timerId timer ID
     */
    public static void cancelActiveTimer(final @NonNull String timerId)
    {
        UUTimer timer = findActiveTimer(timerId);
        if (timer != null)
        {
            timer.cancel();
        }
    }
}
