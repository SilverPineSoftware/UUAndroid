package uu.toolboxapp.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.logging.UULog;


public class CustomPeripheral extends UUPeripheral
{
    public CustomPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
    {
        super(device, rssi, scanRecord);
    }

    @Override
    protected void parseManufacturingData(@NonNull byte[] manufacturingData)
    {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Parcelable
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        try
        {
            dest.writeString(toJsonObject().toString());
        }
        catch (Exception ex)
        {
            UULog.error(getClass(), "writeToParcel", ex);
        }
    }

    public static final Parcelable.Creator<CustomPeripheral> CREATOR
            = new Parcelable.Creator<CustomPeripheral>()
    {
        public CustomPeripheral createFromParcel(Parcel in)
        {
            return new CustomPeripheral(in);
        }

        public CustomPeripheral[] newArray(int size)
        {
            return new CustomPeripheral[size];
        }
    };

    protected CustomPeripheral(final Parcel in)
    {
        super(in);
    }



    public static class CustomPeripheralFactory extends CachingPeripheralFactory<CustomPeripheral>
    {
        @NonNull
        CustomPeripheral createPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
        {
            return new CustomPeripheral(device, rssi, scanRecord);
        }
    }
};
