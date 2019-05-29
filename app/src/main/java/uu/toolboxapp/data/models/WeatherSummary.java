package uu.toolboxapp.data.models;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

import uu.toolbox.core.UUDate;
import uu.toolbox.core.UUJson;
import uu.toolbox.core.UUJsonConvertible;
import uu.toolbox.data.UUDataModel;
import uu.toolbox.data.UUSqlColumn;
import uu.toolbox.data.UUSqlTable;

@UUSqlTable(tableName = "weather_summary")
public class WeatherSummary
        implements UUJsonConvertible, UUDataModel
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Data elements
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @UUSqlColumn(name = "id", type = UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT)
    private Long id;

    @UUSqlColumn(name = "city", type = UUSqlColumn.Type.TEXT)
    private String city;

    @UUSqlColumn(name = "sunrise", type = UUSqlColumn.Type.INT_64)
    private Long sunrise;

    @UUSqlColumn(name = "sunset", type = UUSqlColumn.Type.INT_64)
    private Long sunset;

    @UUSqlColumn(name = "latitude", type = UUSqlColumn.Type.REAL)
    private Double latitude;

    @UUSqlColumn(name = "longitude", type = UUSqlColumn.Type.REAL)
    private Double longitude;

    @UUSqlColumn(name = "weather_main", type = UUSqlColumn.Type.TEXT)
    private String weatherMain;

    @UUSqlColumn(name = "weather_description", type = UUSqlColumn.Type.TEXT)
    private String weatherDescription;

    @UUSqlColumn(name = "weather_icon", type = UUSqlColumn.Type.TEXT)
    private String weatherIcon;

    @UUSqlColumn(name = "temperature", type = UUSqlColumn.Type.REAL)
    private Float temperature;

    @UUSqlColumn(name = "pressure", type = UUSqlColumn.Type.INT_32)
    private Integer pressure;

    @UUSqlColumn(name = "humidity", type = UUSqlColumn.Type.INT_32)
    private Integer humidity;

    @UUSqlColumn(name = "min_temperature", type = UUSqlColumn.Type.REAL)
    private Float minTemperature;

    @UUSqlColumn(name = "max_temperature", type = UUSqlColumn.Type.REAL)
    private Float maxTemperature;

    @UUSqlColumn(name = "wind_speed", type = UUSqlColumn.Type.REAL)
    private Float windSpeed;

    @UUSqlColumn(name = "wind_direction", type = UUSqlColumn.Type.REAL)
    private Float windDirection;

    @UUSqlColumn(name = "visibility", type = UUSqlColumn.Type.INT_32)
    private Integer visibility;

    @UUSqlColumn(name = "timestamp", type = UUSqlColumn.Type.INT_64)
    private Long timestamp;

    @UUSqlColumn(name = "cloud_percent", type = UUSqlColumn.Type.REAL)
    private Float cloudPercent;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Accessors
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public Long getSunrise()
    {
        return sunrise;
    }

    public void setSunrise(Long sunrise)
    {
        this.sunrise = sunrise;
    }

    public Long getSunset()
    {
        return sunset;
    }

    public void setSunset(Long sunset)
    {
        this.sunset = sunset;
    }

    public Double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public String getWeatherMain()
    {
        return weatherMain;
    }

    public void setWeatherMain(String weatherMain)
    {
        this.weatherMain = weatherMain;
    }

    public String getWeatherDescription()
    {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription)
    {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon()
    {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon)
    {
        this.weatherIcon = weatherIcon;
    }

    public Float getTemperature()
    {
        return temperature;
    }

    public void setTemperature(Float temperature)
    {
        this.temperature = temperature;
    }

    public Integer getPressure()
    {
        return pressure;
    }

    public void setPressure(Integer pressure)
    {
        this.pressure = pressure;
    }

    public Integer getHumidity()
    {
        return humidity;
    }

    public void setHumidity(Integer humidity)
    {
        this.humidity = humidity;
    }

    public Float getMinTemperature()
    {
        return minTemperature;
    }

    public void setMinTemperature(Float minTemperature)
    {
        this.minTemperature = minTemperature;
    }

    public Float getMaxTemperature()
    {
        return maxTemperature;
    }

    public void setMaxTemperature(Float maxTemperature)
    {
        this.maxTemperature = maxTemperature;
    }

    public Float getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public Float getWindDirection()
    {
        return windDirection;
    }

    public void setWindDirection(Float windDirection)
    {
        this.windDirection = windDirection;
    }

    public Integer getVisibility()
    {
        return visibility;
    }

    public void setVisibility(Integer visibility)
    {
        this.visibility = visibility;
    }

    public Long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    public Float getCloudPercent()
    {
        return cloudPercent;
    }

    public void setCloudPercent(Float cloudPercent)
    {
        this.cloudPercent = cloudPercent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUJsonConvertible
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void fillFromJson(@NonNull final JSONObject json)
    {
        setId(null);

        JSONObject coord = UUJson.safeGetJsonObject(json, "coord");
        setLatitude(UUJson.safeGetDouble(coord, "lat"));
        setLongitude(UUJson.safeGetDouble(coord, "lon"));

        JSONObject weather = null;
        JSONArray weatherArray = UUJson.safeGetJsonArray(json, "weather");
        if (weatherArray != null)
        {
            Object weatherTmp = UUJson.safeGetJsonObject(weatherArray, 0);
            if (weatherTmp != null && weatherTmp instanceof JSONObject)
            {
                weather = (JSONObject)weatherTmp;
            }
        }

        setWeatherMain(UUJson.safeGetString(weather, "main"));
        setWeatherDescription(UUJson.safeGetString(weather, "description"));
        setWeatherIcon(UUJson.safeGetString(weather, "icon"));

        JSONObject main = UUJson.safeGetJsonObject(json, "main");
        setTemperature(UUJson.safeGetFloat(main, "temp"));
        setPressure(UUJson.safeGetInt(main, "pressure"));
        setHumidity(UUJson.safeGetInt(main, "humidity"));
        setMinTemperature(UUJson.safeGetFloat(main, "temp_min"));
        setMaxTemperature(UUJson.safeGetFloat(main, "temp_max"));

        setVisibility(UUJson.safeGetInt(json, "visibility"));

        JSONObject wind = UUJson.safeGetJsonObject(json, "wind");
        setWindSpeed(UUJson.safeGetFloat(wind, "speed"));
        setWindDirection(UUJson.safeGetFloat(wind, "deg"));

        JSONObject cloud = UUJson.safeGetJsonObject(json, "cloud");
        setCloudPercent(UUJson.safeGetFloat(cloud, "all"));

        setTimestamp(UUJson.safeGetLong(json, "dt") * 1000);
        setCity(UUJson.safeGetString(json, "name"));

        JSONObject sys = UUJson.safeGetJsonObject(json, "sys");
        setSunrise(UUJson.safeGetLong(sys, "sunrise") * 1000);
        setSunset(UUJson.safeGetLong(sys, "sunset") * 1000);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject()
    {
        // Return null because we only care about deserializing from JSON
        return new JSONObject();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // System.Object Overrides
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        try
        {
            return String.format(Locale.US, "city: %s, sunrise: %s, sunset: %s, temp: %f, minTemp: %f, maxTemp: %f, clouds: %f%%, pressure: %d, humidity: %d, visibility: %d, main: %s, description: %s, icon: %s, lat: %f, lng: %f, timestamp: %s",
                    getCity(),
                    UUDate.formatExtendedDate(new Date(getSunrise())),
                    UUDate.formatExtendedDate(new Date(getSunset())),
                    getTemperature(),
                    getMinTemperature(),
                    getMaxTemperature(),
                    getCloudPercent(),
                    getPressure(),
                    getHumidity(),
                    getVisibility(),
                    getWeatherMain(),
                    getWeatherDescription(),
                    getWeatherIcon(),
                    getLatitude(),
                    getLongitude(),
                    UUDate.formatExtendedDate(new Date(getTimestamp())));
        }
        catch (Exception ex)
        {
            return super.toString();
        }
    }
}
