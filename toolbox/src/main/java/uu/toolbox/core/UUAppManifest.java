package uu.toolbox.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * UUAppManifest
 * 
 * Useful Utilities - A set of extension methods for reading meta data from the AndroidManifest.xml file
 *  
 */
public final class UUAppManifest 
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	///////////////////////////////////////////////////////////////////////////////////////////////
	private static final String LOG_TAG = UUAppManifest.class.getName();
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Public helpers
	///////////////////////////////////////////////////////////////////////////////////////////////
    
	/**
     * Reads a string setting from the application manifest
     * 
     * @param context application context
     * @param key name of the value to lookup
     * @return a string from the manifest file, or an empty string if an error occurs
     */
    public static final String getManifestString(final Context context, final String key)
    {
    	try
    	{
	    	ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
	    	if (appInfo != null && appInfo.metaData != null)
	    	{
	    		return appInfo.metaData.getString(key);
	    	}
	    	else
	    	{
	    		Log.w(LOG_TAG, "Unable to read string for key=" + key);
	    		return "";
	    	}
    	}
    	catch (Exception ex)
    	{
    		Log.e(LOG_TAG, "Error getting manifest string for key=" + key + ", ex=" + ex.toString());
    		return "";
    	}
    }
    
    /**
     * Reads a integer setting from the application manifest
     * 
     * @param context application context
     * @param key name of the value to lookup
     * @return a string from the manifest file, or an empty string if an error occurs
     */
    public static final int getManifestInt(final Context context, final String key)
    {
    	try
    	{
	    	ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
	    	
	    	if (appInfo != null && appInfo.metaData != null)
	    	{
	    		return appInfo.metaData.getInt(key);
	    	}
	    	else
	    	{
	    		Log.w(LOG_TAG, "Unable to read int for key=" + key);
	    		return 0;
	    	}
    	}
    	catch (Exception ex)
    	{
    		Log.e(LOG_TAG, "Error getting manifest int for key=" + key + ", ex=" + ex.toString());
    		return 0;
    	}
    }
}
