package uu.toolbox.network;

import android.os.AsyncTask;

import uu.toolbox.logging.UULog;

/**
 * Created by ryandevore on 4/6/14.
 */

@SuppressWarnings("unused")
class UUHttpTask extends AsyncTask<UUHttpRequest, Void, UUHttpResponse>
{
    private UUHttpDelegate _delegate;

    public UUHttpTask(final UUHttpDelegate delegate)
    {
        _delegate = delegate;
    }

    @Override
    protected UUHttpResponse doInBackground(UUHttpRequest... uuHttpClientRequests)
    {
        if (uuHttpClientRequests != null && uuHttpClientRequests.length == 1)
        {
            UUHttpRequest request = uuHttpClientRequests[0];
            UUHttpResponse response;

            int tries = 0;
            int maxTries = 3;

            do
            {
                UULog.debug(getClass(), "doInBackground", "Attempt " + tries);
                response = UUHttp.executeRequest(request);
                ++tries;
            }
            while (tries < maxTries && shouldRetry(response));

            invokeDelegate(response);
            return response;
        }
        else
        {
            return null;
        }
    }

    protected boolean shouldRetry(final UUHttpResponse response)
    {
        if (response != null)
        {
            Exception ex = response.getException();
            if (ex != null)
            {
                if (ex instanceof java.net.SocketTimeoutException)
                {
                    return false;
                }

                if (ex instanceof javax.net.ssl.SSLException ||
                        ex instanceof java.io.IOException)
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(UUHttpResponse uuHttpClientResponse)
    {
        super.onPostExecute(uuHttpClientResponse);
    }

    protected void invokeDelegate(UUHttpResponse uuHttpClientResponse)
    {
        try
        {
            if (_delegate != null)
            {
                _delegate.onCompleted(uuHttpClientResponse);
            }
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "invokeDelegate", ex);
        }
    }
}

