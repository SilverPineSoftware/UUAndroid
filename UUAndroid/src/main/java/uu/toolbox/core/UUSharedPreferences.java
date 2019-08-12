package uu.toolbox.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * UUSharedPreferences
 * 
 * Useful Utilities - A static wrapper around the SharedPreferences class
 *  
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class UUSharedPreferences 
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Data Members
	///////////////////////////////////////////////////////////////////////////////////////////////

	private static Context theApplicationContext;
	private static SharedPreferences theSharedPrefs;
	private static SharedPreferences.Editor theSharedPrefsEditor;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Public Static Methods
	///////////////////////////////////////////////////////////////////////////////////////////////

	public static void init(@NonNull final Context context, @NonNull final String prefsName)
	{
		if (theApplicationContext == null)
		{
			theApplicationContext = context;
			theSharedPrefs = context.getSharedPreferences(prefsName, Activity.MODE_PRIVATE);
			theSharedPrefsEditor = theSharedPrefs.edit();
		}
	}

	@Nullable
	public static String stringForKey(@NonNull final Object key)
	{
		return stringForKey(key, null);
	}

	@Nullable
	public static String stringForKey(@NonNull final Object key, final int defaultValueId)
	{
		return stringForKey(key, theApplicationContext.getString(defaultValueId));
	}

	@Nullable
	public static String stringForKey(@NonNull final Object key, @Nullable final String defaultString)
	{
		verifyReadSingleton();
		return theSharedPrefs.getString(key.toString(), defaultString);
	}

	@Nullable
	public static void setStringForKey(@NonNull final Object key, @Nullable final String value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putString(key.toString(), value);
		theSharedPrefsEditor.commit();
	}
	
	public static int intForKey(@NonNull final Object key)
	{
		return intForKey(key, -1);
	}
	
	public static int intForKey(@NonNull final Object key, final int defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getInt(key.toString(), defaultValue);
	}
	
	public static void setIntForKey(@NonNull final Object key, final int value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putInt(key.toString(), value);
		theSharedPrefsEditor.commit();
	}
	
	public static long longForKey(@NonNull final Object key)
	{
		return longForKey(key, -1);
	}
	
	public static long longForKey(@NonNull final Object key, final long defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getLong(key.toString(), defaultValue);
	}
	
	public static void setLongForKey(@NonNull final Object key, final long value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putLong(key.toString(), value);
		theSharedPrefsEditor.commit();
	}
	
	public static float floatForKey(@NonNull final Object key)
	{
		return floatForKey(key, -1);
	}
	
	public static float floatForKey(@NonNull final Object key, final float defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getFloat(key.toString(), defaultValue);
	}
	
	public static void setFloatForKey(@NonNull final Object key, final float value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putFloat(key.toString(), value);
		theSharedPrefsEditor.commit();
	}

    public static double doubleForKey(@NonNull final Object key)
    {
        return doubleForKey(key, 0);
    }

    public static double doubleForKey(@NonNull final Object key, final double defaultValue)
    {
        verifyReadSingleton();
        return Double.longBitsToDouble(theSharedPrefs.getLong(key.toString(), Double.doubleToLongBits(defaultValue)));
    }

    public static void setDoubleForKey(@NonNull final Object key, final double value)
    {
        verifyWriteSingleton();
        theSharedPrefsEditor.putLong(key.toString(), Double.doubleToLongBits(value));
        theSharedPrefsEditor.commit();
    }
	
	public static boolean boolForKey(@NonNull final Object key)
	{
		return boolForKey(key, false);
	}
	
	public static boolean boolForKey(@NonNull final Object key, final boolean defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getBoolean(key.toString(), defaultValue);
	}
	
	public static void setBoolForKey(@NonNull final Object key, final boolean value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putBoolean(key.toString(), value);
		theSharedPrefsEditor.commit();
	}
	
	public static void removeValueForKey(@NonNull final Object key)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.remove(key.toString());
		theSharedPrefsEditor.commit();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Static Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private static void verifyReadSingleton()
	{
		if (theSharedPrefs == null)
		{
			throw new RuntimeException("Shared Prefs not initialized.  Make sure to call init!");
		}
	}
	
	private static void verifyWriteSingleton()
	{
		if (theSharedPrefsEditor == null)
		{
			throw new RuntimeException("Shared Prefs Editor not initialized.  Make sure to call init!");
		}
	}

}
