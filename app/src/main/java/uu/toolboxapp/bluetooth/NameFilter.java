package uu.toolboxapp.bluetooth;

import android.support.annotation.NonNull;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFilter;
import uu.toolbox.core.UUString;

@SuppressWarnings("unused")
public class NameFilter implements UUPeripheralFilter
{
    private String name;

    public NameFilter(String name)
    {
        this.name = name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public UUPeripheralFilter.Result shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
    {
        if (UUString.areEqual(name, peripheral.getName()))
        {
            return Result.Discover;
        }
        else
        {
            return Result.IgnoreOnce;
        }
    }
}