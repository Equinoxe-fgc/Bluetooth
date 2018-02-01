package com.equinoxe.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;

    BluetoothAdapter mBluetoothAdapter;
    Button btnOnOff;
    TextView out;
    ListView listView;
    ArrayAdapter<String> mPairedDeviceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        out = (TextView)findViewById(R.id.out);
        btnOnOff = (Button)findViewById(R.id.btnOnOff);
        listView = (ListView)findViewById(R.id.listView);

        mPairedDeviceArrayAdapter = new ArrayAdapter<String>(this, R.layout.text_view_list_item);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            out.append("Dispositivo no soportado");
        }

        if (!mBluetoothAdapter.isEnabled())
            btnOnOff.setText("On");
        else {
            btnOnOff.setText("Off");
            clickOnOff(null);
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mPairedDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    public void clickOnOff(View v) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else {
            mBluetoothAdapter.disable();
            mPairedDeviceArrayAdapter.clear();
            btnOnOff.setText("On");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode != RESULT_CANCELED) {
            if (mBluetoothAdapter.isEnabled()) {
                btnOnOff.setText("Off");
                //rellenarListaVinculados();
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                this.registerReceiver(mReceiver, filter);

                mBluetoothAdapter.startDiscovery();
            }
        }
    }

    public void rellenarListaVinculados() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        mPairedDeviceArrayAdapter.clear();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDeviceArrayAdapter.add(device.getName() + "\n"+ device.getAddress());
            }
        } else {
            mPairedDeviceArrayAdapter.add("Ningún dispositivo emparejado");
        }
        listView.setAdapter(mPairedDeviceArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }
        });
    }
}
