package com.example.shjan.embedded;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by FrozenGuy on 6/20/2016.
 */
public class BluetoothConnector {
    static private BluetoothConnector inst;

    static public void init(AppCompatActivity activity, String address, UUID uuid) {
        if (inst == null) {
            inst = new BluetoothConnector();
            inst.initInternal(activity, address);
            new ConnectBT(activity, inst, uuid).execute();
        }
    }

    static public BluetoothConnector instance() {
        return inst;
    }

    private AppCompatActivity activity = null;
    private ProgressDialog progress = null;

    private boolean isBtConnected = false;
    private String address = null;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;

    private BluetoothConnector() {

    }

    private void initInternal(AppCompatActivity activity, String address) {
        this.activity = activity;
        this.address = address;
    }

    private void msg(String str) {
        Toast.makeText(activity.getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    public int read(byte[] buffer) {
        try {
            return btSocket.getInputStream().read(buffer,0,14);
        } catch (Exception e) {
            msg("read error");
        }

        return -1;
    }

    public void write(byte[] buffer) {
        try {
            btSocket.getOutputStream().write(buffer);
        } catch (Exception e) {
            msg("write error");
        }
    }

    public void close() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (Exception e) {
                msg("close error");
            }
        }
    }

    public boolean isConnected() {
        return isBtConnected == true && btSocket != null;
    }

    static private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected
        private AppCompatActivity activity;
        private Context context;
        private BluetoothConnector owner;
        private UUID uuid;

        public ConnectBT(AppCompatActivity activity, BluetoothConnector owner, UUID uuid) {
            this.activity = activity;
            this.context = activity.getApplicationContext();
            this.owner = owner;
            this.uuid = uuid;
        }

        @Override
        protected void onPreExecute() {
            owner.progress = ProgressDialog.show(activity, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (owner.btSocket == null || !owner.isBtConnected) {
                    owner.myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = owner.myBluetooth.getRemoteDevice(owner.address);//connects to the device's address and checks if it's available
                    owner.btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(uuid);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    owner.btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                owner.msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                activity.finish();
            } else {
                owner.msg("Connected.");
                owner.isBtConnected = true;
            }

            owner.progress.dismiss();
        }
    }
}
