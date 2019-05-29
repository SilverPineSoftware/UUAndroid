package uu.toolboxapp.ui;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import uu.toolbox.bluetooth.UUBluetooth;
import uu.toolbox.bluetooth.UUBluetoothError;
import uu.toolbox.bluetooth.UUDescriptorDelegate;
import uu.toolbox.bluetooth.UUPeripheral;
import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;
import uu.toolbox.ui.UUActivity;
import uu.toolbox.ui.UUListView;
import uu.toolboxapp.R;

/**
 * Created by ryandevore on 12/29/16.
 */

public class CharacteristicDetailActivity extends AppCompatActivity
{
    private UUPeripheral peripheral;
    private BluetoothGattService service;
    private BluetoothGattCharacteristic characteristic;
    private final ArrayList<BluetoothGattDescriptor> tableData = new ArrayList<>();
    private ListView listView;
    private CharacteristicDetailActivity.BtleDescriptorAdapter tableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristic_detail);

        Intent intent = getIntent();
        if (intent != null)
        {
            if (intent.hasExtra("peripheral"))
            {
                peripheral = intent.getParcelableExtra("peripheral");
            }

            if (intent.hasExtra("serviceUuid"))
            {
                String serviceUuidString = intent.getStringExtra("serviceUuid");
                if (UUString.isNotEmpty(serviceUuidString))
                {
                    UUID serviceUuid = UUID.fromString(serviceUuidString);
                    if (serviceUuid != null && peripheral != null)
                    {
                        for (BluetoothGattService svc : peripheral.discoveredServices())
                        {
                            if (serviceUuid.toString().compareToIgnoreCase(svc.getUuid().toString()) == 0)
                            {
                                service = svc;
                                break;
                            }
                        }
                    }
                }
            }

            if (service != null)
            {
                if (intent.hasExtra("characteristicUuid"))
                {
                    String characteristicUuidString = intent.getStringExtra("characteristicUuid");
                    if (UUString.isNotEmpty(characteristicUuidString))
                    {
                        UUID characteristicUuid = UUID.fromString(characteristicUuidString);
                        if (characteristicUuid != null && peripheral != null)
                        {
                            for (BluetoothGattCharacteristic charTmp : service.getCharacteristics())
                            {
                                if (characteristicUuid.toString().compareToIgnoreCase(charTmp.getUuid().toString()) == 0)
                                {
                                    characteristic = charTmp;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (peripheral == null)
        {
            UUActivity.showToast(this, getClass().getName() + " requires a peripheral be passed with launch intent", Toast.LENGTH_SHORT);
            finish();
            return;
        }

        if (service == null)
        {
            UUActivity.showToast(this, getClass().getName() + " requires a service be passed with launch intent", Toast.LENGTH_SHORT);
            finish();
            return;
        }

        if (characteristic == null)
        {
            UUActivity.showToast(this, getClass().getName() + " requires a characteristic be passed with launch intent", Toast.LENGTH_SHORT);
            finish();
            return;
        }

        setTitle(peripheral.getName());

        tableAdapter = new CharacteristicDetailActivity.BtleDescriptorAdapter(this, tableData);
        listView = UUActivity.findListViewById (this, R.id.descriptor_list_view);
        if (listView != null)
        {
            listView.setAdapter(tableAdapter);
        }

        updateHeader();
        //updateButtons();

        tableData.addAll(characteristic.getDescriptors());
        tableAdapter.notifyDataSetChanged();
    }

    private void updateHeader()
    {
        View view = findViewById(R.id.info_container);
        ServiceListRow header = new ServiceListRow(view);
        header.update(service);
    }

    private void readData(final @NonNull BluetoothGattDescriptor descriptor, final int position)
    {
        peripheral.readDescriptor(descriptor, 30000, new UUDescriptorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattDescriptor descriptor, @Nullable UUBluetoothError error)
            {
                UULog.debug(getClass(), "readData.onComplete",
                    "Read Data complete, descriptor: " + descriptor.getUuid() +
                    ", error: " + error +
                    ", data: " + UUString.byteToHex(descriptor.getValue()));

                UUListView.reloadRow(listView, position);
            }
        });
    }

    private void writeData(final @NonNull BluetoothGattDescriptor descriptor, final byte[] data, final int position)
    {
        peripheral.writeDescriptor(descriptor, data, 30000, new UUDescriptorDelegate()
        {
            @Override
            public void onComplete(@NonNull UUPeripheral peripheral, @NonNull BluetoothGattDescriptor characteristic, @Nullable UUBluetoothError error)
            {
                UULog.debug(getClass(), "writeData.onComplete",
                    "Write Data complete, descriptor: " + characteristic.getUuid() +
                    ", error: " + error);

                UUListView.reloadRow(listView, position);
            }
        });
    }

    private void dismissKeyboard(final @NonNull EditText editText)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    class BtleDescriptorAdapter extends ArrayAdapter<BluetoothGattDescriptor>
    {
        public BtleDescriptorAdapter(Context context, ArrayList<BluetoothGattDescriptor> data)
        {
            super(context, R.layout.descriptor_list_row, data);
        }

        @Override
        public @NonNull View getView(final int position, View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (view == null)
            {
                view = inflater.inflate(R.layout.descriptor_list_row, parent, false);
            }

            final TextView idLabel = (TextView)view.findViewById(R.id.id_label);
            final TextView nameLabel = (TextView)view.findViewById(R.id.name_label);
            final TextView propertiesLabel = (TextView)view.findViewById(R.id.properties_label);
            final TextView isNotifyingLabel = (TextView)view.findViewById(R.id.is_notifying_label);
            final EditText dataEditBox = (EditText) view.findViewById(R.id.data_edit_box);
            final Button toggleNotifyLink = (Button) view.findViewById(R.id.toggle_notify_button);
            final Button readDataLink = (Button) view.findViewById(R.id.read_data_button);
            final Button writeDataLink = (Button) view.findViewById(R.id.write_data_button);
            final Button writeDataWithoutResponseLink = (Button) view.findViewById(R.id.write_data_without_response_button);

            final BluetoothGattDescriptor rowData = getItem(position);
            if (rowData != null)
            {
                UUID uuid = rowData.getUuid();
                if (uuid != null)
                {
                    idLabel.setText(uuid.toString());
                }
                else
                {
                    idLabel.setText(R.string.null_text);
                }

                nameLabel.setText(UUBluetooth.bluetoothSpecName(uuid));

                /*
                propertiesLabel.setText(UUBluetooth.characteristicPropertiesToString(rowData.getProperties()));


                if (isNotifying(rowData))
                {
                    isNotifyingLabel.setText(R.string.yes);
                    toggleNotifyLink.setText(R.string.turn_notify_off);
                }
                else
                {
                    isNotifyingLabel.setText(R.string.no);
                    toggleNotifyLink.setText(R.string.turn_notify_on);
                }*/

                dataEditBox.setText(UUString.byteToHex(rowData.getValue()));

/*
                boolean canToggleNotify = UUBluetooth.canToggleNotify(rowData);
                boolean canReadData = UUBluetooth.canReadData(rowData);
                boolean canWriteData = UUBluetooth.canWriteData(rowData);
                boolean canWriteDataWithoutResponse = UUBluetooth.canWriteWithoutResponse(rowData);

                UULog.debug(getClass(), "getView",
                    "canToggleNotify: " + canToggleNotify +
                    ", canReadData: " + canReadData +
                    ", canWriteData: " + canWriteData +
                    ", canWriteDataWithoutResponse: " + canWriteDataWithoutResponse);

                toggleNotifyLink.setEnabled(canToggleNotify);
                readDataLink.setEnabled(canReadData);
                writeDataLink.setEnabled(canWriteData);
                writeDataWithoutResponseLink.setEnabled(canWriteDataWithoutResponse);
*/
                /*
                toggleNotifyLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setNotify(rowData, position);
                    }
                });*/

                readDataLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        readData(rowData, position);
                    }
                });

                writeDataLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dismissKeyboard(dataEditBox);
                        writeData(rowData, UUString.hexToByte(dataEditBox.getText().toString()), position);
                    }
                });

                /*
                writeDataWithoutResponseLink.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dismissKeyboard(dataEditBox);
                        writeDataWithoutResponse(rowData, UUString.hexToByte(dataEditBox.getText().toString()), position);
                    }
                });*/
            }

            return view;
        }
    }
}