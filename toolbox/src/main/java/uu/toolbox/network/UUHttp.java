package uu.toolbox.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import uu.toolbox.core.UUCompression;
import uu.toolbox.logging.UULog;

/*
 * UUHttpClient is a simple wrapper for the java.net.HttpURLConnection class in the Android SDK.
 *
 * The main purpose of this class is to provide an easy to use wrapper for JSON web services.
 */

/**
 * UUHttpClientDelegate
 *
 * Useful Utilities - UUHttpClient is a simple wrapper for the java.net.HttpURLConnection class in the Android SDK.
 *
 * The main purpose of this class is to provide an easy to use wrapper for JSON web services.
 *
 */
@SuppressWarnings({"unused", "UnnecessaryToStringCall", "WeakerAccess"})
public final class UUHttp
{
    @NonNull
    public static UUHttpTask get(
            @NonNull final String url,
            @Nullable final HashMap<Object, Object> queryArguments,
            @NonNull final UUHttpDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.get(url, queryArguments);
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    public static UUHttpTask delete(
            @NonNull final String url,
            @Nullable final HashMap<Object, Object> queryArguments,
            @NonNull final UUHttpDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.delete(url, queryArguments);
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    public static UUHttpTask post(
            @NonNull final String url,
            @Nullable final HashMap<Object, Object> queryArguments,
            @Nullable final byte[] body,
            @Nullable final Object contentType,
            @NonNull final UUHttpDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.post(url, queryArguments, body, contentType);
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    public static UUHttpTask put(
            @NonNull final String url,
            @Nullable final HashMap<Object, Object> queryArguments,
            @Nullable final byte[] body,
            @Nullable final Object contentType,
            @NonNull final UUHttpDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.put(url, queryArguments, body, contentType);
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    public static UUHttpTask jsonPost(
            @NonNull final String url,
            @Nullable final HashMap<Object, Object> queryArguments,
            @NonNull final JSONObject body,
            @NonNull final UUHttpDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.jsonPost(url, queryArguments, body);
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    public static UUHttpTask jsonPut(
            @NonNull final String url,
            @Nullable final HashMap<Object, Object> queryArguments,
            @NonNull final JSONObject body,
            @NonNull final UUHttpDelegate delegate)
    {
        UUHttpRequest request = UUHttpRequest.jsonPut(url, queryArguments, body);
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    public static UUHttpTask execute(@NonNull final UUHttpRequest request, @NonNull final UUHttpDelegate delegate)
    {
        UUHttpTask task = new UUHttpTask(delegate);
        task.execute(request);
        return task;
    }

    @NonNull
    @SuppressWarnings("ConstantConditions")
    protected static UUHttpResponse executeRequest(@NonNull final UUHttpRequest request)
    {
        UUHttpResponse response = new UUHttpResponse();
        response.setRequest(request);

        HttpURLConnection urlConnection = null;

        try
        {
            Proxy proxy = request.getProxy();

            URL url = UUHttpUtils.safeCreateUrl(request.buildFullUrlString());

            if (proxy != null)
            {
                //noinspection ConstantConditions
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            }
            else
            {
                urlConnection = (HttpURLConnection) url.openConnection();
            }

            urlConnection.setConnectTimeout(request.getTimeout());
            urlConnection.setReadTimeout(request.getTimeout());
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod(request.getHttpMethod().toString());

            if (urlConnection instanceof HttpsURLConnection)
            {
                HttpsURLConnection httpsConn = (HttpsURLConnection)urlConnection;

                SSLSocketFactory factory = request.getSocketFactory();
                if (factory != null)
                {
                    httpsConn.setSSLSocketFactory(factory);
                }
            }

            UULog.debug(UUHttp.class, "executeRequest", request.getHttpMethod() + " " + url.toString());
            UULog.debug(UUHttp.class, "executeRequest", "Timeout: " + request.getTimeout());

            HashMap<Object, Object> headers = request.getHeaderFields();
            if (headers != null)
            {
                for (Object key : headers.keySet())
                {
                    Object val = headers.get(key);
                    urlConnection.setRequestProperty(key.toString(), val.toString());
                }
            }

            boolean compressed = request.usesGzipCompression();

            if (compressed)
            {
                urlConnection.setRequestProperty("Accept-Encoding", "gzip");
            }

            byte[] body = request.getBody();

            if (compressed)
            {
                body = UUCompression.gzip(body);
            }

            if (body != null && body.length > 0)
            {
                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(body.length);
                urlConnection.setRequestProperty("Content-Type", request.getContentType().toString());
                urlConnection.setRequestProperty("Content-Length", Integer.toString(body.length));

                if (compressed)
                {
                    urlConnection.setRequestProperty("Content-Encoding", "gzip");
                }

                logRequestHeaders(urlConnection);
                writeRequest(urlConnection, body);
            }
            else
            {
                logRequestHeaders(urlConnection);
            }

            logRequestBody(request);

            logResponseHeaders(urlConnection);
            response.setHttpResponseCode(safeGetResponseCode(urlConnection));
            response.setContentType(safeGetResponseContentType(urlConnection));
            response.setContentEncoding(safeGetResponseContentEncoding(urlConnection));
            UULog.debug(UUHttp.class, "executeRequest", "HTTP Response Code: " + response.getHttpResponseCode());
            UULog.debug(UUHttp.class, "executeRequest", "Response Content-Type:" + response.getContentType());
            UULog.debug(UUHttp.class, "executeRequest", "Response Content-Encoding: " + response.getContentEncoding());

            long start = System.currentTimeMillis();
            byte[] responseBuffer = readResponse(urlConnection);
            response.setRawResponse(responseBuffer);
            response.parseResponse();
            response.setResponseHeaders(safeGetResponseHeaderes(urlConnection));
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "executeRequest", ex);
            response.setException(ex);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    private static int safeGetResponseCode(final HttpURLConnection connection)
    {
        int responseCode = -1;

        try
        {
            if (connection != null)
            {
                responseCode = connection.getResponseCode();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "safeGetResponseCode", ex);
        }

        return responseCode;
    }

    private static String safeGetResponseContentType(final HttpURLConnection connection)
    {
        String contentType = null;

        try
        {
            if (connection != null)
            {
                contentType = connection.getContentType();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "safeGetResponseContentType", ex);
        }

        return contentType;
    }

    private static String safeGetResponseContentEncoding(final HttpURLConnection connection)
    {
        String contentEncoding = null;

        try
        {
            if (connection != null)
            {
                contentEncoding = connection.getContentEncoding();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "safeGetResponseContentEncoding", ex);
        }

        return contentEncoding;
    }

    private static Map<String, List<String>> safeGetResponseHeaderes(final HttpURLConnection connection)
    {
        Map<String, List<String>> headers = null;

        try
        {
            if (connection != null)
            {
                headers = connection.getHeaderFields();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "safeGetResponseHeaderes", ex);
        }

        return headers;
    }

    private static void logRequestHeaders(final HttpURLConnection connection)
    {
        try
        {
            if (connection != null)
            {
                Map<String, List<String>> headers = connection.getRequestProperties();
                if (headers != null)
                {
                    Set<String> headerKeys = headers.keySet();
                    for (String key : headerKeys)
                    {
                        List<String> headerValues = headers.get(key);
                        for (String val : headerValues)
                        {
                            UULog.debug(UUHttp.class, "logRequestHeaders", key + ": " + val);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "logRequestHeaders", ex);
        }
    }

    private static void logResponseHeaders(final HttpURLConnection connection)
    {
        try
        {
            if (connection != null)
            {
                Map<String, List<String>> headers = connection.getHeaderFields();
                if (headers != null)
                {
                    Set<String> headerKeys = headers.keySet();
                    for (String key : headerKeys)
                    {
                        List<String> headerValues = headers.get(key);
                        for (String val : headerValues)
                        {
                            UULog.debug(UUHttp.class, "logResponseHeaders", key + ": " + val);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "logResponseHeaders", ex);
        }
    }

    private static void logRequestBody(final UUHttpRequest request)
    {
        try
        {
            if (UULog.LOGGING_ENABLED)
            {
                if (request != null)
                {
                    byte[] body = request.getBody();
                    if (body != null)
                    {
                        String bodyAsString = new String(body, "UTF-8");
                        UULog.debug(UUHttp.class, "logReqeustBody", bodyAsString);
                    }
                    else
                    {
                        UULog.debug(UUHttp.class, "logReqeustBody", "Body is NULL");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "logRequestBody", ex);
        }
    }

    protected static void writeRequest(final HttpURLConnection connection, final byte[] body)
    {
        OutputStream os = null;

        try
        {
            os = new BufferedOutputStream(connection.getOutputStream());
            os.write(body);
            os.flush();
        }
        catch (IOException ex)
        {
            UULog.debug(UUHttp.class, "writeRequest", ex);
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException ex)
                {
                    UULog.debug(UUHttp.class, "writeRequest.close", ex);
                }
            }
        }
    }

    protected static byte[] readResponse(final HttpURLConnection connection) throws IOException
    {
        InputStream in;

        if (connection.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST)
        {
            in = new BufferedInputStream(connection.getErrorStream());
        }
        else
        {
            in = new BufferedInputStream(connection.getInputStream());
        }

        int bytesRead;

        byte[] buffer = new byte[10240];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        while (true)
        {
            bytesRead = in.read(buffer, 0, buffer.length);
            if (bytesRead == -1)
                break;

            //AppLog.debug(UUHttp.class, "readResponse", "Read " + bytesRead + " bytes");
            bos.write(buffer, 0, bytesRead);
        }

        return bos.toByteArray();
    }
}