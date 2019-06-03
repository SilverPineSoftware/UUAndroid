package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;

import uu.toolbox.logging.UULog;

/**
 * A helpful set of methods for use on the base Object class
 */
public class UUObject
{
    /**
     * Safely invokes a void/void method on an object.
     *
     * @param target object to invoke the method on
     * @param method the method name
     */
    public static void invokeMethod(@NonNull final Object target, @NonNull final String method)
    {
        try
        {
            Method m = target.getClass().getMethod(method);
            m.invoke(target);
        }
        catch (Exception ex)
        {
            UULog.error(UUObject.class, "invokeMethod", ex);
        }
    }

    /**
     * Safely invokes a void/void method on an object from the main thread.
     *
     * @param target object to invoke the method on
     * @param method the method name
     */
    public static void invokeMethodOnMainThread(@NonNull final Object target, @NonNull final String method)
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                invokeMethod(target, method);
            }
        });
    }

    @Nullable
    public static <T> T safeCast(@Nullable Class<T> type, @Nullable Object obj)
    {
        T castedObj = null;

        try
        {
            if (obj != null && type != null && obj.getClass().isAssignableFrom(type))
            {
                castedObj = type.cast(obj);
            }
        }
        catch (Exception ex)
        {
            castedObj = null;
        }

        return castedObj;
    }
}
