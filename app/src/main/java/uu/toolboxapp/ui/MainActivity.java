package uu.toolboxapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uu.toolbox.logging.UULog;
import uu.toolboxapp.R;
import uu.toolboxapp.server.WeatherService;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        WeatherService.init(getApplicationContext());

        */
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public void onLeScannerClicked(View view)
    {
        Intent intent = new Intent(this, BtleScanActivity.class);
        startActivity(intent);
    }

    public void onClassicScannerClicked(View view)
    {
        Intent intent = new Intent(this, ClassicScanActivity.class);
        startActivity(intent);
    }

    public void onPhotoGalleryClicked(View view)
    {
        Intent intent = new Intent(this, PhotoGalleryActivity.class);
        startActivity(intent);
    }

    public void onWeatherClicked(View view)
    {
        WeatherService.sharedInstance().fetchWeather("Portland", "us", () ->
        {
            UULog.debug(getClass(), "onWeatherClicked", "Weather has been refreshed");

        });
    }
}
