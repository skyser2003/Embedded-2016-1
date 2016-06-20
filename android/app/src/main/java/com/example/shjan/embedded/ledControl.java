package com.example.shjan.embedded;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.RunnableFuture;


public class ledControl extends AppCompatActivity {

    Button btnOn, btnOff, btnDis, btnInfo;
    SeekBar brightness;
    TextView lumn;
    TextView btText;
    String address = null;

    BluetoothConnector bt = null;
    Thread btThread;

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_led_control);

        //call the widgtes
        btnOn = (Button) findViewById(R.id.button2);
        btnOff = (Button) findViewById(R.id.button3);
        btnDis = (Button) findViewById(R.id.button4);
        btnInfo = (Button) findViewById(R.id.button_info_display);
        brightness = (SeekBar) findViewById(R.id.seekBar);
        lumn = (TextView) findViewById(R.id.lumn);
        btText = (TextView) findViewById(R.id.text_bluetooth_receive);

        BluetoothConnector.init(this, address, myUUID);
        bt = BluetoothConnector.instance();

        //commands to be sent to bluetooth
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnLed();      //method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffLed();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ledControl.this, InfoDisplayActivity.class);
                startActivity(i);
            }
        });

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    lumn.setText(String.valueOf(progress));
                    bt.write(String.valueOf(progress).getBytes());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (bt.isConnected() == false) {
                            sleep(5000);
                            continue;
                        }

                        final byte[] buffer = new byte[255];
                        final int received = bt.read(buffer);
                        final SensorData sensor = new SensorData();
                        sensor.setDataFromByte(buffer);
                        Log.d("LOG", "received : " + received);

                        if (received != 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String str = new String(buffer, 0, received);
                                    Log.d("UI", str);
                                    btText.setText(sensor.toString());
                                }
                            });
                        }

                        sleep(5000);
                    }
                } catch (Exception e) {

                }
            }
        };

        btThread.start();
    }

    private void Disconnect() {
        if (bt != null) {
            bt.close();
        }

        finish(); //return to the first layout

    }

    private void turnOffLed() {
        if (bt != null) {
            bt.write("TF".toString().getBytes());
        }
    }

    private void turnOnLed() {
        if (bt != null) {
            bt.write("TO".toString().getBytes());
        }
    }

    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
