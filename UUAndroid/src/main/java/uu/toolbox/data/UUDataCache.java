package uu.toolbox.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import uu.toolbox.core.UUFile;
import uu.toolbox.core.UUJson;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

public class UUDataCache implements UUDataCacheProtocol
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final long DEFAULT_CONTENT_EXPIRATION_SECONDS = (long)(60L * 60L * 24L * 30L * 1000L); // 30 days

    public static class MetaData
    {
        public static final String Timestamp = "Timestamp";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Singleton Interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static UUDataCache theSharedInstance;

    public static void init(final Context context)
    {
        theSharedInstance = new UUDataCache(context, defaultCacheFolder(context), DEFAULT_CONTENT_EXPIRATION_SECONDS);
    }

    public static synchronized UUDataCache sharedInstance()
    {
        return theSharedInstance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private long contentExpirationLength = DEFAULT_CONTENT_EXPIRATION_SECONDS;
    private File cacheFolder = null;
    private UUDataCacheDb metaDataDb;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public UUDataCache(@NonNull final Context context,
                       @NonNull final File cacheLocation,
                       final long contentExpiration)
    {
        cacheFolder = cacheLocation;
        contentExpirationLength = contentExpiration;
        UUFile.createFoldersIfNeeded(cacheFolder);
        metaDataDb = new UUDataCacheDb(context);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataCacheProtocol interface
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public byte[] getData(@NonNull String key)
    {
        removeIfExpired(key);

        byte[] cached = loadFromCache(key);
        if (cached != null)
        {
            return cached;
        }

        return loadFromDisk(key);
    }

    @Override
    public void setData(@NonNull byte[] data, @NonNull String key)
    {
        saveToDisk(data, key);
        saveToCache(data, key);

        HashMap<String, Object> md = getMetaData(key);
        md.put(MetaData.Timestamp, System.currentTimeMillis());
        setMetaData(md, key);
    }

    @NonNull
    @Override
    public HashMap<String, Object> getMetaData(@NonNull String key)
    {
        return metaDataDb.getMetaData(key);
    }

    @Override
    public void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull String key)
    {
        metaDataDb.setMetaData(key, metaData);
    }

    @Override
    public boolean doesDataExist(@NonNull String key)
    {
        try
        {
            if (UUString.isNotEmpty(key))
            {
                File file = getDiskFileName(key);
                return file.exists();
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "doesDataExist", ex);
        }

        return false;
    }

    @Override
    public boolean isDataExpired(@NonNull String key)
    {
        HashMap<String, Object> md = getMetaData(key);
        Object timestamp = md.get(MetaData.Timestamp);
        if (timestamp != null && timestamp instanceof Long)
        {
            long actualTimestamp = (Long)timestamp;
            long elapsed = System.currentTimeMillis() - actualTimestamp;
            return (elapsed > contentExpirationLength);
        }

        return false;
    }

    @Override
    public void removeData(@NonNull String key)
    {
        removeAllMetaData(key);
        removeFileFromCache(key);
        removeFileFromDisk(key);
    }

    @Override
    public void clearCache()
    {
        clearDiskCache();
        clearMemoryCache();

        metaDataDb.clearAllMetaData();
    }

    @Override
    public void purgeExpiredData()
    {
        ArrayList<String> keys = listKeys();

        for (String key : keys)
        {
            removeIfExpired(key);
        }
    }

    @Override
    public long getDataExpirationInterval()
    {
        return contentExpirationLength;
    }

    @Override
    public void setDataExpirationInterval(long interval)
    {
        contentExpirationLength = interval;
    }

    @NonNull
    @Override
    public ArrayList<String> listKeys()
    {
        ArrayList<String> results = new ArrayList<>();

        try
        {
            if (cacheFolder != null)
            {
                File[] list = cacheFolder.listFiles();
                for (File f : list)
                {
                    results.add(f.getName());
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "listKeys", ex);
            results = new ArrayList<>();
        }

        return results;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Implementation
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static @NonNull File defaultCacheFolder(@NonNull final Context context)
    {
        return new File(context.getCacheDir(), "UUDataCache");
    }

    @NonNull
    private String sanitizeKey(@NonNull final String key)
    {
        return key.replaceAll("[^A-Za-z0-9]", "-");
    }

    @NonNull
    public File getDiskFileName(@NonNull final String key)
    {
        return new File(cacheFolder, sanitizeKey(key));
    }

    private void removeIfExpired(@NonNull final String key)
    {
        if (isDataExpired(key))
        {
            removeData(key);
        }
    }

    @Nullable
    private byte[] loadFromDisk(@NonNull final String key)
    {
        byte[] data = null;

        try
        {
            File pathUrl = getDiskFileName(key);
            data = UUFile.readFile(pathUrl);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "loadFromDisk", ex);
        }

        return data;
    }

    @Nullable
    private byte[] loadFromCache(@NonNull final String key)
    {
        //return dataCache.object(forKey: key as NSString) as Data?

        // TODO: Implement in memory cache
        return null;
    }

    private void removeFileFromDisk(@NonNull String key)
    {
        try
        {
            File pathUrl = getDiskFileName(key);
            UUFile.deleteFile(pathUrl);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "removeFileFromDisk", ex);
        }
    }

    private void removeFileFromCache(@NonNull String key)
    {
        try
        {
            // TODO: Implement remove from cache
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "removeFileFromCache", ex);
        }
    }

    private void removeAllMetaData(@NonNull String key)
    {
        try
        {
            //public static void destroy(final Context context, final Class<? extends UUDatabase> dbClass)
            // TODO: Implement remove all meta data
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "removeAllMetaData", ex);
        }
    }

    private void saveToDisk(@NonNull byte[] data, @NonNull String key)
    {
        try
        {
            File pathUrl = getDiskFileName(key);
            UUFile.writeFile(pathUrl, data);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "saveToDisk", ex);
        }
    }

    private void saveToCache(@NonNull byte[] data, @NonNull String key)
    {
        //dataCache.setObject(data as NSData, forKey: key as NSString)

        // TODO: Implement memory cache
    }

    private void clearDiskCache()
    {
        try
        {
            UUFile.deleteFile(cacheFolder);
            UUFile.createFoldersIfNeeded(cacheFolder);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "clearDiskCache", ex);
        }
    }

    private void clearMemoryCache()
    {
        /*try
        {
            UUFile.deleteFile(cacheFolder);
            UUFile.createFoldersIfNeeded(cacheFolder);
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "clearDiskCache", ex);
        }*/
    }


    @UUSqlTable(tableName = "uu_data_cache_meta_data")
    public static class UUDataCacheMetaData implements UUDataModel
    {
        @UUSqlColumn(name = "name", type = UUSqlColumn.Type.TEXT, primaryKey = true)
        private String name;

        @UUSqlColumn(name = "timestamp", type = UUSqlColumn.Type.REAL)
        private long timestamp;

        @UUSqlColumn(name = "meta_data", type = UUSqlColumn.Type.TEXT)
        private String metaData;

        @NonNull
        private HashMap<String, Object> explodeMetaData()
        {
            HashMap<String, Object> m = new HashMap<>();

            try
            {
                JSONObject json = new JSONObject(metaData);

                Iterator<String> keys = json.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    Object val = UUJson.safeGet(json, key);
                    m.put(key, val);
                };
            }
            catch (Exception ex)
            {
                UULog.error(getClass(), "explodeMetaData", ex);
                m = new HashMap<>();
            }

            return m;
        }

        private void setMetaDataFromHashMap(@NonNull final HashMap<String, Object> dictionary)
        {
            try
            {
                JSONObject obj = UUJson.toJson(dictionary);
                if (obj != null)
                {
                    metaData = obj.toString();
                }
            }
            catch (Exception ex)
            {
                UULog.error(getClass(), "setMetaDataFromHashMap", ex);
            }
        }
    }

    private static class UUDataCacheDb extends UUDefaultDatabase
    {
//        private static final String DB_NAME = "UUDataCacheDb";
//        private static final int DB_VERSION_ONE = 1;
//
//        private static final int DB_VERSION = DB_VERSION_ONE;

        UUDataCacheDb(@NonNull final Context context)
        {
            super(context, new UUDataCacheDbSchema());
        }

        @NonNull
        private HashMap<String, Object> getMetaData(@NonNull final String key)
        {
            String where = String.format(Locale.US, "%s = ?", "name");
            String[] whereArgs = new String[] { key };

            HashMap<String, Object> result = new HashMap<>();

            UUDataCacheMetaData row = querySingleObject(UUDataCacheMetaData.class, where, whereArgs, null);
            if (row != null)
            {
                result = row.explodeMetaData();
            }

            return result;
        }

        private void setMetaData(@NonNull final String key, @NonNull HashMap<String, Object> metaData)
        {
            UUDataCacheMetaData md = new UUDataCacheMetaData();
            md.name = key;
            md.timestamp = System.currentTimeMillis();
            md.setMetaDataFromHashMap(metaData);

            updateObject(UUDataCacheMetaData.class, md);
        }

        private void clearAllMetaData()
        {
            UUDatabaseDefinition schema = getDatabaseDefinition();
            for (UUDataModel dm : schema.getDataModels(schema.getVersion()))
            {
                truncateTable(dm.getTableName());
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // UUDatabaseDefinition
        ////////////////////////////////////////////////////////////////////////////////////////////////

        /*
        private UUDataCacheDb(@NonNull final Context context)
        {
            super(context);
        }

        @SuppressWarnings("unused")
        public String getDatabaseName()
        {
            return DB_NAME;
        }

        public int getVersion()
        {
            return DB_VERSION;
        }

        public ArrayList<UUDataModel> getDataModels(int version)
        {
            switch (version)
            {
                case DB_VERSION_ONE:
                {
                    return getV1DataModels();
                }

                default:
                {
                    return null;
                }
            }
        }

//        @Override
//        public ArrayList<String> getSqlCreateLines(int version)
//        {
//            return null;
//        }

        public void handlePostOpen(SQLiteDatabase db, final int version)
        {

        }

        public void handlePostCreate(SQLiteDatabase db, final int version)
        {

        }

        public void handlePostUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }

        private ArrayList<UUDataModel> getV1DataModels()
        {
            ArrayList<UUDataModel> list = new ArrayList<>();
            list.add(new UUDataCacheMetaData());
            return list;
        }*/
    }

    @UUSqlDatabase(name = "UUDataCacheDb", models = { UUDataCacheMetaData.class })
    private static class UUDataCacheDbSchema implements UUDatabaseDefinition
    {

    }
}