package uu.toolbox.core;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.util.Locale;

import uu.toolbox.logging.UULog;

/**
 * UUAssets
 * 
 * Useful Utilities - A set of helper methods for loading localized bundled assets
 *  
 */
public final class UUAssets
{
    public static String DEFAULT_LANGUAGE_CODE = "en";

    public static String currentLanguageCode()
    {

        try
        {
            String language = Locale.getDefault().getLanguage();
            if (language != null && language.length() > 0)
            {
                return language;
            }
        }
        catch (Exception ex)
        {
            Log.e(UUAssets.class.getName(), "currentLanguageCode", ex);
        }

        return DEFAULT_LANGUAGE_CODE;
    }

	public static boolean doesAssetExist(final Context context, final String asset)
	{
		AssetManager mg = context.getAssets();

		try
		{
			mg.open(asset);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	public static String getAssetPath(final Context context, final String assetFileName)
	{
		String path = null;

		try
		{
			String asset = currentLanguageCode() + "/" + assetFileName;
			if (!doesAssetExist(context, asset))
			{
				asset = assetFileName;
			}

			if (doesAssetExist(context, asset))
			{
                path = "file:///android_asset/" + asset;
			}
		}
		catch (Exception ex)
		{
			UULog.error(UUAssets.class, "getAssetPath", ex);
		}

		return path;
	}
}
