package uu.toolboxapp.bluetooth;

import android.support.annotation.NonNull;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFilter;

@SuppressWarnings("unused")
public class RssiFilter implements UUPeripheralFilter
{
    private int rssiLevel;

    public RssiFilter(int rssiLevel)
    {
        this.rssiLevel = rssiLevel;
    }

    public void setRssiLevel(final int level)
    {
        rssiLevel = level;
    }

    public int getRssiLevel()
    {
        return rssiLevel;
    }

    @Override
    public UUPeripheralFilter.Result shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
    {
        if (peripheral.getRssi() > rssiLevel)
        {
            return Result.Discover;
        }
        else
        {
            return Result.IgnoreOnce;
        }
    }
}