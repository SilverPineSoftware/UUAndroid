package uu.toolbox.network;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

import uu.toolbox.core.UUJson;

/**
 * UUHttpRequest
 *
 * Useful Utilities - Wrapper to encapsulate all request params needed for a UUHttp request.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class UUHttpRequest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Data Members
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String url;
    private UUHttpMethod httpMethod = UUHttpMethod.GET;
    private final HashMap<Object, Object> queryArguments = new HashMap<>();
    private final HashMap<Object, Object> headerFields = new HashMap<>();
    private final ArrayList<Object> queryPathArguments = new ArrayList<>();
    private int timeout;
    private Object contentType;
    private byte[] body;
    private Proxy proxy;
    private SSLSocketFactory sslSocketFactory;
    //private boolean processMimeTypes = true;
    private boolean gzipCompression = false;
    private UUHttpResponseParser responseParser = new UUJsonResponseParser();

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public UUHttpRequest()
    {
    }

    public UUHttpRequest(@NonNull final String url, @NonNull final UUHttpMethod httpMethod)
    {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Public Gettors and Settors
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getURL()
    {
        return url;
    }

    public void setURL(final String url)
    {
        this.url = url;
    }

    public UUHttpMethod getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(final UUHttpMethod httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    @NonNull
    public HashMap<Object, Object> getQueryArguments()
    {
        return queryArguments;
    }

    public void setQueryArguments(@Nullable final HashMap<Object, Object> queryArguments)
    {
        this.queryArguments.clear();

        if (queryArguments != null)
        {
            this.queryArguments.putAll(queryArguments);
        }
    }

    public void addQueryArgument(@NonNull final Object arg, @NonNull final Object value)
    {
        queryArguments.put(arg, value);
    }

    @NonNull
    public ArrayList<Object> getQueryPathArguments()
    {
        return queryPathArguments;
    }

    public void setQueryPathArguments(@Nullable final ArrayList<Object> queryPathArguments)
    {
        this.queryPathArguments.clear();

        if (queryPathArguments != null)
        {
            this.queryPathArguments.addAll(queryPathArguments);
        }
    }

    public void setQueryPathArguments(@Nullable final Object[] queryPathArguments)
    {
        this.queryPathArguments.clear();

        if (queryPathArguments != null)
        {
            for (Object o : queryPathArguments)
            {
                addQueryPathArgument(o);
            }
        }
    }

    public void addQueryPathArgument(@NonNull final Object arg)
    {
        queryPathArguments.add(arg);
    }

    @NonNull
    public HashMap<Object, Object> getHeaderFields()
    {
        return headerFields;
    }

    public void setHeaderFields(@Nullable final HashMap<Object, Object> headerFields)
    {
        this.headerFields.clear();

        if (headerFields != null)
        {
            this.headerFields.putAll(headerFields);
        }
    }

    public void addHeaderField(@NonNull final Object key, @NonNull final Object value)
    {
        headerFields.put(key, value);
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(final int timeout)
    {
        this.timeout = timeout;
    }

    public Object getContentType()
    {
        return contentType;
    }

    public void setContentType(final Object contentType)
    {
        this.contentType = contentType;
    }

    public byte[] getBody()
    {
        return body;
    }

    public void setBody(final byte[] body)
    {
        this.body = body;
    }

    public Proxy getProxy()
    {
        return proxy;
    }

    public void setProxy(final Proxy proxy)
    {
        this.proxy = proxy;
    }

    public SSLSocketFactory getSocketFactory()
    {
        return sslSocketFactory;
    }

    public void setSocketFactory(final SSLSocketFactory socketFactory)
    {
        sslSocketFactory = socketFactory;
    }

//    public boolean getProcessMimeTypes()
//    {
//        return processMimeTypes;
//    }
//
//    public void setProcessMimeTypes(final boolean processMimeTypes)
//    {
//        this.processMimeTypes = processMimeTypes;
//    }

    public boolean usesGzipCompression()
    {
        return gzipCompression;
    }

    public void setGzipCompression(final boolean gzipCompression)
    {
        this.gzipCompression = gzipCompression;
    }

    public UUHttpResponseParser getResponseParser()
    {
        return responseParser;
    }

    public void setResponseParser(UUHttpResponseParser responseParser)
    {
        this.responseParser = responseParser;
    }

    @NonNull
    public String buildFullUrlString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getURL());

        ArrayList<Object> pathArgs = getQueryPathArguments();
        for (Object pathArg : pathArgs)
        {
            sb.append("/");
            sb.append(pathArg);
        }

        HashMap<Object, Object> args = getQueryArguments();

        if (!args.isEmpty())
        {
            Uri.Builder uri = new Uri.Builder();

            Set<Object> keys = args.keySet();
            for (Object key : keys)
            {
                Object val = args.get(key);
                uri.appendQueryParameter(key.toString(), val.toString());
            }

            sb.append(uri.build().toString());
        }

        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Static convenience constructors
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static UUHttpRequest get(final String url, @Nullable final HashMap<Object, Object> queryArguments)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.GET);
        req.setQueryArguments(queryArguments);
        return req;
    }

    public static UUHttpRequest delete(final String url, @Nullable final HashMap<Object, Object> queryArguments)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.DELETE);
        req.setQueryArguments(queryArguments);
        return req;
    }

    public static UUHttpRequest post(final String url, @Nullable final HashMap<Object, Object> queryArguments, @Nullable final byte[] body, @Nullable final Object contentType)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(body);
        req.setContentType(contentType);
        return req;
    }

    public static UUHttpRequest put(final String url, @Nullable final HashMap<Object, Object> queryArguments, @Nullable final byte[] body, @Nullable final Object contentType)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.PUT);
        req.setQueryArguments(queryArguments);
        req.setBody(body);
        req.setContentType(contentType);
        return req;
    }

    public static UUHttpRequest jsonPost(final String url, @Nullable final HashMap<Object, Object> queryArguments, @NonNull final JSONObject body)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(UUJson.safeSerializeJson(body, UUContentEncoding.UTF8.toString()));
        req.setContentType(UUMimeType.ApplicationJson);
        return req;
    }

    public static UUHttpRequest jsonPut(final String url, @Nullable final HashMap<Object, Object> queryArguments, @NonNull final JSONObject body)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.PUT);
        req.setQueryArguments(queryArguments);
        req.setBody(UUJson.safeSerializeJson(body, UUContentEncoding.UTF8.toString()));
        req.setContentType(UUMimeType.ApplicationJson);
        return req;
    }

    public static UUHttpRequest jsonPost(final String url, @Nullable final HashMap<Object, Object> queryArguments, @NonNull final JSONArray body)
    {
        UUHttpRequest req = new UUHttpRequest(url, UUHttpMethod.POST);
        req.setQueryArguments(queryArguments);
        req.setBody(UUJson.safeSerializeJson(body, UUContentEncoding.UTF8.toString()));
        req.setContentType(UUMimeType.ApplicationJson);
        return req;
    }
}