package uu.toolbox.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;

import uu.toolbox.logging.UULog;

/**
 * Helper class for requesting runtime permissions in Android 6.0 and up
 *
 * Example usage:
 *
 * UUPermissions.requestPermissions(this, "android.permission.ACCESS_FINE_LOCATION", 1234, new UUPermissions.UUPermissionDelegate()
   {
       @Override
       public void onPermissionRequestComplete(String permission, boolean granted)
       {
            if (granted)
            {
                // Do something with the permission
            }
            else
            {
                // Handle the error
            }
       }
   });


 Step 2:  Handle onRequestPermissionsResult in your activity and pass the result to the permissions manager

 @Override
 public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
 {
    boolean handled = UUPermissions.handleRequestPermissionsResult(this, requestCode, permissions, grantResults);
    if (!handled)
    {
        // This permission request didn't come from UUPermissions
    }
 }

 *
 */
public class UUPermissions
{
    private static HashMap<Integer, UUPermissionDelegate> callbacks = new HashMap<>();

    public interface UUPermissionDelegate
    {
        void onPermissionRequestComplete(String permission, boolean granted);
    }

    public static boolean hasPermission(final Context context, final String permission)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    private static boolean hasEverRequestedPermission(final Context context, final String permission)
    {
        SharedPreferences prefs = context.getSharedPreferences(UUPermissions.class.getName(), Activity.MODE_PRIVATE);
        return prefs.getBoolean("HAS_REQUESTED_" + permission, false);
    }

    private static void setHasRequestedPermission(final Context context, final String permission)
    {
        SharedPreferences prefs = context.getSharedPreferences(UUPermissions.class.getName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean("HAS_REQUESTED_" + permission, true);
        prefsEditor.apply();
    }



    public static boolean canRequestPermission(@NonNull final Activity activity, @NonNull final String permission)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                return !hasEverRequestedPermission(activity, permission) || activity.shouldShowRequestPermissionRationale(permission);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUPermissions.class, "canRequestPermission", ex);
        }

        return true;
    }



    public static void requestPermissions(final Activity activity, final String permission, final int requestId, final UUPermissionDelegate delegate)
    {
        boolean hasPermission = hasPermission(activity, permission);
        if (hasPermission)
        {
            safeNotifyDelegate(delegate, permission, true);
            removeDelegate(requestId);
        }
        else
        {
            // Wait for the results
            saveDelegate(delegate, requestId);
            ActivityCompat.requestPermissions(activity, new String[] { permission }, requestId);
        }
    }

    public static boolean handleRequestPermissionsResult(final Activity activity, int requestCode, String permissions[], int[] grantResults)
    {
        try
        {
            UUPermissionDelegate delegate = null;

            if (callbacks.containsKey(requestCode))
            {
                delegate = callbacks.get(requestCode);
                for (int i = 0; i < permissions.length; i++)
                {
                    String permission = permissions[i];
                    setHasRequestedPermission(activity, permission);
                    int result = grantResults[i];
                    safeNotifyDelegate(delegate, permission, (result == PackageManager.PERMISSION_GRANTED));
                }

                removeDelegate(requestCode);
                return true;
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUPermissions.class, "handleRequestPermissionsResult", ex);
        }

        return false;
    }

    private static synchronized void saveDelegate(final UUPermissionDelegate delegate, final Integer requestId)
    {
        try
        {
            if (delegate != null)
            {
                callbacks.put(requestId, delegate);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUPermissions.class, "saveDelegate", ex);
        }
    }

    private static synchronized void removeDelegate(final Integer requestId)
    {
        try
        {
            if (callbacks.containsKey(requestId))
            {
                callbacks.remove(requestId);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUPermissions.class, "safeNotifyDelegate", ex);
        }
    }

    private static void safeNotifyDelegate(final UUPermissionDelegate delegate, final String permission, final boolean granted)
    {
        try
        {
            if (delegate != null)
            {
                delegate.onPermissionRequestComplete(permission, granted);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUPermissions.class, "safeNotifyDelegate", ex);
        }
    }
}
