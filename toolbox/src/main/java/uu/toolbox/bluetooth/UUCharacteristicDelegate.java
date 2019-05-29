package uu.toolbox.bluetooth;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for delivering BTLE characteristic specific async events to callers
 */
public interface UUCharacteristicDelegate
{
    /**
     * Callback invoked when a BTLE event is completed.
     *
     * @param peripheral the peripheral being interacted with
     * @param characteristic the characteristic being interacted with
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUPeripheral peripheral, final @NonNull BluetoothGattCharacteristic characteristic, final @Nullable UUBluetoothError error);
}