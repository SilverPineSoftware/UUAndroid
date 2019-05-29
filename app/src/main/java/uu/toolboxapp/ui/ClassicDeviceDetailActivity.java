package uu.toolboxapp.ui;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetoothError;
import uu.toolbox.bluetooth.UUBluetoothPowerManager;
import uu.toolbox.bluetooth.UUBluetoothSpp;
import uu.toolbox.bluetooth.UUBluetoothSppErrorDataDelegate;
import uu.toolbox.bluetooth.UUBluetoothSppErrorDelegate;
import uu.toolbox.core.UUThread;
import uu.toolbox.ui.UUActivity;
import uu.toolboxapp.R;

public class ClassicDeviceDetailActivity extends AppCompatActivity
{
    private TextView nameLabel;
    private TextView macLabel;
    //private TextView rssiLabel;
    //private TextView connectionStateLabel;

    private Button pairButton;
    private Button connectButton;
    private Button discoverServicesButton;
    private Button readRssiButton;
    private Button pollRssiButton;

    private BluetoothDevice device;
    private UUBluetoothSpp session;
    private final ArrayList<UUID> tableData = new ArrayList<>();
    private ClassicDeviceDetailActivity.ClassicServiceAdapter tableAdapter;

    private UUBluetoothPowerManager bluetoothPowerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_device_detail);

        bluetoothPowerManager = new UUBluetoothPowerManager(getApplicationContext());

        device = null;
        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra("device"))
            {
                device = intent.getParcelableExtra("device");
            }
        }

        if (device == null)
        {
            UUActivity.showToast(this, getClass().getName() + " requires a device be passed with launch intent", Toast.LENGTH_SHORT);
            finish();
        }

        session = new UUBluetoothSpp(getApplicationContext(), device);

        setTitle(device.getName());// peripheral.getName());

        nameLabel = UUActivity.findTextViewById(this, R.id.name);
        macLabel = UUActivity.findTextViewById(this, R.id.address);
        //rssiLabel = UUActivity.findTextViewById(this, R.id.rssi);
        //connectionStateLabel = UUActivity.findTextViewById(this, R.id.state);

        pairButton = findViewById(R.id.pair_button);
        connectButton = findViewById(R.id.connect_button);
        discoverServicesButton = findViewById(R.id.discover_services_button);
        //readRssiButton = UUActivity.findButtonById(this, R.id.read_rssi_button);
        //pollRssiButton = UUActivity.findButtonById(this, R.id.poll_rssi_button);

        tableAdapter = new ClassicDeviceDetailActivity.ClassicServiceAdapter(this, tableData);
        ListView listView = UUActivity.findListViewById (this, R.id.services_list_view);
        if (listView != null)
        {
            listView.setAdapter(tableAdapter);
        }

        updateUi();
    }

    private void updateHeader()
    {
        nameLabel.setText(device.getName());
        macLabel.setText(device.getAddress());
        //rssiLabel.setText(String.format(Locale.US, "%d", peripheral.getRssi()));
        //connectionStateLabel.setText(peripheral.getConnectionState(getApplicationContext()).toString());
    }

    private void updateButtons()
    {
        boolean isPaired = session.isPaired();
        boolean hasSpp = session.hasSppService();
        boolean sppConnected = session.isSppConnected();

        pairButton.setEnabled(true);
        discoverServicesButton.setEnabled(true);
        connectButton.setEnabled(hasSpp);

        pairButton.setText(isPaired ? R.string.unpair : R.string.pair);
        connectButton.setText(sppConnected ? R.string.disconnect_spp : R.string.connect_spp);

        /*
        UUPeripheral.ConnectionState state = peripheral.getConnectionState(getApplicationContext());

        switch (state)
        {
            case Connecting:
            {
                connectButton.setText(R.string.connecting);
                break;
            }

            case Connected:
            {
                connectButton.setText(R.string.disconnect);
                break;
            }

            case Disconnecting:
            {
                connectButton.setText(R.string.disconnecting);
                break;
            }

            default:
            case Disconnected:
            {
                connectButton.setText(R.string.connect);
                break;
            }
        }

        // Peripheral should never get 'stuck' in intermediate state
        connectButton.setEnabled((state == UUPeripheral.ConnectionState.Connected || state == Disconnected));

        boolean isConnected = (state == UUPeripheral.ConnectionState.Connected);
        discoverServicesButton.setEnabled(isConnected);
        readRssiButton.setEnabled(isConnected);
        pollRssiButton.setEnabled(isConnected);

        if (peripheral.isPollingForRssi())
        {
            pollRssiButton.setText(R.string.stop_polling);
        }
        else
        {
            pollRssiButton.setText(R.string.poll_rssi);
        }*/
    }

    private void updateTable()
    {
        tableData.clear();
        tableData.addAll(session.discoveredServiceUuids());
        tableAdapter.notifyDataSetChanged();
    }

    private void updateUi()
    {
        UUThread.runOnMainThread(new Runnable()
        {
            @Override
            public void run()
            {
                updateHeader();
                updateButtons();
                updateTable();
            }
        });
    }

    public void onPairClicked(View view)
    {
        if (session.isPaired())
        {
            unpair();
        }
        else
        {
            pair();
        }
    }

    private void pair()
    {
        session.pair(60000, new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable final UUBluetoothError error)
            {
                updateUi();

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (error == null)
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Successfully paired", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Failed to pair: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void unpair()
    {
        session.unpair(60000, new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable final UUBluetoothError error)
            {
                updateUi();

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (error == null)
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Successfully unpaired", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Failed to unpair: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void onConnectClicked(View view)
    {
        if (session.isSppConnected())
        {
            disconnectSpp();
        }
        else
        {
            connectSpp();
        }
    }

    private void connectSpp()
    {
        session.connectSpp(60000, false, new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable final UUBluetoothError error)
            {
                updateUi();

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (error == null)
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Spp Connected", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Spp failed to connect: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void disconnectSpp()
    {
        session.disconnectSpp(60000, new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable final UUBluetoothError error)
            {
                updateUi();

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (error == null)
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Spp Disconnected", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Spp failed to disconnect: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void onDiscoverServicesClicked(View view)
    {
        session.discoverServices(60000, new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable final UUBluetoothError error)
            {
                updateUi();

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (error == null)
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Successfully discovered services", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ClassicDeviceDetailActivity.this, "Failed to discover services: " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void onServiceClicked(final @NonNull UUID uuid)
    {
        byte[] tx = new byte[] { 0x00, 0x01, 0x02, 0x03 };

        session.writeSppData(tx, 30000, new UUBluetoothSppErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUBluetoothSpp session, @Nullable UUBluetoothError error)
            {
                if (error == null)
                {
                    session.readSppData(100, 30000, null, new UUBluetoothSppErrorDataDelegate()
                    {
                        @Override
                        public void onComplete(@NonNull UUBluetoothSpp session, byte[] data, @Nullable UUBluetoothError error)
                        {

                        }
                    });
                }
            }
        });

    }

    class ClassicServiceAdapter extends ArrayAdapter<UUID>
    {
        public ClassicServiceAdapter(Context context, ArrayList<UUID> data)
        {
            super(context, R.layout.classic_service_list_row, data);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.classic_service_list_row, parent, false);
            }

            final UUID rowData = getItem(position);
            if (rowData != null)
            {
                ClassicServiceListRow row = new ClassicServiceListRow(view);
                row.update(rowData);

                view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onServiceClicked(rowData);
                    }
                });
            }

            return view;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.peripheral_detail_menu, menu);
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

            case R.id.action_power_bluetooth_off:
            {
                onPowerBluetoothOff();
                break;
            }

            case R.id.action_power_bluetooth_on:
            {
                onPowerBluetoothOn();
                break;
            }

            case R.id.action_power_cycle_bluetooth:
            {
                onPowerCycleBluetooth();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void onPowerBluetoothOff()
    {
        bluetoothPowerManager.turnBluetoothOff(new UUBluetoothPowerManager.PowerOffDelegate()
        {
            @Override
            public void onBluetoothPoweredOff(boolean success)
            {
                if (success)
                {
                    Toast.makeText(ClassicDeviceDetailActivity.this, "Bluetooth Powered Off", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ClassicDeviceDetailActivity.this, "Bluetooth NOT Powered Off", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onPowerBluetoothOn()
    {
        bluetoothPowerManager.turnBluetoothOn(new UUBluetoothPowerManager.PowerOnDelegate()
        {
            @Override
            public void onBluetoothPoweredOn(boolean success)
            {
                if (success)
                {
                    Toast.makeText(ClassicDeviceDetailActivity.this, "Bluetooth Powered On", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ClassicDeviceDetailActivity.this, "Bluetooth NOT Powered On", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onPowerCycleBluetooth()
    {
        bluetoothPowerManager.powerCycleBluetooth(new UUBluetoothPowerManager.PowerCycleDelegate()
        {
            @Override
            public void onBluetoothPoweredCycled(boolean success)
            {
                if (success)
                {
                    Toast.makeText(ClassicDeviceDetailActivity.this, "Bluetooth Powered Cycled", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(ClassicDeviceDetailActivity.this, "Bluetooth NOT Power Cycled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}