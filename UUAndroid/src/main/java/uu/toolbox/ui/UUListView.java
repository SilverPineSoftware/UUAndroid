package uu.toolbox.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import uu.toolbox.core.UUThread;

/**
 * Helper methods for working with ListView
 */
public class UUListView
{
    /**
     * Reloads a single row in a list view
     *
     * @param listView the list view
     * @param position row position to reload
     */
    public static void reloadRow(final @NonNull ListView listView, final int position)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                int first = listView.getFirstVisiblePosition();
                int last = listView.getLastVisiblePosition();
                if (position >= first && position <= last)
                {
                    View existing = listView.getChildAt(position);
                    if (existing != null)
                    {
                        listView.getAdapter().getView(position, existing, listView);
                    }
                }
            }
        });
    }
}
