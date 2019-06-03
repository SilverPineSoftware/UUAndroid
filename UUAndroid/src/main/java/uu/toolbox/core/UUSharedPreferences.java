package uu.toolbox.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * UUSharedPreferences
 * 
 * Useful Utilities - A static wrapper around the SharedPreferences class
 *  
 */
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

	public static final void init(final Context context, final String prefsName)
	{
		if (theApplicationContext == null)
		{
			theApplicationContext = context;
			theSharedPrefs = context.getSharedPreferences(prefsName, Activity.MODE_PRIVATE);
			theSharedPrefsEditor = theSharedPrefs.edit();
		}
	}
	
	public static final String stringForKey(final String key)
	{
		return stringForKey(key, null);
	}
	
	public static final String stringForKey(final String key, final int defaultValueId)
	{
		return stringForKey(key, theApplicationContext.getString(defaultValueId));
	}
	
	public static final String stringForKey(final String key, final String defaultString)
	{
		verifyReadSingleton();
		return theSharedPrefs.getString(key, defaultString);
	}
	
	public static final void setStringForKey(final String key, final String value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putString(key, value);
		theSharedPrefsEditor.commit();
	}
	
	public static final int intForKey(final String key)
	{
		return intForKey(key, -1);
	}
	
	public static final int intForKey(final String key, final int defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getInt(key, defaultValue);
	}
	
	public static final void setIntForKey(final String key, final int value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putInt(key, value);
		theSharedPrefsEditor.commit();
	}
	
	public static final long longForKey(final String key)
	{
		return longForKey(key, -1);
	}
	
	public static final long longForKey(final String key, final long defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getLong(key, defaultValue);
	}
	
	public static final void setLongForKey(final String key, final long value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putLong(key, value);
		theSharedPrefsEditor.commit();
	}
	
	public static final float floatForKey(final String key)
	{
		return floatForKey(key, -1);
	}
	
	public static final float floatForKey(final String key, final float defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getFloat(key, defaultValue);
	}
	
	public static final void setFloatForKey(final String key, final float value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putFloat(key, value);
		theSharedPrefsEditor.commit();
	}

    public static final double doubleForKey(final String key)
    {
        return doubleForKey(key, 0);
    }

    public static final double doubleForKey(final String key, final double defaultValue)
    {
        verifyReadSingleton();
        return Double.longBitsToDouble(theSharedPrefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static final void setDoubleForKey(final String key, final double value)
    {
        verifyWriteSingleton();
        theSharedPrefsEditor.putLong(key, Double.doubleToLongBits(value));
        theSharedPrefsEditor.commit();
    }
	
	public static final boolean boolForKey(final String key)
	{
		return boolForKey(key, false);
	}
	
	public static final boolean boolForKey(final String key, final boolean defaultValue)
	{
		verifyReadSingleton();
		return theSharedPrefs.getBoolean(key, defaultValue);
	}
	
	public static final void setBoolForKey(final String key, final boolean value)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.putBoolean(key, value);
		theSharedPrefsEditor.commit();
	}
	
	public static final void removeValueForKey(final String key)
	{
		verifyWriteSingleton();
		theSharedPrefsEditor.remove(key);
		theSharedPrefsEditor.commit();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Static Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final void verifyReadSingleton()
	{
		if (theSharedPrefs == null)
		{
			throw new RuntimeException("Shared Prefs not initialized.  Make sure to call init!");
		}
	}
	
	private static final void verifyWriteSingleton()
	{
		if (theSharedPrefsEditor == null)
		{
			throw new RuntimeException("Shared Prefs Editor not initialized.  Make sure to call init!");
		}
	}

}
