package uu.toolbox.network;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import uu.toolbox.core.UUDate;
import uu.toolbox.core.UUNonNullObjectDelegate;
import uu.toolbox.core.UUString;
import uu.toolbox.data.UUDataCache;
import uu.toolbox.logging.UULog;

public class UURemoteData implements UURemoteDataProtocol
{
    public static class MetaData extends UUDataCache.MetaData
    {
        public static final String MimeType = "MimeType";
        public static final String DownloadTimestamp = "DownloadTimestamp";
    }

    public class Notifications
    {
        public static final String DataDownloaded = "UUDataDownloadedNotification";
        public static final String DataDownloadFailed = "UUDataDownloadFailedNotification";
    }

    public class NotificationKeys
    {
        public static final String RemotePath = "UUDataRemotePathKey";
        public static final String Error = "UURemoteDataErrorKey";
    }

    public static class DataResponse
    {
        public byte[] data;
        public String mimeType;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Singleton Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static UURemoteData theSharedInstance;

    public static void init(@NonNull final Context context)
    {
        theSharedInstance = new UURemoteData(context);

        if (UUDataCache.sharedInstance() == null)
        {
            UUDataCache.init(context);
        }
    }

    public static void setTheSharedInstance(@NonNull final UURemoteData instance)
    {
        theSharedInstance = instance;
    }

    public static synchronized UURemoteData sharedInstance()
    {
        return theSharedInstance;
    }

    private LocalBroadcastManager broadcastManager;
    private final HashMap<String, Boolean> activeDownloads = new HashMap<>();
    private final Deque<String> queuedDownloadRequests = new ArrayDeque<>();
    private final HashMap<String, Long> lastFailureTimes = new HashMap<>();


    private int maxDownloadRequests = 5;
    private long failedDownloadIgnoreTime = 15 * UUDate.MILLIS_IN_ONE_MINUTE;

    public UURemoteData(@NonNull final Context context)
    {
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Nullable
    @Override
    public byte[] getData(@NonNull final String key)
    {
        try
        {
            if (!checkUrl(key))
            {
                return null;
            }

            byte[] data = UUDataCache.sharedInstance().getData(key);
            if (data != null)
            {
                return data;
            }

            queueDownloadRequest(key);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "getData", ex);
            return null;
        }

        return null;
    }

    protected boolean checkUrl(@NonNull final String key)
    {
        try
        {
            if (UUString.isEmpty(key))
            {
                // Fast fail if the key is empty
                return false;
            }

            // A bad URL with throw here
            new URL(key);
        }
        catch (Exception ex)
        {
            UULog.debug(getClass(), "checkUrl", ex);
            return false;
        }

        return true;
    }

    protected void downloadData(@NonNull final String key, @NonNull final UUNonNullObjectDelegate<DataResponse> delegate)
    {
        UUHttpRequest request = UUHttpRequest.get(key, null);
        request.setResponseParser(null);
        UUHttp.execute(request, response ->
        {
            DataResponse dataResponse = new DataResponse();

            try
            {
                Exception ex = response.getException();
                byte[] rawResponse = response.getRawResponse();

                if (ex == null && rawResponse != null)
                {
                    dataResponse.data = response.getRawResponse();
                    dataResponse.mimeType = response.getContentType();
                }
                else
                {
                    UULog.debug(getClass(),
                        "handleDownloadResponse",
                        String.format(Locale.US, "Remote download failed!\n\nPath: %s\nStatus Code: %d\nError: %s",
                            key, response.getHttpResponseCode(), UUString.safeToString(ex)));
                }
            }
            catch (Exception ex)
            {
                UULog.error(getClass(), " handleDownloadResponse", ex);
            }

            UUNonNullObjectDelegate.safeInvoke(delegate, dataResponse);
        });
    }

    private synchronized void queueDownloadRequest(@NonNull final String key)
    {
        logQueue("Before Push");
        queuedDownloadRequests.remove(key);
        queuedDownloadRequests.push(key);
        logQueue("After Push");

        processDownloadRequests();
    }

    private synchronized void logQueue(@NonNull final String message)
    {
        UULog.debug(getClass(), "logQueue", message + ", There are " + queuedDownloadRequests.size() + " items in the download queue.");

        Iterator<String> iterator = queuedDownloadRequests.descendingIterator();
        int i = 0;

        while (iterator.hasNext())
        {
            String next = iterator.next();
            UULog.debug(getClass(), "logQueue", message + ", " + i + ": " + next);
        }
    }

    private synchronized int numberOfActiveRequests()
    {
        return activeDownloads.size();
    }

    private synchronized void addActiveRequest(@NonNull final String key)
    {
        activeDownloads.put(key, Boolean.TRUE);
    }

    private synchronized void removeActiveRequest(@NonNull final String key)
    {
        activeDownloads.remove(key);
        processDownloadRequests();
    }

    private int getMaxDownloadRequests()
    {
        return maxDownloadRequests;
    }

    public void setMaxDownloadRequests(int maxDownloadRequests)
    {
        this.maxDownloadRequests = maxDownloadRequests;
    }

    @Nullable
    private synchronized String dequeueRequest()
    {
        logQueue("Before Pop");

        String value = null;

        if (queuedDownloadRequests.size() > 0)
        {
            value = queuedDownloadRequests.pop();
        }

        logQueue("After Pop");
        return value;
    }

    private void processDownloadRequests()
    {
        ArrayList<String> skipped = new ArrayList<>();

        while (numberOfActiveRequests() < getMaxDownloadRequests())
        {
            String key = dequeueRequest();
            if (key == null)
            {
                // No more queue'd requests
                break;
            }

            Long lastFailure = lastFailureTime(key);
            if (lastFailure != null)
            {
                long timeSinceLastFailure = System.currentTimeMillis() - lastFailure;
                if (timeSinceLastFailure < getFailedDownloadIgnoreTime())
                {
                    UULog.debug(getClass(), "processDownloadRequests", "key: " + key + ", last failure was " + timeSinceLastFailure + " millis ago, waiting to download.");
                    skipped.add(key);
                    continue;
                }
            }

            if (!isDownloadPending(key))
            {
                addActiveRequest(key);
                downloadData(key, response -> handleDownloadResponse(response, key));
            }
        }

        // Add skipped back in but don't trigger the processing loop
        synchronized (queuedDownloadRequests)
        {
            queuedDownloadRequests.addAll(skipped);

        }
    }

    @Override
    public boolean isDownloadPending(@NonNull final String key)
    {
        synchronized (activeDownloads)
        {
            return activeDownloads.containsKey(key);
        }
    }

    @NonNull
    @Override
    public HashMap<String, Object> getMetaData(@NonNull final String key)
    {
        return UUDataCache.sharedInstance().getMetaData(key);
    }

    @Override
    public void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull final String key)
    {
        UUDataCache.sharedInstance().setMetaData(metaData, key);
    }

    private void handleDownloadResponse(@NonNull final DataResponse response, @NonNull final String key)
    {
        try
        {
            byte[] rawResponse = response.data;

            if (rawResponse != null)
            {
                UUDataCache.sharedInstance().setData(rawResponse, key);
                //updateMetaDataFromResponse(response, key);

                HashMap<String, Object> md = UUDataCache.sharedInstance().getMetaData(key);
                md.put(MetaData.MimeType, response.mimeType);
                md.put(MetaData.DownloadTimestamp, System.currentTimeMillis());
                UUDataCache.sharedInstance().setMetaData(md, key);

                Intent intent = new Intent(Notifications.DataDownloaded);
                intent.putExtra(NotificationKeys.RemotePath, key);

                broadcastManager.sendBroadcast(intent);
            }
            else
            {
                Intent intent = new Intent(Notifications.DataDownloadFailed);
                intent.putExtra(NotificationKeys.RemotePath, key);

                // TODO: Put error info into intent

                broadcastManager.sendBroadcast(intent);

                markDownloadFailed(key);
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "handleDownloadResponse", ex);
        }
        finally
        {
            removeActiveRequest(key);
        }
    }

    public void notifyDataDownloaded(@NonNull final String key)
    {
        try
        {
            HashMap<String, Object> md = UUDataCache.sharedInstance().getMetaData(key);
            md.put(MetaData.MimeType, "raw"); // Is this needed?
            md.put(MetaData.DownloadTimestamp, System.currentTimeMillis());
            UUDataCache.sharedInstance().setMetaData(md, key);

            Intent intent = new Intent(Notifications.DataDownloaded);
            intent.putExtra(NotificationKeys.RemotePath, key);

            broadcastManager.sendBroadcast(intent);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), " notifyDataDownloaded", ex);
        }
    }

    private synchronized void markDownloadFailed(@NonNull final String key)
    {
        lastFailureTimes.put(key, System.currentTimeMillis());
    }

    @Nullable
    private synchronized Long lastFailureTime(@NonNull final String key)
    {
        return lastFailureTimes.get(key);
    }

    public long getFailedDownloadIgnoreTime()
    {
        return failedDownloadIgnoreTime;
    }

    public void setFailedDownloadIgnoreTime(long failedDownloadIgnoreTime)
    {
        this.failedDownloadIgnoreTime = failedDownloadIgnoreTime;
    }

    public synchronized void clearLastFailureTimes()
    {
        lastFailureTimes.clear();
    }

    public synchronized void clearPendingQueue()
    {
        queuedDownloadRequests.clear();
    }
}
