package uu.toolbox.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import uu.toolbox.core.UUCompression;
import uu.toolbox.core.UUJson;
import uu.toolbox.logging.UULog;

public class UUJsonResponseParser implements UUHttpResponseParser
{
    @Override
    @Nullable
    public Object parseResponse(@NonNull UUHttpResponse response)
    {
        Object parsed = null;
        byte[] rawResponse = null;

        try
        {
            rawResponse = response.getRawResponse();
            String contentEncoding = response.getContentEncoding();
            String contentType = response.getContentType();

            byte[] processedResponse = rawResponse;

            if (contentEncoding != null)
            {
                if (contentEncoding.contains("gzip"))
                {
                    processedResponse = UUCompression.gunzip(rawResponse);
                }
            }

            if (contentType != null && processedResponse != null)
            {
                logResponseBody(processedResponse, null);

                if (contentType.contains("json"))
                {
                    String str = new String(processedResponse);
                    parsed = UUJson.toJson(str);
                }
                else if (contentType.startsWith("text"))
                {
                    parsed = new String(processedResponse);
                }
            }

            if (parsed == null)
            {
                parsed = processedResponse;
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "parseResponse", "Unable to parse response", ex);
            parsed = null;
        }

        if (parsed == null)
        {
            parsed = rawResponse;
        }

        return parsed;
    }

    private static void logResponseBody(@Nullable final byte[] body, @Nullable final String contentEncoding)
    {
        try
        {
            if (UULog.LOGGING_ENABLED)
            {
                if (body != null)
                {
                    if (contentEncoding != null && contentEncoding.contains("gzip"))
                    {
                        UULog.debug(UUHttp.class, "logResponseBody", "Body is gzipped");
                    }
                    else
                    {
                        String bodyAsString = new String(body, "UTF-8");
                        UULog.debug(UUHttp.class, "logResponseBody", bodyAsString);
                    }
                }
                else
                {
                    UULog.debug(UUHttp.class, "logResponseBody", "Body is NULL");
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUHttp.class, "logResponseBody", ex);
        }
    }
}
