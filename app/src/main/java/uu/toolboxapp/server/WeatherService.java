package uu.toolboxapp.server;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;

import uu.toolbox.network.UUHttp;
import uu.toolbox.network.UUHttpDelegate;
import uu.toolbox.network.UUHttpMethod;
import uu.toolbox.network.UUHttpRequest;
import uu.toolbox.network.UUHttpResponse;
import uu.toolbox.network.UUMimeType;
import uu.toolbox.logging.UULog;
import uu.toolboxapp.data.AppDatabase;
import uu.toolboxapp.data.models.WeatherSummary;

/**
 * Simple interface to Open Weather Map API.  Used to illustrate UUHttp usage.
 */
public class WeatherService
{
    // Static Variables
    private static WeatherService theSharedInstance;

    // Instance Variables
    private Context context;
    private String apiKey = "6a747114c9cf80cf4d2908053cb4f8c2";
    private String baseUrl = "http://api.openweathermap.org/data/2.5/weather";


    public static void init(final Context context)
    {
        theSharedInstance = new WeatherService(context);
    }

    public static synchronized WeatherService sharedInstance()
    {
        return theSharedInstance;
    }

    private WeatherService(final Context context)
    {
        this.context = context;
    }

    public interface WeatherDelegate
    {
        void onComplete();
    }



    public void fetchWeather(final String city, final String country, final WeatherDelegate callback)
    {
        try
        {
            String url = baseUrl;

            HashMap<Object, Object> queryArguments = new HashMap<>();
            queryArguments.put("q", city + "," + country);
            queryArguments.put("APPID", apiKey);

            UUHttpRequest request = new UUHttpRequest(url, UUHttpMethod.GET);
            request.setQueryArguments(queryArguments);
            request.setContentType(UUMimeType.ApplicationJson);
            request.setTimeout(30000); // 30 seconds

            UUHttp.execute(request, new UUHttpDelegate()
            {
                @Override
                public void onCompleted(final UUHttpResponse response)
                {
                    final int httpResponseCode = response.getHttpResponseCode();
                    UULog.debug(getClass(), "fetchWeather.onComplete", "httpResponseCode: " + httpResponseCode);

                    try
                    {
                        if (response.isSuccessResponse())
                        {
                            JSONObject parsedResponse = response.getResponseAsJson();
                            if (parsedResponse != null)
                            {
                                WeatherSummary summary = new WeatherSummary();
                                summary.fillFromJson(parsedResponse);
                                UULog.debug(getClass(), "fetchWeather.onComplete", "Weather Summary: " + summary.toString());

                                AppDatabase.sharedInstance().logTable(WeatherSummary.class, "Before add");
                                AppDatabase.sharedInstance().addWeatherSummary(summary);
                                AppDatabase.sharedInstance().logTable(WeatherSummary.class, "After add");
                            }
                            else
                            {
                                UULog.debug(getClass(), "fetchWeather.onComplete", "Expected a JSON response");
                            }
                        }
                        else
                        {
                            if (response.getException() != null)
                            {
                                Exception ex = response.getException();
                                UULog.debug(getClass(), "fetchWeather.onComplete", ex);
                            }
                            else
                            {
                                JSONObject obj = response.getResponseAsJson();
                                if (obj != null)
                                {
                                }
                                else
                                {
                                    UULog.debug(getClass(), "fetchWeather.onComplete", "Expected a JSON Error response");
                                }
                            }
                        }
                    }
                    catch (Exception ex)
                    {
                        UULog.error(getClass(), "fetchWeather.onComplete", ex);
                    }
                    finally
                    {
                        safelyFinishOperation(callback, null);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "fetchWeather", ex);
            safelyFinishOperation(callback, ex);
        }
    }

    private static void safelyFinishOperation(final WeatherDelegate delegate, final Exception exception)
    {
        try
        {
            if (delegate != null)
            {
                //delegate.onComplete(error);
            }
        }
        catch (Exception ex)
        {
            UULog.error(WeatherService.class, "safelyFinishOperation", ex);
        }
    }
}
