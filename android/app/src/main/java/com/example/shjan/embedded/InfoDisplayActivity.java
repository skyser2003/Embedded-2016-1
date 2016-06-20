package com.example.shjan.embedded;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InfoDisplayActivity extends AppCompatActivity {
    // UI
    ArrayList<CheckBox> roomList;

    CheckBox statusDisplay;
    Button buttonOn, buttonOff;

    TextView valueTemperature;
    TextView valueHumidity;
    TextView valueBrightness;

    CheckBox checkManualTemperature;
    EditText valueManualTemperature;

    Spinner spinnerGps;

    // Etc
    private BluetoothConnector bt = null;
    private Thread btThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        statusDisplay = (CheckBox) findViewById(R.id.status_display);
        buttonOn = (Button) findViewById(R.id.button_on);
        buttonOff = (Button) findViewById(R.id.button_off);

        roomList = new ArrayList<>();

        valueTemperature = (TextView) findViewById(R.id.value_temperature);
        valueHumidity = (TextView) findViewById(R.id.value_humidity);
        valueBrightness = (TextView) findViewById(R.id.value_brightness);

        checkManualTemperature = (CheckBox) findViewById(R.id.checkbox_manual_temperature);
        valueManualTemperature = (EditText) findViewById(R.id.value_manual_temperature);

        spinnerGps = (Spinner) findViewById(R.id.spinner_gps);

        bt = BluetoothConnector.instance();

        // Init data
        statusDisplay.setEnabled(false);

        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        roomList.add((CheckBox) findViewById(R.id.room1));
        roomList.add((CheckBox) findViewById(R.id.room2));
        roomList.add((CheckBox) findViewById(R.id.room3));
        roomList.add((CheckBox) findViewById(R.id.room4));
        roomList.add((CheckBox) findViewById(R.id.room5));

        for (CheckBox checkbox : roomList) {
            checkbox.setEnabled(false);
        }

        checkManualTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : send bluetooth manual data
            }
        });

        checkManualTemperature.setChecked(false);

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("ON");
        spinnerArray.add("OFF");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGps.setAdapter(adapter);

        // Bluetooth thread
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

                        if (received != 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SensorData sensor = new SensorData();
                                    sensor.setDataFromByte(buffer);

                                    valueTemperature.setText(String.valueOf(sensor.temp));
                                    valueHumidity.setText(String.valueOf(sensor.humi));
                                    valueBrightness.setText(String.valueOf(sensor.illu));

                                    roomList.get(0).setChecked(250 <= sensor.dis1);
                                    roomList.get(1).setChecked(250 <= sensor.dis2);
                                    roomList.get(2).setChecked(250 <= sensor.pw);
                                    roomList.get(3).setChecked(250 <= sensor.ad);
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
}
