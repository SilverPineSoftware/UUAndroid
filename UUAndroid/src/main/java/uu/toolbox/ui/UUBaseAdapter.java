package uu.toolbox.ui;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * This useful extension to BaseAdapter ties a specific object to each row
 * and provides a simple callback delegate when the row is pressed.
 *
 */
public abstract class UUBaseAdapter<T extends Object> extends BaseAdapter
{
	//////////////////////////////////////////////////////////////////////////////////////////
	// Private Data Members
	//////////////////////////////////////////////////////////////////////////////////////////
	private Context context;
	protected List<T> data = new Vector<T>();
	private UUBaseAdapterDelegate<T> delegate;
	private int cellMasterLayoutId;
	private Class<T> rowDataClass;
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Construction
	//////////////////////////////////////////////////////////////////////////////////////////

	public UUBaseAdapter(final Context context, final Class<T> rowDataClass, final UUBaseAdapterDelegate<T> delegate, final int cellMasterLayoutId)
	{
		this.context = context;
		this.delegate = delegate;
		this.cellMasterLayoutId = cellMasterLayoutId;
		this.rowDataClass = rowDataClass;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	// BaseAdapter Overrides
	//////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getCount() 
	{
		return data.size();
	}

	@Override
	public Object getItem(int position) 
	{
		if (position >= 0 && position < data.size())
		{
			return data.get(position);
		}
		
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}
	
	@Override
	public int getItemViewType(int position) 
	{
		return position;
	}
	
	@Override
	public int getViewTypeCount() 
	{
		return 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View dataView = convertView;
		
		try
		{
			if (dataView == null) 
			{
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				dataView = inflater.inflate(cellMasterLayoutId, parent, false);
			} 
			
			final int row = position;
			final T rowData = dataForRow(position);
			updateRow(row, dataView, rowData);
			
			if (delegate != null)
			{
				dataView.setOnClickListener(new OnClickListener() 
				{	
					@Override
					public void onClick(View v) 
					{
						onRowClicked(row, rowData);
					}
				});
			}
		}
		catch (Exception ex)
		{
			Log.e(getClass().getName(), "Error in getView", ex);
		}
		
		return dataView;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Abstract Methods
	//////////////////////////////////////////////////////////////////////////////////////////
	protected abstract void updateRow(final int row, final View view, final T rowData);

	//////////////////////////////////////////////////////////////////////////////////////////
	// Protected Methods
	//////////////////////////////////////////////////////////////////////////////////////////
	protected T dataForRow(final int row)
	{
		Object obj = getItem(row);
		if (obj != null)
		{
			if (obj.getClass().isAssignableFrom(rowDataClass))
			{
				return rowDataClass.cast(obj);
			}
		}
		
		return null;
	}
	
	protected Context getContext()
	{
		return context;
	}
	
	protected void onRowClicked(final int row, final T rowData)
	{
		try
		{
			if (delegate != null)
			{
				delegate.didSelectRow(row, rowData);
			}
		}
		catch (Exception ex)
		{
			Log.e(getClass().getName(), "Error in onRowClicked", ex);
		}
	}
		
	//////////////////////////////////////////////////////////////////////////////////////////
	// Public Methods
	//////////////////////////////////////////////////////////////////////////////////////////
	
	public void updateData(final List<T> data)
	{
		this.data = data;
		notifyDataSetChanged();
	}
	
	public void addItem(final T item)
	{
		this.data.add(item);
		notifyDataSetChanged();
	}
	
	public void removeItem(final T item)
	{
		this.data.remove(item);
		notifyDataSetChanged();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Public Interfaces
	//////////////////////////////////////////////////////////////////////////////////////////

	public interface UUBaseAdapterDelegate<T extends Object>
	{
		void didSelectRow(final int row, final T rowData);
	}
}

