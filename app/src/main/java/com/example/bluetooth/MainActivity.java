package com.example.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.content.Context;
import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "bluetooth1";

    private Button btnOn,btnOff,btnVisible,btnList,btnConnect,btnSend,btnVcontacts;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of bluetooth
    // MAC FOR SAVE
    //private static String address = "20:13:10:25:42:42";
    // MAC FOR SAVE-U
    private static String address = "30:14:11:06:20:09";

    /*App creation activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btnOn = (Button)findViewById(R.id.btnOn);
        //btnOff = (Button)findViewById(R.id.btnOff);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        btnSend = (Button)findViewById(R.id.btnSend);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String numbers = sharedPref.getString("Contacts","404 not found");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Introducir Numeros a Enviar
                sendData(numbers);
                Toast.makeText(getBaseContext(), "Numbers Sent", Toast.LENGTH_SHORT).show();
            }
        });

        btnConnect = (Button)findViewById(R.id.btnConnect);
        //Visible = (Button)findViewById(R.id.button3);

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, uuid);
            } catch (Exception e) {
                Log.e(TAG, "Could not create insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(uuid);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try Connect...");

        // Set up device
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // MAC AND UUID for connection
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        btAdapter.cancelDiscovery();

        // Establish connection
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create data stream
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e ) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }

        // Create data stream
        Log.d(TAG, "...Create Socket...");

        try{
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }

    }

    private void checkBTState() {
        if(btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            }else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + uuid.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);

        }

    }
/*

    public void connect(View view){
        on(view);
        list(view);
    }

    public void on(View view){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Connected to device"
                    ,Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Already on",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void list(View view){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());

        Toast.makeText(getApplicationContext(),"Showing Paired Devices",
                Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter
                (this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }
*/
 /*   public void send(View view)throws IOException{
        String msg = "6461216921";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        msg = sharedPref.getString("Contacts","404 not found");

        //mmOutputStream.write(msg.getBytes());
        Toast.makeText(getApplicationContext(),msg,
        Toast.makeText(getApplicationContext(),"Here COMES THE MESSAGE" ,
                Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(),"Number Added",
                Toast.LENGTH_LONG).show();
    }*/

   /* public void off(View view){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned off" ,
                Toast.LENGTH_LONG).show();
    }
*/
    public void vcontacts (View view) {
        Toast.makeText(getApplicationContext(),"Desplegando Contactos" ,
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    public void visible(View view){
        Intent getVisible = new Intent(BluetoothAdapter.
                ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}