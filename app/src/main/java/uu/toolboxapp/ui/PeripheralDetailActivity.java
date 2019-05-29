package uu.toolboxapp.ui;

import android.bluetooth.BluetoothGattService;
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
import java.util.Locale;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolbox.bluetooth.UUBluetoothError;
import uu.toolbox.bluetooth.UUBluetoothPowerManager;
import uu.toolbox.bluetooth.UUConnectionDelegate;
import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.bluetooth.UUPeripheralDelegate;
import uu.toolbox.bluetooth.UUPeripheralErrorDelegate;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;
import uu.toolbox.ui.UUActivity;
import uu.toolboxapp.R;

import static uu.toolbox.bluetooth.UUPeripheral.ConnectionState.Disconnected;

public class PeripheralDetailActivity extends AppCompatActivity
{
    private TextView nameLabel;
    private TextView macLabel;
    private TextView rssiLabel;
    private TextView connectionStateLabel;

    private Button connectButton;
    private Button discoverServicesButton;
    private Button readRssiButton;
    private Button pollRssiButton;

    private UUPeripheral peripheral;
    private final ArrayList<BluetoothGattService> tableData = new ArrayList<>();
    private PeripheralDetailActivity.BtleServiceAdapter tableAdapter;

    private UUBluetoothPowerManager bluetoothPowerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral_detail);

        bluetoothPowerManager = new UUBluetoothPowerManager(getApplicationContext());

        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra("peripheral"))
            {
                peripheral = intent.getParcelableExtra("peripheral");
            }
        }

        if (peripheral == null)
        {
            UUActivity.showToast(this, getClass().getName() + " requires a peripheral be passed with launch intent", Toast.LENGTH_SHORT);
            finish();
        }

        setTitle(peripheral.getName());

        nameLabel = UUActivity.findTextViewById(this, R.id.name);
        macLabel = UUActivity.findTextViewById(this, R.id.address);
        rssiLabel = UUActivity.findTextViewById(this, R.id.rssi);
        connectionStateLabel = UUActivity.findTextViewById(this, R.id.state);

        connectButton = UUActivity.findButtonById(this, R.id.connect_button);
        discoverServicesButton = UUActivity.findButtonById(this, R.id.discover_services_button);
        readRssiButton = UUActivity.findButtonById(this, R.id.read_rssi_button);
        pollRssiButton = UUActivity.findButtonById(this, R.id.poll_rssi_button);

        tableAdapter = new PeripheralDetailActivity.BtleServiceAdapter(this, tableData);
        ListView listView = UUActivity.findListViewById (this, R.id.services_list_view);
        if (listView != null)
        {
            listView.setAdapter(tableAdapter);
        }

        updateHeader();
        updateButtons();
    }

    private void updateHeader()
    {
        nameLabel.setText(peripheral.getName());
        macLabel.setText(peripheral.getAddress());
        rssiLabel.setText(String.format(Locale.US, "%d", peripheral.getRssi()));
        connectionStateLabel.setText(peripheral.getConnectionState(getApplicationContext()).toString());
    }

    private void updateButtons()
    {
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
        }
    }

    private void updateTable()
    {
        tableData.clear();
        tableData.addAll(peripheral.discoveredServices());
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

    public void onConnectClicked(View view)
    {
        UUPeripheral.ConnectionState connectionState = peripheral.getConnectionState(getApplicationContext());

        switch (connectionState)
        {
            case Connected:
            {
                disconnect();
                break;
            }

            case Disconnected:
            {
                connect();
                break;
            }

            default:
            {
                UULog.debug(getClass(), "onConnectClicked", "Peripheral is in between states: " + connectionState);
                break;
            }
        }
    }

    public void onReadRssiClicked(View view)
    {
        peripheral.readRssi(300000, new UUPeripheralErrorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                updateUi();
            }
        });
    }

    public void onPollRssiClicked(View view)
    {
        if (peripheral.isPollingForRssi())
        {
            peripheral.stopRssiPolling();
        }
        else
        {
            peripheral.startRssiPolling(getApplicationContext(), 500, new UUPeripheralDelegate()
            {
                @Override
                public void onComplete(@NonNull UUPeripheral peripheral)
                {
                    UULog.debug(getClass(), "rssiPoller.onTimer", "tick: " + peripheral.getRssi());
                    updateUi();
                }
            });
        }

        updateUi();
    }

    public void onDiscoverServicesClicked(View view)
    {
        peripheral.discoverServices(10000, new UUPeripheralErrorDelegate()
        {
            @Override
            public void onComplete(final @NonNull UUPeripheral peripheral, final @Nullable UUBluetoothError error)
            {
                UULog.debug(getClass(), "onDiscoverServices.onComplete", "Peripheral: " + peripheral + ", Error: " + error);

                UUThread.runOnMainThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tableData.clear();
                        tableData.addAll(peripheral.discoveredServices());
                        tableAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void connect()
    {
        UULog.debug(getClass(), "connect", "Connecting to : " + peripheral);
        UUBluetooth.connectPeripheral(this, peripheral, false, 10000, 10000, new UUConnectionDelegate()
        {
            @Override
            public void onConnected(@NonNull UUPeripheral peripheral)
            {
                UULog.debug(getClass(), "connect.onConnected", "Peripheral connected, " + peripheral);
                updateUi();
            }

            @Override
            public void onDisconnected(@NonNull UUPeripheral peripheral, @Nullable UUBluetoothError error)
            {
                UULog.debug(getClass(), "connect.onConnected", "Peripheral disconnected, " + peripheral);
                updateUi();
            }
        });

        updateUi();
    }

    private void disconnect()
    {
        UULog.debug(getClass(), "disconnect", "Disconnecting from : " + peripheral);
        UUBluetooth.disconnectPeripheral(peripheral);

        updateUi();
    }

    private void onServiceClicked(final @NonNull BluetoothGattService service)
    {
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        intent.putExtra("peripheral", peripheral);
        intent.putExtra("serviceUuid", service.getUuid().toString());
        startActivity(intent);
    }

    class BtleServiceAdapter extends ArrayAdapter<BluetoothGattService>
    {
        public BtleServiceAdapter(Context context, ArrayList<BluetoothGattService> data)
        {
            super(context, R.layout.service_list_row, data);
        }

        @Override
        public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.service_list_row, parent, false);
            }

            final BluetoothGattService rowData = getItem(position);
            if (rowData != null)
            {
                ServiceListRow row = new ServiceListRow(view);
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
                    Toast.makeText(PeripheralDetailActivity.this, "Bluetooth Powered Off", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(PeripheralDetailActivity.this, "Bluetooth NOT Powered Off", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(PeripheralDetailActivity.this, "Bluetooth Powered On", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(PeripheralDetailActivity.this, "Bluetooth NOT Powered On", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(PeripheralDetailActivity.this, "Bluetooth Powered Cycled", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(PeripheralDetailActivity.this, "Bluetooth NOT Power Cycled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}