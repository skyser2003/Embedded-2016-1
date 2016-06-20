package com.example.shjan.embedded;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    ArrayList<TextView> roomDestTempList;

    // Bluetooth
    private BluetoothConnector bt = null;
    private Thread btThread = null;
    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Data
    private IdealTemperatureGenerator tempGenerator;
    private boolean systemOn = true;
    private boolean isInside = true;

    private int currentRoomID = 0;

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
        roomList.add((CheckBox) findViewById(R.id.room1));
        roomList.add((CheckBox) findViewById(R.id.room2));
        roomList.add((CheckBox) findViewById(R.id.room3));
        roomList.add((CheckBox) findViewById(R.id.room4));

        valueTemperature = (TextView) findViewById(R.id.value_temperature);
        valueHumidity = (TextView) findViewById(R.id.value_humidity);
        valueBrightness = (TextView) findViewById(R.id.value_brightness);

        valueManualRoom = (EditText) findViewById(R.id.value_manual_room);
        valueManualTemperature = (EditText) findViewById(R.id.value_manual_temperature);
        buttonManualSubmit = (Button) findViewById(R.id.button_manual_submit);

        spinnerGps = (Spinner) findViewById(R.id.spinner_gps);

        roomDestTempList = new ArrayList<>();
        roomDestTempList.add((TextView) findViewById(R.id.value_dest_temp_room1));
        roomDestTempList.add((TextView) findViewById(R.id.value_dest_temp_room2));
        roomDestTempList.add((TextView) findViewById(R.id.value_dest_temp_room3));
        roomDestTempList.add((TextView) findViewById(R.id.value_dest_temp_room4));

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

                                    if (450 <= sensor.illu) {
                                        turnOffAll();
                                        return;
                                    }

                                    boolean moveSensor1 = sensor.dis1 <= 250;
                                    boolean moveSensor2 = sensor.dis2 <= 250;
                                    boolean moveSensor3 = sensor.pw <= 250;

                                    Log.d("MOVE 1", "" + (moveSensor1 ? 1 : 0));
                                    Log.d("MOVE 2", "" + (moveSensor2 ? 1 : 0));
                                    Log.d("MOVE 3", "" + (moveSensor3 ? 1 : 0));

                                    switch (currentRoomID) {
                                        case 0: {
                                            if (moveSensor1) {
                                                currentRoomID = 1;
                                            }
                                        }
                                        break;

                                        case 1: {
                                            if (moveSensor1) {
                                                currentRoomID = 0;
                                            } else if (moveSensor2) {
                                                currentRoomID = 2;
                                            }
                                        }
                                        break;

                                        case 2: {
                                            if (moveSensor2) {
                                                currentRoomID = 1;
                                            } else if (moveSensor3) {
                                                currentRoomID = 3;
                                            }
                                        }
                                        break;

                                        case 3: {
                                            if (moveSensor3) {
                                                currentRoomID = 2;
                                            }
                                        }
                                        break;
                                    }

                                    valueTemperature.setText(String.valueOf(sensor.temp) + " C");
                                    valueHumidity.setText(String.valueOf(sensor.humi) + "%");
                                    valueBrightness.setText(String.valueOf(sensor.illu));

                                    for (CheckBox checkbox : roomList) {
                                        checkbox.setChecked(false);
                                    }

                                    roomList.get(currentRoomID).setChecked(true);

                                    for (int i = 0; i < roomDestTempList.size(); ++i) {
                                        TextView text = roomDestTempList.get(i);
                                        text.setText("Room " + i + " : " + tempGenerator.getIdealTemperature(i) + " C");
                                    }
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
        for (int i = 0; i < roomDestTempList.size(); ++i) {
            TextView text = roomDestTempList.get(i);
            text.setText("Room " + i + " : 0 C");
        }
    }
}