package com.example.shjan.embedded;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2016-06-20.
 */
public class MyDB extends SQLiteOpenHelper{
    public MyDB(Context context, String name,
                SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table manual_temp_table(id integer primary key autoincrement, room integer, temperature float)";
        String sql2 = "create table moving_pattern(id integer primary key autoincrement, past_room integer, start_room integer, target_room integer)";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table manual_temp_table";
        String sql2 = "drop table moving_pattern";
        db.execSQL(sql);
        Log.d("Sql","create moving");
        db.execSQL(sql2);
        onCreate(db);
    }
}
