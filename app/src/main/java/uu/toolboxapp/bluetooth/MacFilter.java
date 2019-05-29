package uu.toolboxapp.bluetooth;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFilter;

@SuppressWarnings("unused")
public class MacFilter implements UUPeripheralFilter
{
    private final ArrayList<String> macWhiteList = new ArrayList<>();

    public MacFilter(ArrayList<String> list)
    {
        macWhiteList.addAll(list);
    }

    public void setList(final ArrayList<String> list)
    {
        macWhiteList.clear();
        macWhiteList.addAll(list);
    }

    public ArrayList<String> getMacWhiteList()
    {
        return macWhiteList;
    }

    @Override
    public Result shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
    {
        if (macWhiteList.contains(peripheral.getAddress()))
        {
            return Result.Discover;
        }
        else
        {
            return Result.IgnoreOnce;
        }
    }
}