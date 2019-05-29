package uu.toolbox.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import uu.toolbox.core.UUTools;
import uu.toolbox.logging.UULog;

/**
 * UUActivity
 * 
 * Useful Utilities - A set of extension methods for the Activity class.
 *  
 */
@SuppressWarnings("unused")
public final class UUActivity 
{
	//////////////////////////////////////////////////////////////////////////////////////////
	// Constants
	//////////////////////////////////////////////////////////////////////////////////////////
	private static final String LOG_TAG = UUActivity.class.getName();

	//////////////////////////////////////////////////////////////////////////////////////////
	// Public Static Methods
	//////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Finds an TextView by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a TextView
	 */
	public static @Nullable TextView findTextViewById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, TextView.class);
    }
	
	/**
	 * Finds an Button by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a Button
	 */
	public static @Nullable Button findButtonById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, Button.class);
    }
	
	/**
	 * Finds an ImageButton by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return an ImageButton
	 */
	public static @Nullable ImageButton findImageButtonById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, ImageButton.class);
    }
	
	/**
	 * Finds an EditText by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return an EditText
	 */
	public static @Nullable EditText findEditTextById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, EditText.class);
    }
	
	/**
	 * Finds an ImageView by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a ImageView
	 */
	public static @Nullable ImageView findImageViewById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, ImageView.class);
    }
	
	/**
	 * Finds an LinearLayout by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a LinearLayout
	 */
	public static @Nullable LinearLayout findLinearLayoutById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, LinearLayout.class);
    }
	
	/**
	 * Finds an RelativeLayout by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a RelativeLayout
	 */
	public static @Nullable RelativeLayout findRelativeLayoutById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, RelativeLayout.class);
    }
	
	/**
	 * Finds an GridView by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a GridView
	 */
	public static @Nullable GridView findGridViewById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, GridView.class);
    }
	
	/**
	 * Finds an ListView by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a ListView
	 */
	public static @Nullable ListView findListViewById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, ListView.class);
    }
	
	/**
	 * Finds an ExpandableListView by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a ListView
	 */
	public static @Nullable ExpandableListView findExpandableListViewById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, ExpandableListView.class);
    }
	
	/**
	 * Finds an ScrollView by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a ScrollView
	 */
	public static @Nullable ScrollView findScrollViewById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, ScrollView.class);
    }
	
	/**
	 * Finds an CheckBox by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a CheckBox
	 */
	public static @Nullable CheckBox findCheckBoxById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, CheckBox.class);
    }
	
	/**
	 * Finds an RadioButton by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a RadioButton
	 */
	public static @Nullable RadioButton findRadioButtonById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, RadioButton.class);
    }
	
	/**
	 * Finds an SeekBar by a resource id
	 * 
	 * @param activity calling activity
	 * @param id resource id to fetch
	 * @return a SeekBar
	 */
	public static @Nullable SeekBar findSeekBarById(final @NonNull Activity activity, final int id)
    {
		return findViewById(activity, id, SeekBar.class);
    }
	
	/**
	 * Finds a View by resource ID and casts to a particular class type
	 * 
	 * @param activity calling activity
	 * @param id resource identifier
	 * @param type expected class type of the resource
	 * @return a view object that has been cast to the correct type.  Null if the view id is not found or the cast is invalid.
	 */
	public static @Nullable <T extends View> T findViewById(final @NonNull Activity activity, final int id, final @NonNull Class<T> type)
    {
        try
        {
            View v = activity.findViewById(id);
			if (v != null)
            {
				if (type.isAssignableFrom(v.getClass()))
				{
					return type.cast(v);
				}
            }
        }
        catch (Exception ex)
        {
            logException("findViewById", ex);
        }
        
        return null;    	
    }
	
	/**
	 * Safely displays a toast popup message from anywhere, regardless of which thread the caller is on
	 * 
	 * @param activity calling activity
	 * @param textResourceId resource of string id to show
	 * @param length length of toast.  Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
	 */
	public static void showToast(final Activity activity, final int textResourceId, final int length)
	{
		String text = null;
		if (activity != null)
		{
			text = activity.getResources().getString(textResourceId);
			showToast(activity, text, length);
		}
	}
	
	/**
	 * Safely displays a toast popup message from anywhere, regardless of which thread the caller is on
	 * 
	 * @param activity calling activity
	 * @param text message to show
	 * @param length length of toast.  Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
	 */
	public static void showToast(final Activity activity, final String text, final int length)
	{
		if (activity != null)
		{
			activity.runOnUiThread(new Runnable()
			{
				public void run()
				{
					try
					{
						Toast.makeText(activity, text, length).show();
					}
					catch (Exception ex)
					{
						Log.e(LOG_TAG, "", ex);
					}
				}
				
			});
		}
	}
	
	/**
	 * Launches the built in camera activity
	 * 
	 * @param activity calling activity
	 * @param extraOutputPath if specified, this is set as the EXTRA_OUTPUT param in the launching Intent.  This is
	 * supposed to save the captured image to this location, however some devices don't seem able to write to all
	 * locations.
	 * @param resultCode The activity callback result code
	 * @return true if the camera activity was launched, false otherwise
	 */
	public static boolean launchCamera(final Activity activity, final File extraOutputPath, final int resultCode)
    {
    	if (extraOutputPath != null)
    	{
    		Uri uri = Uri.fromFile(extraOutputPath);
    		return launchCamera(activity, uri, resultCode);
    	}
    	
    	return false;
    }
	
	/**
	 * Launches the built in camera activity
	 * 
	 * @param activity calling activity
	 * @param extraOutputPath if specified, this is set as the EXTRA_OUTPUT param in the launching Intent.  This is
	 * supposed to save the captured image to this location, however some devices don't seem able to write to all
	 * locations.
	 * @param resultCode The activity callback result code
	 * @return true if the camera activity was launched, false otherwise
	 */
	public static boolean launchCamera(final Activity activity, final Uri extraOutputPath, final int resultCode)
    {
		try
		{
	    	// Take Photo
	    	if (!UUTools.isIntentAvailable(activity, MediaStore.ACTION_IMAGE_CAPTURE))
	    	{
	    		Log.w(LOG_TAG, "Camera Intent is not available! Cannot launch camera.");
	    		return false;
	    	}
	
	    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	
	    	if (extraOutputPath != null)
	    	{
	    		takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutputPath);
	    	}
	    	
	    	activity.startActivityForResult(takePictureIntent, resultCode);
	    	return true;
		}
		catch (Exception ex)
		{
			Log.e(LOG_TAG, "Caught exception while trying to launch built in camera", ex);
			return false;
		}
    }
	
	/**
	 * Convenience method to set the visibility of a list of view id's
	 * 
	 * @param activity the activity that owns these view ids
	 * @param viewIdList a list of R.id values
	 * @param visibility the visibility value to assign to these views.
	 */
	public static void setGroupVisibility(final Activity activity, final int[] viewIdList, int visibility)
    {
    	for (int id : viewIdList)
		{
			View v = activity.findViewById(id);
			if (v != null)
			{
				v.setVisibility(visibility);
			}
		}
    }
	
	/**
	 * Convenience method to set the visibility of a single view id
	 * 
	 * @param activity the activity that owns these view ids
	 * @param viewId a resource id
	 * @param visibility the visibility value to assign to these views.
	 */
	public static void setVisibility(final Activity activity, final int viewId, int visibility)
    {
    	if (activity != null)
		{
			View v = activity.findViewById(viewId);
			if (v != null)
			{
				v.setVisibility(visibility);
			}
		}
    }
	
	/**
	 * Convenience method to remove a set of views from its parent
	 * 
	 * @param activity the activity that owns these view ids
	 * @param viewIdList a list of R.id values
	 * @param parentId the parent container view
	 */
	public static void removeViewsFromParent(final Activity activity, final int parentId, final int[] viewIdList)
    {
    	for (int id : viewIdList)
		{
    		ViewGroup vg = (ViewGroup)activity.findViewById(parentId);
			View v = activity.findViewById(id);
			if (v != null && vg != null)
			{
				vg.removeView(v);
			}
		}
    }
	
	/**
	 * Convenience method to add a set of views from its parent
	 * 
	 * @param activity the activity that owns these view ids
	 * @param viewIdList a list of R.id values
	 * @param parentId the parent container view
	 */
	public static void addViewsFromParent(final Activity activity, final int parentId, final int[] viewIdList)
    {
    	for (int id : viewIdList)
		{
    		ViewGroup vg = (ViewGroup)activity.findViewById(parentId);
			View v = activity.findViewById(id);
			if (v != null && vg != null)
			{
				vg.addView(v);
			}
		}
    }
	
	// Experimental....need investigation of various params
	public static boolean launchPhotoCrop(
			final Activity activity, 
			final Uri sourceUri, 
			final Uri destUri, 
			final Integer aspectX,
			final Integer aspectY,
			final Integer outputX,
			final Integer outputY,
			final Bitmap.CompressFormat outputFormat,
			final int resultCode)
	{
		Log.d(LOG_TAG, "Photo has been saved in gallery, now we can crop it");
		//Log.d(LOG_TAG, "onScanCompleted, path=" + path + ", uri=" + uri);
		
		Intent cropIntent = new Intent(Intent.ACTION_GET_CONTENT, sourceUri);
		cropIntent.setType("image/*");
		cropIntent.putExtra("crop", "true");
		
		if (aspectX != null)
		{
			cropIntent.putExtra("aspectX", aspectX.intValue());
		}
		
		if (aspectY != null)
		{
			cropIntent.putExtra("aspectY", aspectY.intValue());
		}
		
		if (outputX != null)
		{
			cropIntent.putExtra("outputX", outputX.intValue());
		}
		
		if (outputY != null)
		{
			cropIntent.putExtra("outputY", outputY.intValue());
		}
		
		cropIntent.putExtra("scale", true);
		cropIntent.putExtra("return-data", false);
		
		if (destUri != null)
		{
			// Is this required?
			cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, destUri);
		}
		
		if (outputFormat != null)
		{
			cropIntent.putExtra("outputFormat", outputFormat.toString());
		}
		
		activity.startActivityForResult(cropIntent, resultCode);
		return true;
	}
	
	public static Animation fadeInView(final Activity activity, int viewId, final int duration)
	{
		return doFadeAnimation(activity, viewId, android.R.anim.fade_in, duration, View.VISIBLE);
	}
	
	public static Animation fadeOutView(final Activity activity, int viewId, final int duration)
	{
		return doFadeAnimation(activity, viewId, android.R.anim.fade_out, duration, View.INVISIBLE);
	}
	
	public static Animation fadeInView(final Activity activity, int viewId)
	{
		return doFadeAnimation(activity, viewId, android.R.anim.fade_in, -1, View.VISIBLE);
	}
	
	public static Animation fadeOutView(final Activity activity, int viewId)
	{
		return doFadeAnimation(activity, viewId, android.R.anim.fade_out, -1, View.INVISIBLE);
	}
	
	public static Animation doFadeAnimation(final Activity activity, int viewId, final int animationId, final int duration, final int postAnimVisibility)
	{
		Animation animation  = AnimationUtils.loadAnimation(activity, animationId);
		if(animation == null)
		{
			return null;
		}
		
		if (duration > 0)
		{
			animation.setDuration(duration);
		}
		
		animation.reset();
		final View view = activity.findViewById(viewId);
		if (view != null)
		{
			animation.setAnimationListener(new AnimationListener() 
			{
				@Override
				public void onAnimationStart(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) 
				{
				}
				
				@Override
				public void onAnimationEnd(Animation animation) 
				{
					view.setVisibility(postAnimVisibility);
				}
			});
			
			view.clearAnimation();
			view.setVisibility(View.VISIBLE);
			view.startAnimation(animation);
			return animation;
		}
		
		return null;
	}
	
	/**
	 * Calculates a single line font size for a text label
	 * 
	 * @param tv
	 * @param startingTextSize
	 * @param maxWidth
	 * @return
	 */
	public static float deriveSingleLineFontSize(final TextView tv, final float startingTextSize, final int maxWidth)
    {
		Paint paint = new Paint();
		Rect bounds = new Rect();

		int tmpWidth = 0;
		float tmpSize = startingTextSize;

		paint.setTypeface(tv.getTypeface());
		paint.setTextSize(tmpSize);
		paint.setTextScaleX(tv.getTextScaleX());

		String text = tv.getText().toString();

		paint.getTextBounds(text, 0, text.length(), bounds);

		tmpWidth =  bounds.width();
		
		while (tmpWidth > maxWidth)
		{
			tmpSize -= 2;
			
			paint.setTextSize(tmpSize);
			paint.getTextBounds(text, 0, text.length(), bounds);
			tmpWidth =  bounds.width();
		}
		
		return tmpSize - 2;
    }
	
	/**
	 * Automatically adjusts the text view font for a single line
	 * 
	 * @param tv the text view in question
	 */
	public static void adjustFontForSingleLine(final TextView tv, final float startingTextSize)
	{
    	final ViewTreeObserver observer = tv.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
        {
            @Override
            public void onGlobalLayout() 
            {
            	int maxHeaderWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
            	float adjustedFontSize = UUActivity.deriveSingleLineFontSize(tv, startingTextSize, maxHeaderWidth);
    	        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, adjustedFontSize);
    	        
    	        tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
	}


	/**
	 * Hides the keyboard
	 *
	 * @param activity an activity
	 */
	public static void hideKeyboard(@NonNull final Activity activity)
	{
		try
		{
			View view = activity.getCurrentFocus();
			if (view != null)
			{
				InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null)
				{
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		}
		catch (Exception ex)
		{
			UULog.error(UUActivity.class, "hideKeyboard", ex);
		}
	}




    private static void debugLog(final String method, final String message)
    {
        UULog.debug(UUActivity.class, method, message);
    }

    private static void logException(final String method, final Exception ex)
    {
        UULog.error(UUActivity.class, method, ex);
    }

}
