package com.example.shjan.embedded;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Random;

/**
 * Created by Administrator on 2016-06-21.
 */
public class MovingPredict {
    private SQLiteDatabase db;
    private int predicted = -1;
    String tag = "SQLite";
    Random rand = new Random();

    public MovingPredict(MyDB dbHelper)
    {
        db = dbHelper.getWritableDatabase();
    }

    public void moving(int pastRoom, int startRoom, int targetRoom)
    {
        int weight = 1;
        if(predicted != targetRoom)
            weight = rand.nextInt(5) + 1;
        for(int i = 0; i < weight; i++) {
            ContentValues values = new ContentValues();
            values.put("start_room", startRoom);
            values.put("target_room", targetRoom);
            values.put("past_room", pastRoom);
            db.insert("moving_pattern", null, values);
        }
    }

    public int predict(int pastRoom, int nowRoom)
    {
        int count[] = {0,0,0,0};

        Cursor c = db.rawQuery("select target_room ,count(*) from moving_pattern where start_room = " + nowRoom + " and past_room = " + pastRoom + " group by target_room ",null);
        while(c.moveToNext())
        {
            count[c.getInt(0)] = c.getInt(1);
        }
        int predictTemp1,predictTemp2, predict;
        int predictTemp1Count,predictTemp2Count;
        if(count[0] > count[1]) {
            predictTemp1Count = count[0];
            predictTemp1 = 0;
        }
        else {
            predictTemp1Count = count[1];
            predictTemp1 = 1;
        }
        if(count[2] > count[3])
        {
            predictTemp2Count = count[2];
            predictTemp2 = 2;
        }
        else {
            predictTemp2Count = count[3];
            predictTemp2 = 3;
        }
        if(predictTemp1Count > predictTemp2Count)
        {
            predict = predictTemp1;
        }
        else
            predict = predictTemp2;

        Log.d("Moving", "pridect : " + predict);
        predicted = predict;
        return predict;
    }
}
