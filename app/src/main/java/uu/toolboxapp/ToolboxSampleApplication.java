package uu.toolboxapp;

import android.app.Application;
import android.content.Context;

import uu.toolbox.data.UUDataCache;
import uu.toolbox.network.UURemoteData;
import uu.toolbox.network.UURemoteImage;
import uu.toolboxapp.data.AppDatabase;
import uu.toolboxapp.server.WeatherService;

public class ToolboxSampleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Context context = getApplicationContext();
        AppDatabase.init(context);
        UUDataCache.init(context);
        UURemoteData.init(context);
        UURemoteImage.init(context);
        WeatherService.init(context);
    }
}
