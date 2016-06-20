package com.example.shjan.embedded;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfoDisplayActivity extends AppCompatActivity {
    // UI
    ArrayList<CheckBox> roomList;

    Button buttonOn, buttonOff;

    TextView valueTemperature;
    TextView valueHumidity;
    TextView valueBrightness;

    EditText valueManualRoom;
    EditText valueManualTemperature;
    Button buttonManualSubmit;

    Spinner spinnerGps;

    // Bluetooth
    private BluetoothConnector bt = null;
    private Thread btThread = null;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Data
    private IdealTemperatureGenerator tempGenerator;
    private boolean systemOn = true;
    private boolean isInside = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        // Bluetooth init
        Intent i = getIntent();
        String address = i.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        BluetoothConnector.init(this, address, uuid);
        bt = BluetoothConnector.instance();

        // Data
        MyDB dbHelper = new MyDB(this, "past.db", null, 1);
        try {
            tempGenerator = new IdealTemperatureGenerator(dbHelper);
        } catch (Exception e) {
        }

        // UI variables
        buttonOn = (Button) findViewById(R.id.button_on);
        buttonOff = (Button) findViewById(R.id.button_off);

        roomList = new ArrayList<>();

        valueTemperature = (TextView) findViewById(R.id.value_temperature);
        valueHumidity = (TextView) findViewById(R.id.value_humidity);
        valueBrightness = (TextView) findViewById(R.id.value_brightness);

        valueManualRoom = (EditText) findViewById(R.id.value_manual_room);
        valueManualTemperature = (EditText) findViewById(R.id.value_manual_temperature);
        buttonManualSubmit = (Button) findViewById(R.id.button_manual_submit);

        spinnerGps = (Spinner) findViewById(R.id.spinner_gps);

        // Init data
        buttonOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                systemOn = true;
            }
        });

        buttonOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (systemOn == true) {
                    turnOffAll();
                }

                systemOn = false;
            }
        });

        roomList.add((CheckBox) findViewById(R.id.room1));
        roomList.add((CheckBox) findViewById(R.id.room2));
        roomList.add((CheckBox) findViewById(R.id.room3));
        roomList.add((CheckBox) findViewById(R.id.room4));

        for (CheckBox checkbox : roomList) {
            checkbox.setEnabled(false);
        }

        buttonManualSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomTxt = valueManualRoom.getText().toString();
                int roomID = Integer.parseInt(roomTxt);

                String tempTxt = valueManualTemperature.getText().toString();
                float manualTemp = Integer.parseInt(tempTxt);

                tempGenerator.setManualTemperature(manualTemp, roomID);
            }
        });

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("ON");
        spinnerArray.add("OFF");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGps.setAdapter(adapter);

        spinnerGps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    isInside = true;
                } else {
                    isInside = false;

                    turnOffAll();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Bluetooth thread
        btThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (bt.isConnected() == false || isInside == false || systemOn == false) {
                            sleep(2000);
                            continue;
                        }

                        final byte[] buffer = new byte[255];
                        final int received = bt.read(buffer);

                        if (received != 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isInside == false || systemOn == false) {
                                        return;
                                    }

                                    SensorData sensor = new SensorData();
                                    sensor.setDataFromByte(buffer);

                                    if (300 <= sensor.illu) {
                                        turnOffAll();
                                        return;
                                    }

                                    valueTemperature.setText(String.valueOf(sensor.temp) + " C");
                                    valueHumidity.setText(String.valueOf(sensor.humi) + "%");
                                    valueBrightness.setText(String.valueOf(sensor.illu));

                                    roomList.get(0).setChecked(sensor.dis1 <= 250);
                                    roomList.get(1).setChecked(sensor.dis2 <= 250);
                                    roomList.get(2).setChecked(sensor.pw <= 250);
                                    roomList.get(3).setChecked(sensor.ad <= 250);
                                }
                            });
                        }

                        sleep(2000);
                    }
                } catch (Exception e) {

                }
            }
        };

        btThread.start();
    }

    private void turnOffAll() {
        valueTemperature.setText("0 C");
        valueHumidity.setText("0%");
        valueBrightness.setText("0");

        for (CheckBox checkbox : roomList) {
            checkbox.setChecked(false);
        }
    }
}