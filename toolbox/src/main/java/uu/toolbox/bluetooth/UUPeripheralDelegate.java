package uu.toolbox.bluetooth;

import android.support.annotation.NonNull;

/**
 * Interface for delivering async results from a UUPeripheral action
 */
public interface UUPeripheralDelegate
{
    /**
     * Callback invoked when a peripheral action is completed.
     *
     * @param peripheral the peripheral being interacted with
     */
    void onComplete(final @NonNull UUPeripheral peripheral);
}
