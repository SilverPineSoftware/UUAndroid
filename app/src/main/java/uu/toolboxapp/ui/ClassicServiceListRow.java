package uu.toolboxapp.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolboxapp.R;

public class ClassicServiceListRow
{
    private TextView idLabel;
    private TextView nameLabel;

    public ClassicServiceListRow(final @NonNull View parent)
    {
        idLabel = parent.findViewById(R.id.id_label);
        nameLabel = parent.findViewById(R.id.name_label);
    }

    public void update(final @NonNull UUID uuid)
    {
        idLabel.setText(uuid.toString());
        nameLabel.setText(UUBluetooth.bluetoothSpecName(uuid));
    }
}
