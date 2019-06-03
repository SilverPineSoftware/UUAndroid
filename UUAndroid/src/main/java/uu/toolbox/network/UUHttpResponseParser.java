package uu.toolbox.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface UUHttpResponseParser
{
    @Nullable
    Object parseResponse(@NonNull final UUHttpResponse response);
}
