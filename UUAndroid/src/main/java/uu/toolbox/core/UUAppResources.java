package uu.toolbox.core;

import android.content.Context;
import android.util.TypedValue;

public final class UUAppResources 
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Singleton Implementation
	///////////////////////////////////////////////////////////////////////////////////////////////

	private UUAppResources() {} // Enforce singleton
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Public Methods
	///////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Looks up a string resource by key
	 * 
	 * @param context the application context
	 * @param key the resource name
	 * @return
	 */
	public static final String getStringByKey(final Context context, final String key)
	{
		TypedValue tv = getResourceByKey(context, key, "string");
		if (tv != null && tv.type == TypedValue.TYPE_STRING)
		{
			return tv.string.toString();
		}
		
		return "";
	}
	
	/**
	 * Fetches a resource by string key
	 * 
	 * Example: To fetch R.string.app_name, you would call like this:
	 * 
	 * // Assume we're being called from within an Activity method... 
	 * String appName = UUActivity.getResourceByKey(this, "app_name", "string");
	 * 
	 * @param context the application context
	 * @param key the resource name
	 * @param resourceType the resource type
	 * @return a valid resource object, or null if the resource cannot be found.
	 */
	public static final TypedValue getResourceByKey(final Context context, final String key, final String resourceType)
	{
		int keyId = context.getResources().getIdentifier(key, resourceType, context.getPackageName());
		if (keyId == 0)
		{
			// Zero is an invalid resource id.
			return null;
		}

		TypedValue tv = new TypedValue();
		context.getResources().getValue(keyId, tv, false);
		return tv;
	}

}
