package uu.toolbox.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;

import uu.toolbox.logging.UULog;

@SuppressWarnings("unused")
public final class UUDeviceInfo
{
    public static String getPlatformString()
    {
        return Build.MODEL;
    }

    public static String getOSVersionString()
    {
        return String.valueOf(Build.VERSION.RELEASE);
    }

    public static String getAppName(final Context context)
    {
        try
        {
            return context.getPackageName();
        }
        catch(Exception ex)
        {
            UULog.error(UUDeviceInfo.class, "getAppName", ex);
        }

        return "unknown";
    }

    public static String getAppVersion(final Context context)
    {
        try
        {
            PackageManager manager = context.getPackageManager();

            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        }
        catch(Exception ex)
        {
            UULog.error(UUDeviceInfo.class, "getAppVersion", ex);
        }

        return "unknown";
    }

    public static String getScreenSize(final Context context)
    {
        try
        {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null)
            {
                Display display = wm.getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                return String.format(Locale.US, "%dx%d", width, height);
            }
        }
        catch(Exception ex)
        {
            UULog.error(UUDeviceInfo.class, "getScreenSize", ex);
        }

        return "unknown";
    }

    public static String getHardwareId(final Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
