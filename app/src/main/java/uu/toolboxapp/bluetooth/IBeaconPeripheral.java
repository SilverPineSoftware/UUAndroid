package uu.toolboxapp.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.UUID;

import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFilter;
import uu.toolbox.core.UUData;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

@SuppressWarnings("unused")
public class IBeaconPeripheral extends UUPeripheral
{
    private static final int APPLE_COMPANY_ID = 0x4C00;
    private static final int APPLE_BEACON_TYPE = 0x0215;

    private boolean isValidAppleBeacon;
    private byte[] proximityUuid;
    private int major;
    private int minor;
    private int measuredPower;

    private IBeaconPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
    {
        super(device, rssi, scanRecord);
    }

    @Override
    protected void parseManufacturingData(@NonNull byte[] manufacturingData)
    {
        isValidAppleBeacon = false;
        proximityUuid = null;
        major = 0;
        minor = 0;
        measuredPower = 0;

        if (manufacturingData.length == 25)
        {
            int index = 0;
            ByteBuffer bb = ByteBuffer.wrap(manufacturingData);

            int companyId = bb.getShort(index);
            index += 2;

            int beaconType = bb.getShort(index);
            index += 2;

            isValidAppleBeacon = (companyId == APPLE_COMPANY_ID && beaconType == APPLE_BEACON_TYPE);

            proximityUuid = UUData.subData(manufacturingData, index, 16);
            index += proximityUuid.length;
            UULog.debug(getClass(), "parseMfgData", getAddress() + ", Proximity UUID: " + getProximityUuid() + ", " + UUString.byteToHex(proximityUuid));

            major = bb.getShort(index);
            index += 2;

            minor = bb.getShort(index);
            index += 2;

            measuredPower = manufacturingData[index];
        }
    }

    private boolean isValidAppleBeacon()
    {
        return isValidAppleBeacon;
    }

    @NonNull
    public String getProximityUuid()
    {
        try
        {
            ByteBuffer bb = ByteBuffer.wrap(proximityUuid);
            long high = bb.getLong();
            long low = bb.getLong();
            UUID uuid = new UUID(high, low);
            return uuid.toString().toUpperCase();
        }
        catch (Exception ex)
        {
            return UUString.byteToHex(proximityUuid);
        }
    }

    public int getMajor()
    {
        return major;
    }

    public int getMinor()
    {
        return minor;
    }

    public int getMeasuredPower()
    {
        return measuredPower;
    }

    public static class IBeaconPeripheralFactory extends CachingPeripheralFactory<IBeaconPeripheral>
    {
        @NonNull
        IBeaconPeripheral createPeripheral(final @NonNull BluetoothDevice device, final int rssi, final @NonNull byte[] scanRecord)
        {
            return new IBeaconPeripheral(device, rssi, scanRecord);
        }
    }

    public static class IBeaconFilter implements UUPeripheralFilter
    {
        public IBeaconFilter()
        {
        }

        @Override
        public UUPeripheralFilter.Result shouldDiscoverPeripheral(@NonNull UUPeripheral peripheral)
        {
            if (peripheral instanceof IBeaconPeripheral)
            {
                IBeaconPeripheral cp = (IBeaconPeripheral)peripheral;
                if (cp.isValidAppleBeacon())
                {
                    return Result.Discover;
                }
            }

            return Result.IgnoreForever;
        }
    }
}
