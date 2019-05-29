package uu.toolbox.network;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * UUHttpUtils
 *
 * Useful Utilities - A bag of helper methods for the UUHttpClient class
 *
 */
@SuppressWarnings("unused")
public final class UUHttpUtils
{
    protected static URL safeCreateUrl(final String url)
    {
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException ex)
        {
            return null;
        }
    }

    protected static String parseEndpoint(final UUHttpResponse response)
    {
        String[] urlParts = response.getRequest().getURL().split("/");
        String endpoint = "";
        if (urlParts.length > 0)
        {
            endpoint = urlParts[urlParts.length - 1];
        }

        return endpoint;
    }
}