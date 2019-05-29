package uu.toolboxapp.bluetooth;

import android.support.annotation.NonNull;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFilter;
import uu.toolbox.core.UUString;

@SuppressWarnings("unused")
public class NullNameFilter implements UUPeripheralFilter
{
    @Override
    public Result shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
    {
        if (UUString.isEmpty(peripheral.getName()))
        {
            return Result.IgnoreForever;
        }
        else
        {
            return Result.Discover;
        }
    }
}