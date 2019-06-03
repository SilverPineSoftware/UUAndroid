package uu.toolbox.core;

import android.content.Context;
import android.content.Intent;

/**
 * UUContext
 * 
 * Useful Utilities - A set of extension methods for the Context class.
 *  
 */
public class UUContext 
{
	/**
	 * Sends a simple broadcast across the android system
	 * 
	 * @param context the sending context
	 * @param notificationId the message
	 */
	public static final void broadcastNotification(final Context context, final String notificationId)
	{
		Intent i = new Intent(notificationId);
		context.sendBroadcast(i);
	}
	
	/**
	 * Sends a simple broadcast across the android system with a single data item
	 * 
	 * @param context the sending context
	 * @param notificationId the message
	 */
	public static final void broadcastNotification(
			final Context context, 
			final String notificationId,
			final String dataKey,
			final String data)
	{
		Intent i = new Intent(notificationId);
		i.putExtra(dataKey, data);
		context.sendBroadcast(i);
	}
}
