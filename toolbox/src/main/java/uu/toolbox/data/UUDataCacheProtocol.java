package uu.toolbox.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * UUDataCacheProtocol defines a lightweight interface for caching of data
 * along with a meta data dictionary about each blob of data.
 *
 */
public interface UUDataCacheProtocol
{
    /**
     * Fetches data from the cache
     *
     * @param key lookup key
     * @return data from the cache, or null if the object does not exist.
     */
    @Nullable
    byte[] getData(@NonNull final String key);

    /**
     * Stores data in the cache
     *
     * @param data the data to cache
     * @param key lookup key
     */
    void setData(@NonNull byte[] data, @NonNull final String key);

    /**
     * Gets meta data associated with an object
     *
     * @param key lookup key
     * @return hash map of meta data for a given object
     */
    @NonNull
    HashMap<String, Object> getMetaData(@NonNull final String key);

    /**
     * Sets the meta data associated with an object
     *
     * @param metaData the meta data to set
     * @param key lookup key
     */
    void setMetaData(@NonNull HashMap<String, Object> metaData, @NonNull final String key);

    /**
     * Checks to see if data exists for a given key
     *
     * @param key lookup key
     * @return true if the object exists, false if it does not
     */
    boolean doesDataExist(@NonNull final String key);

    /**
     * Checks to see if data is expired for a given key
     *
     * @param key lookup key
     * @return true if the object is expired, false if is not
     */
    boolean isDataExpired(@NonNull final String key);

    /**
     * Removes data and meta data from the cache
     *
     * @param key lookup key
     */
    void removeData(@NonNull final String key);

    /**
     * Clears all data from the cache
     */
    void clearCache();

    /**
     * Remvoes all expired content from the cache
     */
    void purgeExpiredData();

    /**
     * Gets the object expiration interval
     *
     * @return expiration interval in milliseconds
     */
    long getDataExpirationInterval();

    /**
     * Sets the object expiration interval
     *
     * @param interval a value in milliseconds
     */
    void setDataExpirationInterval(final long interval);

    /**
     * Gets a list of all keys with data in the cache
     *
     * @return a list of string lookup keys
     */
    @NonNull
    ArrayList<String> listKeys();
}
