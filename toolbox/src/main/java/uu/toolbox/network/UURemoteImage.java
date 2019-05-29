package uu.toolbox.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.util.Size;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import uu.toolbox.core.UUThread;
import uu.toolbox.data.UUDataCache;

public class UURemoteImage
{
    public static class MetaData extends UURemoteData.MetaData
    {
        public static final String ImageWidth = "ImageWidth";
        public static final String ImageHeight = "ImageHeight";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Singleton Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static UURemoteImage theSharedInstance;

    public static void init(final Context context)
    {
        theSharedInstance = new UURemoteImage(context);

        if (UURemoteData.sharedInstance() == null)
        {
            UURemoteData.init(context);
        }
    }

    public static synchronized UURemoteImage sharedInstance()
    {
        return theSharedInstance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Members
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private MemoryCache memoryCache;

    public UURemoteImage(@NonNull final Context context)
    {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = maxMemory / 8;

        memoryCache = new MemoryCache(cacheSize);
    }

    public void clearCache()
    {
        memoryCache.evictAll();
    }

    @Nullable
    public Bitmap getImage(@NonNull final String key, final boolean skipDownload)
    {
        return getImage(key, null, null, skipDownload);
    }

    @Nullable
    public Bitmap getImage(@NonNull final String key, @Nullable final Integer targetWidth, @Nullable final Integer targetHeight, final boolean skipDownload)
    {
        String photoKey = getPhotoKey(key, targetWidth, targetHeight);

        if (UUDataCache.sharedInstance().doesDataExist(key))
        {
            Bitmap cached = memoryCache.get(photoKey);
            if (cached != null)
            {
                return cached;
            }
            else
            {
                UUThread.runOnBackgroundThread(() ->
                {
                    File file = UUDataCache.sharedInstance().getDiskFileName(key);

                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                    updateMetaDataIfNeeded(key, opt);

                    opt.inJustDecodeBounds = false;
                    opt.inSampleSize = getSampleSize(opt, targetWidth, targetHeight);

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
                    memoryCache.add(photoKey, bitmap);
                    UURemoteData.sharedInstance().notifyDataDownloaded(key);
                });

                return null;
            }
        }
        else if (!skipDownload)
        {
            UURemoteData.sharedInstance().getData(key);
        }

        return null;
    }

    @NonNull
    private String getPhotoKey(@NonNull final String key, @Nullable final Integer targetWidth, @Nullable final Integer targetHeight)
    {
        String dimPart = "";

        if (targetWidth != null && targetHeight != null)
        {
            dimPart = String.format(Locale.US, "_w%d_w%d", targetWidth, targetHeight);
        }

        return String.format(Locale.US, "%s%s", key, dimPart);
    }

    private int getSampleSize(@NonNull BitmapFactory.Options bmOptions, @Nullable final Integer targetWidth, @Nullable final Integer targetHeight)
    {
        int scaleFactor = 1;

        if (targetWidth != null && targetHeight != null)
        {
            scaleFactor = Math.min(bmOptions.outWidth / targetWidth, bmOptions.outHeight / targetHeight);
        }

        return scaleFactor;
    }

    private void updateMetaDataIfNeeded(@NonNull final String key, BitmapFactory.Options opt)
    {
        HashMap<String, Object> md = getMetaData(key);

        int w = -1;
        int h = -1;
        if (md.containsKey(MetaData.ImageWidth) &&
            md.containsKey(MetaData.ImageHeight))
        {
            Object wo = md.get(MetaData.ImageWidth);
            Object ho = md.get(MetaData.ImageHeight);
            if (wo != null && ho != null && wo instanceof Integer && ho instanceof Integer)
            {
                w = (Integer)wo;
                h = (Integer)ho;
            }
        }

        if (w != opt.outWidth || h != opt.outHeight)
        {
            md.put(MetaData.ImageWidth, opt.outWidth);
            md.put(MetaData.ImageHeight, opt.outHeight);
            setMetaData(md, key);
        }
    }

    public boolean isDownloadPending(@NonNull final String key)
    {
        return UURemoteData.sharedInstance().isDownloadPending(key);
    }

    public HashMap<String, Object> getMetaData(@NonNull final String key)
    {
        return UURemoteData.sharedInstance().getMetaData(key);
    }

    public void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull final String key)
    {
        UURemoteData.sharedInstance().setMetaData(metaData, key);
    }

    @Nullable
    public Size imageSize(@NonNull final String key)
    {
        Size size = null;

        HashMap<String, Object> md = getMetaData(key);

        if (md.containsKey(MetaData.ImageWidth) &&
            md.containsKey(MetaData.ImageHeight))
        {
            Object wo = md.get(MetaData.ImageWidth);
            Object ho = md.get(MetaData.ImageHeight);
            if (wo instanceof Integer && ho instanceof Integer)
            {
                size = new Size((Integer)wo, (Integer)ho);
            }
        }

        return size;
    }


    static class MemoryCache extends LruCache<String, Bitmap>
    {
        MemoryCache(final int cacheSize)
        {
            super(cacheSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value)
        {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return value.getByteCount() / 1024;
        }

        void add(@NonNull final String key, @NonNull final Bitmap bitmap)
        {
            if (get(key) == null)
            {
                put(key, bitmap);
            }
        }
    }
}