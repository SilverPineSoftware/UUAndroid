package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interface for delivering async results from a UUPeripheral action
 */
public interface UUPeripheralErrorDelegate
{
    /**
     * Callback invoked when a peripheral action is completed.
     *
     * @param peripheral the peripheral being interacted with
     * @param error an error if one occurs
     */
    void onComplete(final @NonNull UUPeripheral peripheral, final @Nullable UUBluetoothError error);
}
