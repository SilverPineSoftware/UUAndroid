package uu.toolbox.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import uu.toolbox.core.UUObject;
import uu.toolbox.logging.UULog;

/**
 * UUHttpResponse
 *
 * Useful Utilities - Encapsulates response data from a UUHttpClient request call
 *
 */

@SuppressWarnings("unused")
public class UUHttpResponse
{
    protected UUHttpRequest _request;
    protected Exception _exception;
    protected Object _parsedResponse;
    protected byte[] _rawResponse;
    protected int _httpResponseCode;
    protected String _httpResponseMessage;
    protected String _contentType;
    protected String _contentEncoding;
    protected Map<String, List<String>> _responseHeaders;

    public UUHttpResponse()
    {

    }

    public UUHttpResponse(@NonNull final UUHttpResponse other)
    {
        _request = other._request;
        _exception = other._exception;
        _parsedResponse = other._parsedResponse;
        _rawResponse = other._rawResponse;
        _httpResponseCode = other._httpResponseCode;
        _httpResponseMessage = other._httpResponseMessage;
        _contentType = other._contentType;
        _contentEncoding = other._contentEncoding;
        _responseHeaders = other._responseHeaders;
    }

    public UUHttpRequest getRequest()
    {
        return _request;
    }

    public void setRequest(final UUHttpRequest request)
    {
        _request = request;
    }

    public Exception getException()
    {
        return _exception;
    }

    public void setException(final Exception exception)
    {
        _exception = exception;
    }

    public Object getParsedResponse()
    {
        return _parsedResponse;
    }

    public void setParsedResponse(final Object parsedResponse)
    {
        _parsedResponse = parsedResponse;
    }

    public byte[] getRawResponse()
    {
        return _rawResponse;
    }

    public void setRawResponse(final byte[] rawResponse)
    {
        _rawResponse = rawResponse;
    }

    public int getHttpResponseCode()
    {
        return _httpResponseCode;
    }

    public void setHttpResponseCode(final int httpResponseCode)
    {
        _httpResponseCode = httpResponseCode;
    }

    public String getHttpResponseMessage()
    {
        return _httpResponseMessage;
    }

    public void setHttpResponseMessage(final String httpResponseMessage)
    {
        _httpResponseMessage = httpResponseMessage;
    }

    public String getContentType()
    {
        return _contentType;
    }

    public void setContentType(final String contentType)
    {
        _contentType = contentType;
    }

    public String getContentEncoding()
    {
        return _contentEncoding;
    }

    public void setContentEncoding(final String contentEncoding)
    {
        _contentEncoding = contentEncoding;
    }

    public Map<String, List<String>> getResponseHeaders()
    {
        return _responseHeaders;
    }

    public void setResponseHeaders(final Map<String, List<String>> headers)
    {
        _responseHeaders = headers;
    }

    public String getResponseHeader(final String key)
    {
        String value = null;

        if (_responseHeaders != null && _responseHeaders.containsKey(key))
        {
            List<String> values = _responseHeaders.get(key);
            if (values != null && values.size() > 0)
            {
                value = values.get(0);
            }
        }

        return value;
    }

    public boolean isSuccessResponse()
    {
        return (_httpResponseCode >= 200 && _httpResponseCode < 300);
    }

    public JSONObject getResponseAsJson()
    {
        JSONObject obj = null;

        if (_parsedResponse != null)
        {
            if (_parsedResponse instanceof JSONObject)
            {
                obj = (JSONObject) _parsedResponse;
            }
        }

        return obj;
    }

    public JSONArray getResponseAsJsonArray()
    {
        JSONArray arr = null;

        if (_parsedResponse != null)
        {
            if (_parsedResponse instanceof JSONArray)
            {
                arr = (JSONArray) _parsedResponse;
            }
        }

        return arr;
    }

    @Nullable
    public static <T> T parsedResponseAs(@Nullable Class<T> type, @Nullable UUHttpResponse response)
    {
        if (response == null)
        {
            return null;
        }

        return UUObject.safeCast(type, response.getParsedResponse());
    }

    public void parseResponse()
    {
        Object parsedResponse = null;

        try
        {
            UUHttpResponseParser parser = _request.getResponseParser();
            if (parser != null)
            {
                parsedResponse = parser.parseResponse(this);
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUHttpResponseParser.class, "parseResponse", ex);
            parsedResponse = null;
        }

        setParsedResponse(parsedResponse);
    }
}