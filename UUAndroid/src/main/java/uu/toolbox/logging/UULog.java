package uu.toolbox.logging;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import uu.toolbox.BuildConfig;
import uu.toolbox.core.UUWorkerThread;

/**
 * Simple logging wrapper.  Set LOGGING_ENABLED to false to turn off all Logging.  This is useful
 * when you want to disable all Log output for release builds in one common location.
 */
public class UULog
{
    public static final boolean LOGGING_ENABLED = BuildConfig.DEBUG;
    private static final String NEW_LINE = "\n";
    private static final String LOG_TAG = "UULog";
    private static final int LINE_LENGTH = 4000;

    private static UUWorkerThread workerThread = new UUWorkerThread("UULog");

    private UULog()
    {
    }

    public synchronized static void error(final Class callingClass, final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.ERROR, tag, method, exception);
        }
    }

    public synchronized static void error(final Class callingClass, final String method, final String message, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.ERROR, tag, method + ": " + message, exception);
        }
    }

    public static void debug(final Class callingClass, final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.DEBUG, tag, method + ": " + message);
        }
    }

    public synchronized static void debug(final Class callingClass, final String method, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.DEBUG, tag, method, exception);
        }
    }

    public synchronized static void debug(final Class callingClass, final String method, final String message, final Throwable exception)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.DEBUG, tag, method + ": " + message, exception);
        }
    }

    public static void warn(final Class callingClass, final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.WARN, tag, method + ": " + message);
        }
    }

    public static void error(final Class callingClass, final String method, final String message)
    {
        if (LOGGING_ENABLED)
        {
            String tag = callingClass.getName();
            writeToLog(Log.ERROR, tag, method + ": " + message);
        }
    }

    public static void logIntent(final Class callingClass, final String method, final String message, final Intent intent)
    {
        if (LOGGING_ENABLED)
        {
            if (intent != null)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(message);
                sb.append(NEW_LINE);

                String action = intent.getAction();
                sb.append("Action: " + (action != null ? action : "null"));
                sb.append(NEW_LINE);

                Uri data = intent.getData();
                sb.append(("Data: " + (data != null ? data : "null")));
                sb.append(NEW_LINE);

                String scheme = intent.getScheme();
                sb.append(("Scheme: " + (scheme != null ? scheme : "null")));
                sb.append(NEW_LINE);

                sb.append("Categories: ");
                Set<String> categories = intent.getCategories();
                if (categories != null)
                {
                    sb.append(categories.size());
                    sb.append(NEW_LINE);

                    for (String c : categories)
                    {
                        sb.append(c);
                        sb.append(NEW_LINE);
                    }
                }
                else
                {
                    sb.append("null");
                    sb.append(NEW_LINE);
                }

                sb.append("Extras: ");
                Bundle b = intent.getExtras();
                if (b != null)
                {
                    Set<String> keys = b.keySet();
                    if (keys != null)
                    {
                        sb.append(keys.size());
                        sb.append(NEW_LINE);

                        for (String key : keys)
                        {
                            Object val = b.get(key);
                            sb.append(key);
                            sb.append(" => ");

                            if (val != null)
                            {
                                sb.append(val.toString());
                                sb.append(" (" + val.getClass().toString() + ")");
                                sb.append(NEW_LINE);
                            }
                            else
                            {
                                sb.append("null");
                                sb.append(NEW_LINE);
                            }
                        }
                    }
                }
                else
                {
                    sb.append("null");
                    sb.append(NEW_LINE);
                }

                debug(callingClass, method, sb.toString());
            }
        }
    }

    public static void logDisplayMetrics(final Class callingClass, final String method, final String message, @NonNull final Context applicationContext)
    {
        if (LOGGING_ENABLED)
        {
            try
            {
                DisplayMetrics dm = applicationContext.getResources().getDisplayMetrics();

                StringBuilder sb = new StringBuilder();
                sb.append(message);

                sb.append(NEW_LINE);
                sb.append("density: ");
                sb.append(dm.density);

                sb.append(NEW_LINE);
                sb.append("densityDpi: ");
                sb.append(dm.densityDpi);

                sb.append(NEW_LINE);
                sb.append("scaledDensity: ");
                sb.append(dm.scaledDensity);

                sb.append(NEW_LINE);
                sb.append("heightPixels: ");
                sb.append(dm.heightPixels);

                sb.append(NEW_LINE);
                sb.append("widthPixels: ");
                sb.append(dm.widthPixels);

                sb.append(NEW_LINE);
                sb.append("xdpi: ");
                sb.append(dm.xdpi);

                sb.append(NEW_LINE);
                sb.append("ydpi: ");
                sb.append(dm.ydpi);

                debug(callingClass, method, sb.toString());
            }
            catch (Exception ex)
            {
                Log.e(LOG_TAG, "Error logging display metrics", ex);
            }
        }
    }

    private static void writeToLog(int level, String tag, String logLine)
    {
        writeToLog(level, tag, logLine, null);
    }

    private static void writeToLog(final int level, final String tag, final String logLine, final Throwable exception)
    {
        postRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                threadDoLogWrite(level, tag, logLine, exception);
            }
        });
    }

    private static void postRunnable(final Runnable runnable)
    {
        try
        {
            workerThread.post(runnable);
        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, "Error posting runnable", ex);
        }
    }

    private static void threadDoLogWrite(int level, String tag, String logLine, Throwable exception)
    {
        try
        {
            if (exception != null)
            {
                logLine += ", Exception: " + stackTraceToString(exception);
            }

            int expectedToWrite = logLine.length();
            int totalWritten = 0;


            while (totalWritten < expectedToWrite)
            {
                String chunk = logLine.substring(totalWritten, Math.min(totalWritten + LINE_LENGTH, logLine.length()));
                int bytesWritten = Log.println(level, tag, chunk);

                // On some devices, logging seems to fail and return zero.  In this case, we have to just
                // abort and let the app keep running.
                if (bytesWritten <= 0)
                {
                    break;
                }

                totalWritten += chunk.length();
            }
        }
        catch (Exception ex)
        {
            Log.e(LOG_TAG, "Error writing to log", ex);
        }
    }

    @NonNull
    public static String stackTraceToString(@Nullable final Throwable throwable)
    {
        try
        {
            if (throwable != null)
            {
                StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                return sw.toString();
            }
        }
        catch (Throwable t)
        {
            // Eat it
        }

        try
        {
            if (throwable != null)
            {
                return throwable.toString();
            }
        }
        catch (Throwable t)
        {
            // Eat it
        }


        return "";
    }
}
