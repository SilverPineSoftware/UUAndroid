package uu.toolbox.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * UUView
 * 
 * Useful Utilities - A set of extension methods for the View class.
 *  
 */
public class UUView 
{
	/**
	 * Finds an TextView by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an TextView
	 */
	public static final TextView findTextViewById(final View view, final int id)
    {
		return findViewById(view, id, TextView.class);
    }
	
	/**
	 * Finds an Button by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an Button
	 */
	public static final Button findButtonById(final View view, final int id)
    {
		return findViewById(view, id, Button.class);
    }
	
	/**
	 * Finds an ImageButton by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an ImageButton
	 */
	public static final ImageButton findImageButtonById(final View view, final int id)
    {
		return findViewById(view, id, ImageButton.class);
    }
	
	/**
	 * Finds an EditText by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an EditText
	 */
	public static final EditText findEditTextById(final View view, final int id)
    {
		return findViewById(view, id, EditText.class);
    }
	
	/**
	 * Finds an ImageView by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an ImageView
	 */
	public static final ImageView findImageViewById(final View view, final int id)
    {
		return findViewById(view, id, ImageView.class);
    }
	
	/**
	 * Finds an LinearLayout by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an LinearLayout
	 */
	public static final LinearLayout findLinearLayoutById(final View view, final int id)
    {
		return findViewById(view, id, LinearLayout.class);
    }
	
	/**
	 * Finds an RelativeLayout by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an RelativeLayout
	 */
	public static final RelativeLayout findRelativeLayoutById(final View view, final int id)
    {
		return findViewById(view, id, RelativeLayout.class);
    }
	
	/**
	 * Finds an ProgressBar by a resource id
	 * 
	 * @param view the parent view
	 * @param id
	 * @return an ProgressBar
	 */
	public static final ProgressBar findProgressBarById(final View view, final int id)
    {
		return findViewById(view, id, ProgressBar.class);
    }
	
	/**
	 * Finds an View by a resource id
	 * 
	 * @param activity calling view
	 * @param id
	 * @return an ListView
	 */
	public static final ListView findListViewById(final View view, final int id)
    {
		return findViewById(view, id, ListView.class);
    }

    /**
     * Finds an View by a resource id
     *
     * @param activity calling view
     * @param id
     * @return an ExpandableListView
     */
    public static final ExpandableListView findExpandableListViewById(final View view, final int id)
    {
        return findViewById(view, id, ExpandableListView.class);
    }

	/**
	 * Finds a View by resource ID and casts to a particular class type
	 * 
	 * @param view the parent view
	 * @param id resource identifier
	 * @param type expected class type of the resource
	 * @return a view object that has been cast to the correct type.  Null if the view id is not found or the cast is invalid.
	 */
	public static final <T extends View> T findViewById(final View view, final int id, final Class<T> type)
    {
		if (view != null)
		{
	    	View v = view.findViewById(id);
	        if (v != null && v.getClass().isAssignableFrom(type))
	        {
	        	return type.cast(v);
	        }
		}
        
        return null;    	
    }
	
	/**
	 * Sets a bitmap or a default resource id
	 * 
	 * @param imageView target image view
	 * @param bitmap the bitmap to set
	 * @param defaultResourceId the fallback default resource id
	 */
	public static final void setBitmapOrDefault(final ImageView imageView, final Bitmap bitmap, final int defaultResourceId)
	{
		if (imageView != null)
		{
			if (bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
			}
			else
			{
				imageView.setImageResource(defaultResourceId);
			}
		}
	}
	
	/**
	 * Hides the software keyboard
	 * 
	 * @param view a view on the target screen
	 */
	public static final void hideSoftKeyword(final View view)
	{
		if (view != null)
		{
			InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	/**
	 * Gets the view tag and safely casts to an integer
	 */
	public static final int getTagAsInt(View view)
	{
		int tag = 0;
		
		try
		{
			if (view != null)
			{
				Object o = view.getTag();
				if (o != null && o.getClass().isAssignableFrom(String.class))
				{
					String str = (String)o;
					if (str != null)
					{
						Integer i = Integer.parseInt(str);
						tag = i.intValue();
					}
					
				}
			}
		}
		catch (Exception ex)
		{
			Log.e(UUView.class.getName(), "Error getting view tag as in integer", ex);
		}
		
		return tag;
	}
}
