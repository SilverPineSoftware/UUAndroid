package uu.toolboxapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import uu.toolbox.core.UUJson;
import uu.toolbox.data.UUDataCache;
import uu.toolbox.logging.UULog;
import uu.toolbox.network.UUHttp;
import uu.toolbox.network.UUHttpDelegate;
import uu.toolbox.network.UUHttpMethod;
import uu.toolbox.network.UUHttpRequest;
import uu.toolbox.network.UUHttpResponse;
import uu.toolbox.network.UURemoteData;
import uu.toolbox.network.UURemoteImage;
import uu.toolboxapp.R;

public class PhotoGalleryActivity extends AppCompatActivity
{
    private GridView gridView;
    private RemotePhotoAdapter gridAdapter;
    private final ArrayList<String> gridData = new ArrayList<>();

    private int contentCellSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        gridView = findViewById(R.id.gridView);
        gridAdapter = new RemotePhotoAdapter(this, R.layout.photo_grid_cell, gridData);
        gridView.setAdapter(gridAdapter);

        UUDataCache.sharedInstance().clearCache();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UURemoteData.Notifications.DataDownloaded);
        filter.addAction(UURemoteData.Notifications.DataDownloadFailed);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        BroadcastReceiver br = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                handlePhotoDownload(context, intent);
            }
        };


        ViewTreeObserver tvo = gridView.getViewTreeObserver();
        tvo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if (gridData.size() > 0)
                {
                    gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int contentContainerWidth = gridView.getWidth();
                    int columnCount = gridView.getNumColumns();
                    contentCellSize = contentContainerWidth / columnCount;
                }

                gridAdapter.notifyDataSetChanged();
            }
        });

        fetchImages();
    }

    private void fetchImages()
    {
        try
        {
            String url = "https://api.shutterstock.com/v2/images/search";

            UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.GET);
            req.addQueryArgument("page", "1");
            req.addQueryArgument("per_page", "500");
            req.addQueryArgument("query", "forest");

            String username = "d4a89-1400b-04251-4faee-f7a23-12271:61764-d9c3c-8a832-a7bdf-098e4-0b382";
            byte[] usernameData = username.getBytes("UTF-8");
            String usernameEncoded = Base64.encodeToString(usernameData, Base64.NO_WRAP);
            req.addHeaderField("Authorization", "Basic " + usernameEncoded);

            UUHttp.execute(req, new UUHttpDelegate()
            {
                @Override
                public void onCompleted(UUHttpResponse response)
                {
                    try
                    {
                        gridData.clear();

                        if (response.isSuccessResponse())
                        {
                            Object o = response.getParsedResponse();
                            if (o instanceof JSONObject)
                            {
                                JSONObject json = (JSONObject) o;
                                JSONArray data = UUJson.safeGetJsonArray(json, "data");
                                if (data != null)
                                {
                                    for (int i = 0; i < data.length(); i++)
                                    {
                                        JSONObject item = data.getJSONObject(i);
                                        JSONObject assets = UUJson.safeGetJsonObject(item, "assets");
                                        if (assets != null)
                                        {
                                            JSONObject largeThumb = UUJson.safeGetJsonObject(assets, "large_thumb");
                                            if (largeThumb != null)
                                            {
                                                String url = UUJson.safeGetString(largeThumb, "url");
                                                gridData.add(url);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        UULog.error(getClass(), "fetchImages", ex);
                    }
                    finally
                    {
                        gridAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "fetchImages", ex);
        }

        /*
        let url = "https://api.shutterstock.com/v2/images/search"

        var args : [String:String] = [:]
        args["page"] = "1"
        args["per_page"] = "500"
        args["query"] = "forest"

        let req = UUHttpRequest.getRequest(url, args)

        let username = "d4a89-1400b-04251-4faee-f7a23-12271:61764-d9c3c-8a832-a7bdf-098e4-0b382"
        let usernameData = username.data(using: .utf8)
        let usernameEncoded = usernameData!.base64EncodedString(options: Data.Base64EncodingOptions.init(rawValue: 0))
        req.headerFields["Authorization"] = "Basic \(usernameEncoded)"

        _ = UUHttpSession.executeRequest(req)
        { (response:UUHttpResponse) in

            if (response.httpError == nil)
            {
                self.tableData.removeAll()

                let parsed = response.parsedResponse as? [AnyHashable:Any]
                if (parsed != nil)
                {
                    let data = parsed!["data"] as? [ [AnyHashable:Any] ]
                    if (data != nil)
                    {
                        for item in data!
                            {
                                    let assets = item["assets"] as? [AnyHashable:Any]
                        if (assets != nil)
                        {
                            let largeThumb = assets!["large_thumb"] as? [AnyHashable:Any]
                            if (largeThumb != nil)
                            {
                                let value = largeThumb!["url"] as? String
                                if (value != nil)
                                {
                                    self.tableData.append(value!)
                                }
                            }
                        }
                        }
                    }

                    DispatchQueue.main.async
                    {
                        self.collectionView.reloadData()
                    }
                }
            }
            else
            {
                self.tableData.removeAll()

                DispatchQueue.main.async
                {
                    self.collectionView.reloadData()
                }
            }
        }*/
    }

    private void handlePhotoDownload(Context context, Intent intent)
    {
        if (UURemoteData.Notifications.DataDownloaded.equals(intent.getAction()))
        {
            String key = intent.getStringExtra(UURemoteData.NotificationKeys.RemotePath);
            UULog.debug(getClass(), "handlePhotoDownloaded", "Photo was downloaded: " + key);
            gridAdapter.notifyDataSetChanged();
        }

    }

    class RemotePhotoAdapter extends ArrayAdapter<String>
    {
        private LayoutInflater inflater;
        private int layoutResourceId;

        RemotePhotoAdapter(Context context, int resourceId, ArrayList<String> data)
        {
            super(context, resourceId, data);
            inflater = LayoutInflater.from(context);
            layoutResourceId = resourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            View v = convertView;
            if (v == null)
            {
                v = inflater.inflate(layoutResourceId, parent, false);
            }

            GridView.LayoutParams lp = (GridView.LayoutParams)v.getLayoutParams();
            lp.width = contentCellSize;
            lp.height = contentCellSize;
            v.setLayoutParams(lp);

            final String data = getItem(position);
            if (data != null)
            {
                ImageView imgView = v.findViewById(R.id.image);

                Bitmap bmp = UURemoteImage.sharedInstance().getImage(data, false);
                if (bmp != null)
                {
                    imgView.setImageBitmap(bmp);
                }
            }

            return v;
        }
    }
}
