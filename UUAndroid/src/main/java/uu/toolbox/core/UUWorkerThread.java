package uu.toolbox.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

public class UUWorkerThread extends HandlerThread
{
    private static String LOG_TAG = UUWorkerThread.class.getName();

    private Handler handler;

    public UUWorkerThread(@NonNull final String name)
    {
        super(name);
        start();

        Looper looper = getLooper();
        handler = new Handler(looper);
    }

    public void post(final Runnable runnable)
    {
        if (handler != null)
        {
            handler.post(runnable);
        }
        else
        {
            Log.d(LOG_TAG, "Handler is null! unable to post!");
        }
    }

    public void postDelayed(final Runnable runnable, final long delay)
    {
        if (handler != null)
        {
            handler.postDelayed(runnable, delay);
        }
        else
        {
            Log.d(LOG_TAG, "Handler is null! unable to post!");
        }
    }

    public void removeRunnable(final Runnable runnable)
    {
        if (handler != null)
        {
            handler.removeCallbacks(runnable);
        }
        else
        {
            Log.d(LOG_TAG, "Handler is null! unable to remove runnable!");
        }
    }
}
