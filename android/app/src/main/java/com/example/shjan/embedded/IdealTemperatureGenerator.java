package com.example.shjan.embedded;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.spec.ECField;
import java.util.Random;

/**
 * Created by Administrator on 2016-06-20.
 */
public class IdealTemperatureGenerator {

    private float[] IdealTemperature;
    private SQLiteDatabase db;
    String tag = "SQLite";

    Random rand;
    public IdealTemperatureGenerator(MyDB dbHelper) throws Exception
    {
        rand = new Random();
        db = dbHelper.getWritableDatabase();
        IdealTemperature = new float[4];
        for(int i = 0; i < 4; i++)
        {
            IdealTemperature[i] = 25.0f;
            Cursor c = db.rawQuery("select * from manual_temp_table where room = " + i,null);
            while(c.moveToNext())
            {
                float temperature = c.getFloat(2);
                IdealTemperature[i] = feedback(temperature,IdealTemperature[i]);
            }
        }
    }

    public float getIdealTemperature(int room)
    {
        return IdealTemperature[room];
    }

    public void setManualTemperature(float temperature, int room)
    {
        ContentValues values = new ContentValues();
        values.put("room",room);
        values.put("temperature", temperature);
        db.insert("manual_temp_table",null,values);

        IdealTemperature[room] = feedback(temperature,IdealTemperature[room]);
    }

    private float feedback(float temperature,float ideal)
    {
        float weight = (float)rand.nextInt(100) / 50;

        float dis = temperature - ideal;

        Log.d(tag,"Weight : " + weight + " dis : " + dis + "Temperature : " + temperature + "ideal : " + ideal);

        return ideal + dis * weight;
    }
}
