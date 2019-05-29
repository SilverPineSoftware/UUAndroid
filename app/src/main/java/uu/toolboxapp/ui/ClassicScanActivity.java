package uu.toolboxapp.ui;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolbox.bluetooth.UUBluetoothBroadcastReceiver;
import uu.toolbox.bluetooth.UUBluetoothDeviceFilter;
import uu.toolbox.bluetooth.UUBluetoothDeviceScanner;
import uu.toolbox.bluetooth.UUBluetoothPowerManager;
import uu.toolbox.bluetooth.UUClassicBluetoothScanner;
import uu.toolbox.core.UUPermissions;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;
import uu.toolboxapp.R;

public class ClassicScanActivity extends AppCompatActivity
{
    private BluetoothDeviceAdapter tableAdapter;
    private Button scanButton;
    private Button toggleBtButton;
    private HashMap<String, BluetoothDevice> cachedDevices = new HashMap<>();
    private final ArrayList<BluetoothDevice> tableData = new ArrayList<>();
    private long lastUiRefreshTime = 0;

    private UUClassicBluetoothScanner scanner;

    private UUBluetoothPowerManager bluetoothPowerManager;
    private UUBluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Context ctx = getApplicationContext();
        bluetoothBroadcastReceiver = new UUBluetoothBroadcastReceiver(ctx);
        bluetoothPowerManager = new UUBluetoothPowerManager(ctx);
        scanner = new UUClassicBluetoothScanner(ctx);

        scanButton = findViewById(R.id.scanButton);
        scanButton.setEnabled(false);

        toggleBtButton = findViewById(R.id.toggleBluetoothButton);

        ListView listView = findViewById(R.id.list_view);
        tableAdapter = new BluetoothDeviceAdapter(this, tableData);
        listView.setAdapter(tableAdapter);

        updateToggleBtButtonState();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        UUPermissions.requestPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, 57, new UUPermissions.UUPermissionDelegate()
        {
            @Override
            public void onPermissionRequestComplete(String permission, boolean granted)
            {
                scanButton.setEnabled(granted);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        UUPermissions.handleRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void onBluetoothStateChanged(@Nullable Integer state)
    {
        updateToggleBtButtonState();
    }

    private void updateToggleBtButtonState()
    {
        if (bluetoothPowerManager.isBluetoothOn())
        {
            toggleBtButton.setText(R.string.turn_bluetooth_off);
        }
        else
        {
            toggleBtButton.setText(R.string.turn_bluetooth_on);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {

            case R.id.action_scan_settings:
            {

                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onScanClicked(View v)
    {
        if (scanner.isScanning())
        {
            scanner.stopScanning();
            scanButton.setText(R.string.scan);
        }
        else
        {
            startScanning();
            scanButton.setText(R.string.stop);
        }
    }

    public void onToggleBluetoothClicked(View v)
    {
        if (bluetoothPowerManager.isBluetoothOn())
        {
            bluetoothPowerManager.turnBluetoothOff(new UUBluetoothPowerManager.PowerOffDelegate()
            {
                @Override
                public void onBluetoothPoweredOff(boolean success)
                {
                    updateToggleBtButtonState();
                }
            });
        }
        else
        {
            bluetoothPowerManager.turnBluetoothOn(new UUBluetoothPowerManager.PowerOnDelegate()
            {
                @Override
                public void onBluetoothPoweredOn(boolean success)
                {
                    updateToggleBtButtonState();
                }
            });
        }
    }

    private void startScanning()
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                cachedDevices.clear();
                lastUiRefreshTime = System.currentTimeMillis();
                tableAdapter.notifyDataSetChanged();
            }
        });


        ArrayList<UUBluetoothDeviceFilter> filters = new ArrayList<>();

        ArrayList<String> macList = new ArrayList<>();

        scanner.startScanning(filters, new UUBluetoothDeviceScanner.Listener()
        {
            @Override
            public void onDeviceFound(@NonNull UUBluetoothDeviceScanner scanner, @NonNull BluetoothDevice device)
            {
                handleDeviceFound(device);
            }
        });
    }

    private void handleDeviceFound(@NonNull final BluetoothDevice device)
    {
        UULog.debug(getClass(), "handleDeviceFound", "deviceFound, class: " + device.getClass());

        final long timeSinceLastUpdate = System.currentTimeMillis() - lastUiRefreshTime;
        if (true) //timeSinceLastUpdate > 10)
        {
            UUThread.runOnMainThread(new Runnable()
            {
                @Override
                public void run()
                {
                    cachedDevices.put(device.getAddress(), device);

                    tableData.clear();
                    tableData.addAll(cachedDevices.values());

                    Collections.sort(tableData, new Comparator<BluetoothDevice>()
                    {
                        @Override
                        public int compare(BluetoothDevice o1, BluetoothDevice o2)
                        {
                            return o1.getAddress().compareTo(o2.getAddress());
                        }
                    });

                    lastUiRefreshTime = System.currentTimeMillis();
                    tableAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void onRowClicked(final BluetoothDevice device)
    {
        scanner.stopScanning();

        Intent intent = new Intent(this, ClassicDeviceDetailActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);


        /*
        UUBluetoothSession session  = new UUBluetoothSession(getApplicationContext(), device);
        session.startSppSession(60000, false, new UUBluetoothSessionErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSession session, @Nullable UUBluetoothSession.UUBluetoothSessionError error)
            {
                UULog.debug(getClass(), "onConnected", "SPP Session is " + (error == null ? "ready" : "NOT ready"));
            }
        });
        */
    }

    private class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice>
    {
        private BluetoothDeviceAdapter(Context context, ArrayList<BluetoothDevice> data)
        {
            super(context, R.layout.classic_device_list_row, data);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.classic_device_list_row, parent, false);
            }

            TextView nameLabel = view.findViewById(R.id.name_label);
            TextView macLabel = view.findViewById(R.id.mac_label);
            TextView rssiLabel = view.findViewById(R.id.rssi_label);
            TextView connectionLabel = view.findViewById(R.id.connection_state_label);
            TextView timeLabel = view.findViewById(R.id.time_label);

            final BluetoothDevice rowData = getItem(position);
            if (rowData != null)
            {
                nameLabel.setText(rowData.getName());
                macLabel.setText(rowData.getAddress());
                rssiLabel.setText(UUBluetooth.deviceTypeToString(rowData.getType()));
                timeLabel.setText(rowData.getBluetoothClass().toString());
                connectionLabel.setText(UUBluetooth.bondStateToString(rowData.getBondState()));

                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onRowClicked(rowData);
                    }
                });
            }

            return view;
        }
    }
}
