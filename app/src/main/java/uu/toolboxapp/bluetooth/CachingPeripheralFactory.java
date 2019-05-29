package uu.toolboxapp.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.util.HashMap;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFactory;

abstract class CachingPeripheralFactory<T extends UUPeripheral> implements UUPeripheralFactory<T>
{
    private HashMap<String, T> cachedPeripherals = new HashMap<>();

    @NonNull
    public T fromScanResult(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
    {
        return findCachedPeripheral(device, rssi, scanRecord);
    }

    private synchronized T findCachedPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
    {
        T peripheral = null;

        String lookupKey = device.getAddress();
        if (cachedPeripherals.containsKey(lookupKey))
        {
            peripheral = cachedPeripherals.get(lookupKey);
            peripheral.updateAdvertisement(device, rssi, scanRecord);
        }
        else
        {
            peripheral = createPeripheral(device, rssi, scanRecord);
            cachedPeripherals.put(lookupKey, peripheral);
        }

        return peripheral;
    }

    @NonNull
    abstract T createPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord);
}

