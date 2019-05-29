package uu.toolbox.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONObject;

/*
    Android equivalent of NSError.  Simply a container for an error code, domain and a dictionary
    of user information.

 */
public class UUError implements UUJsonConvertible, Parcelable
{
    private enum JsonKeys
    {
        code,
        domain,
        exception,
        userInfo
    }

    private int code;
    private String domain;
    private Exception exception;
    private JSONObject userInfo;

    public UUError()
    {
    }

    public UUError(@NonNull final String domain, final int code)
    {
        this.domain = domain;
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    public Exception getException()
    {
        return exception;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }

    public JSONObject getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo(JSONObject userInfo)
    {
        this.userInfo = userInfo;
    }

    public void addUserInfo(Object key, Object value)
    {
        if (userInfo == null)
        {
            userInfo = new JSONObject();
        }

        UUJson.safePut(userInfo, key, value);
    }

    public Object getUserInfoField(@NonNull final Object key)
    {
        return UUJson.safeGet(userInfo, key);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUJsonConvertible
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void fillFromJson(@NonNull final JSONObject json)
    {
        code = UUJson.safeGetInt(json, JsonKeys.code);
        domain = UUJson.safeGetString(json, JsonKeys.domain);

        byte[] exceptionBytes = UUJson.safeGetDataFromBase64String(json, JsonKeys.exception);
        exception = UUData.deserializeObject(Exception.class, exceptionBytes);

        userInfo = UUJson.safeGetJsonObject(json, JsonKeys.userInfo);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject()
    {
        JSONObject json = new JSONObject();

        UUJson.safePut(json, JsonKeys.code, code);
        UUJson.safePut(json, JsonKeys.domain, domain);
        UUJson.safePut(json, JsonKeys.exception, UUString.byteToBase64String(UUData.serializeObject(exception)));
        UUJson.safePut(json, JsonKeys.userInfo, userInfo);

        return json;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Parcelable Implementation
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        writeJsonToParcel(dest, flags);
    }

    public static final Parcelable.Creator<UUError> CREATOR = new Parcelable.Creator<UUError>()
    {
        public UUError createFromParcel(Parcel in)
        {
            return new UUError(in);
        }

        public UUError[] newArray(int size)
        {
            return new UUError[size];
        }
    };

    private UUError(final Parcel in)
    {
        fillJsonFromParcel(in);
    }
}
