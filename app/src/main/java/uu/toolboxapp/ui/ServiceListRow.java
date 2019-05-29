package uu.toolboxapp.ui;

import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolboxapp.R;

public class ServiceListRow
{
    private TextView idLabel;
    private TextView nameLabel;
    private TextView isPrimaryLabel;

    public ServiceListRow(final @NonNull View parent)
    {
        idLabel = parent.findViewById(R.id.id_label);
        nameLabel = parent.findViewById(R.id.name_label);
        isPrimaryLabel = parent.findViewById(R.id.is_primary_label);
    }

    public void update(final @NonNull BluetoothGattService service)
    {
        UUID uuid = service.getUuid();
        if (uuid != null)
        {
            idLabel.setText(uuid.toString());
        }
        else
        {
            idLabel.setText(R.string.null_text);
        }

        nameLabel.setText(UUBluetooth.bluetoothSpecName(uuid));

        int serviceType = service.getType();
        switch(serviceType)
        {
            case BluetoothGattService.SERVICE_TYPE_PRIMARY:
                isPrimaryLabel.setText(R.string.yes);
                break;

            case BluetoothGattService.SERVICE_TYPE_SECONDARY:
                isPrimaryLabel.setText(R.string.no);
                break;

            default:
                isPrimaryLabel.setText(String.valueOf(serviceType));
                break;
        }
    }
}
