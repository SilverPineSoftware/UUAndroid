package uu.toolbox.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;

import uu.toolbox.logging.UULog;

/**
 * Convenience wrappers for using system dialogs
 */
public final class UUAlert
{
    public enum ButtonType
    {
        Positive,
        Negative,
        Neutral
    }

    public interface AlertCallback
    {
        void onComplete(final ButtonType which);
    }

    private static void invokeAlertCallback(final AlertCallback callback, final ButtonType which)
    {
        try
        {
            if (callback != null)
            {
                callback.onComplete(which);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUAlert.class, "invokeAlertCallback", ex);
        }
    }

    public static void showMessageBox(
            final Activity activity,
            final int title,
            final int message,
            final int positiveButtonText,
            final int negativeButtonText,
            final AlertCallback callback)
    {
        Resources rez = activity.getResources();

        showMessageBox(activity,
                rez.getString(title),
                rez.getString(message),
                rez.getString(positiveButtonText),
                rez.getString(negativeButtonText),
                callback);
    }

    public static void showMessageBox(
            final Activity activity,
            final CharSequence title,
            final CharSequence message,
            final CharSequence positiveButtonText,
            final CharSequence negativeButtonText,
            final AlertCallback callback)
    {
        try
        {
            if (activity != null)
            {
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(title);
                            builder.setMessage(message);

                            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                    invokeAlertCallback(callback, ButtonType.Positive);
                                }
                            });

                            builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                    invokeAlertCallback(callback, ButtonType.Negative);}
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        catch (Exception ex)
                        {
                            UULog.debug(UUAlert.class, "showMessageBox.run", ex);
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUAlert.class, "showMessageBox", ex);
        }
    }

    public static void showMessageBox(
            final Activity activity,
            final int message,
            final int buttonText,
            final AlertCallback callback)
    {
        Resources rez = activity.getResources();

        showMessageBox(activity,
                rez.getString(message),
                rez.getString(buttonText),
                callback);
    }

    public static void showMessageBox(
            final Activity activity,
            final String message,
            final String buttonText,
            final AlertCallback callback)
    {
        try
        {
            if (activity != null)
            {
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(message);

                            builder.setNeutralButton(buttonText, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                    invokeAlertCallback(callback, ButtonType.Neutral);
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        catch (Exception ex)
                        {
                            UULog.debug(UUAlert.class, "showMessageBox.run", ex);
                        }
                    }
                });
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUAlert.class, "showMessageBox", ex);
        }
    }
}
