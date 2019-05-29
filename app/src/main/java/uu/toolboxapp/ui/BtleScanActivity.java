package uu.toolboxapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Locale;
import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetoothScanner;
import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralFactory;
import uu.toolbox.bluetooth.UUPeripheralFilter;
import uu.toolbox.core.UUInteger;
import uu.toolbox.core.UUPermissions;
import uu.toolbox.core.UUString;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;
import uu.toolboxapp.R;
import uu.toolboxapp.bluetooth.IBeaconPeripheral;
import uu.toolboxapp.bluetooth.MacFilter;

public class BtleScanActivity extends AppCompatActivity
{
    private PeripheralAdapter tableAdapter;
    private Button scanButton;
    private HashMap<String, UUPeripheral> cachedPeripherals = new HashMap<>();
    private final ArrayList<UUPeripheral> tableData = new ArrayList<>();
    private long lastUiRefreshTime;

    private UUBluetoothScanner scanner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scanner = new UUBluetoothScanner(getApplicationContext());

        scanButton = (Button)findViewById(R.id.scanButton);
        scanButton.setEnabled(false);

        ListView listView = (ListView)findViewById(R.id.list_view);
        tableAdapter = new PeripheralAdapter(this, tableData);
        listView.setAdapter(tableAdapter);
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

    private void startScanning()
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                cachedPeripherals.clear();
                lastUiRefreshTime = System.currentTimeMillis();
                tableAdapter.notifyDataSetChanged();
            }
        });


        ArrayList<UUPeripheralFilter> filters = new ArrayList<>();
        //filters.add(new NullNameFilter());
        //filters.add(new RssiFilter(-100));
        //filters.add(new IBeaconPeripheral.IBeaconFilter());

        ArrayList<String> macList = new ArrayList<>();
  
        //filters.add(new MacFilter(macList));


        //
        //

        UUID[] uuidList = new UUID[]
        {
        };

        UUPeripheralFactory factory = new IBeaconPeripheral.IBeaconPeripheralFactory();

        scanner.startScanning(factory, uuidList, filters, new UUBluetoothScanner.Listener()
        {
            @Override
            public void onPeripheralFound(@NonNull UUBluetoothScanner scanner, @NonNull UUPeripheral peripheral)
            {
                handlePeripheralFound(peripheral);
            }
        });
    }

    private void handlePeripheralFound(final @NonNull UUPeripheral peripheral)
    {
        UULog.debug(getClass(), "onPeripheralFound", "peripheralFound, class: " + peripheral.getClass());

        final long timeSinceLastUpdate = System.currentTimeMillis() - lastUiRefreshTime;
        if (timeSinceLastUpdate > 1000)
        {
            UUThread.runOnMainThread(new Runnable()
            {
                @Override
                public void run()
                {
                    cachedPeripherals.put(peripheral.getAddress(), peripheral);

                    tableData.clear();
                    tableData.addAll(cachedPeripherals.values());

                    Collections.sort(tableData, new Comparator<UUPeripheral>()
                    {
                        @Override
                        public int compare(UUPeripheral o1, UUPeripheral o2)
                        {
                            return UUInteger.compare(o2.getRssi(), o1.getRssi());
                        }
                    });

                    lastUiRefreshTime = System.currentTimeMillis();
                    tableAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void onRowClicked(final UUPeripheral peripheral)
    {
        scanner.stopScanning();

        Intent intent = new Intent(this, PeripheralDetailActivity.class);
        intent.putExtra("peripheral", peripheral);
        startActivity(intent);
    }

    private class PeripheralAdapter extends ArrayAdapter<UUPeripheral>
    {
        private PeripheralAdapter(Context context, ArrayList<UUPeripheral> data)
        {
            super(context, R.layout.peripheral_list_row, data);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.peripheral_list_row, parent, false);
            }

            TextView nameLabel = (TextView)view.findViewById(R.id.name_label);
            TextView macLabel = (TextView)view.findViewById(R.id.mac_label);
            TextView rssiLabel = (TextView)view.findViewById(R.id.rssi_label);
            TextView timeLabel = (TextView)view.findViewById(R.id.time_label);
            TextView connectionLabel = (TextView)view.findViewById(R.id.connection_state_label);
            TextView beaconRateLabel = (TextView)view.findViewById(R.id.beacon_rate_label);
            TextView mfgDataLabel = (TextView)view.findViewById(R.id.mfg_data_label);

            final UUPeripheral rowData = getItem(position);
            if (rowData != null)
            {
                nameLabel.setText(rowData.getName());
                macLabel.setText(rowData.getAddress());
                rssiLabel.setText(String.format(Locale.US, "%d", rowData.getRssi()));
                timeLabel.setText(String.format(Locale.US, "%.3f", rowData.getTimeSinceLastUpdate() / 1000.0f));
                beaconRateLabel.setText(String.format(Locale.US, "%d, %.3f", rowData.totalBeaconCount(), rowData.averageBeaconRate()));
                connectionLabel.setText(rowData.getConnectionState(getApplicationContext()).toString());
                mfgDataLabel.setText(UUString.byteToHex(rowData.getManufacturingData()));

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
