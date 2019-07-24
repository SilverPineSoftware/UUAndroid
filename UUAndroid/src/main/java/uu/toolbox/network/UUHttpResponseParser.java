package uu.toolbox.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface UUHttpResponseParser
{
    @Nullable
    Object parseResponse(@NonNull final UUHttpResponse response);
}
