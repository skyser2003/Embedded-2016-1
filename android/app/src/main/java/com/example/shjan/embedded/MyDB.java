package com.example.shjan.embedded;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table manual_temp_table";
        db.execSQL(sql);
        onCreate(db);
    }
}
