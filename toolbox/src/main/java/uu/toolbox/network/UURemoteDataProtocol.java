package uu.toolbox.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 *
 * UURemoteDataProtocol defines a lightweight interface for remote fetching of data from either
 * a local data cache or the network.
 *
 */
public interface UURemoteDataProtocol
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
     * Checks to see if a download is currently pending
     *
     * @param key lookup key
     * @return true if a download is pending, false if it is not.
     */
    boolean isDownloadPending(@NonNull final String key);

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
}
