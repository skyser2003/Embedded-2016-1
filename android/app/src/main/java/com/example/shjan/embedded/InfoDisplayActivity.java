package com.example.shjan.embedded;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class InfoDisplayActivity extends AppCompatActivity {
    ArrayList<CheckBox> roomList;

    TextView valueTemperature;
    TextView valueHumidity;
    TextView valueBrightness;

    CheckBox checkManualTemperature;
    TextView valueManualTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        roomList = new ArrayList<>();
        roomList.add((CheckBox) findViewById(R.id.room1));
        roomList.add((CheckBox) findViewById(R.id.room2));
        roomList.add((CheckBox) findViewById(R.id.room3));
        roomList.add((CheckBox) findViewById(R.id.room4));
        roomList.add((CheckBox) findViewById(R.id.room5));

        valueTemperature = (TextView) findViewById(R.id.value_temperature);
        valueHumidity = (TextView) findViewById(R.id.value_humidity);
        valueBrightness = (TextView) findViewById(R.id.value_brightness);

        checkManualTemperature = (CheckBox) findViewById(R.id.checkbox_manual_temperature);
        valueManualTemperature = (TextView) findViewById(R.id.value_manual_temperature);

        checkManualTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valueManualTemperature.setEnabled(checkManualTemperature.isChecked());
            }
        });

        checkManualTemperature.setChecked(false);
        valueManualTemperature.setEnabled(false);
    }
}
