package com.example.shjan.embedded;

import android.util.Log;

/**
 * Created by Administrator on 2016-06-20.
 */
public class SensorData {
    public int illu;
    public int dis1;
    public int dis2;
    public int pw;
    public int ad;
    public float temp;
    public float humi;

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public int setDataFromByte(byte[] buffer)
    {
        illu = unsignedToBytes(buffer[0]) * (int)(Math.pow(2,8)) + unsignedToBytes(buffer[1]);
        Log.d("errer",buffer[0] + "");
        Log.d("errer",buffer[1] + "");
        dis1 = unsignedToBytes(buffer[2]) * (int)(Math.pow(2,8)) + unsignedToBytes(buffer[3]);
        dis2 = unsignedToBytes(buffer[4]) * (int)(Math.pow(2,8)) + unsignedToBytes(buffer[5]);
        pw = unsignedToBytes(buffer[6]) * (int)(Math.pow(2,8)) + unsignedToBytes(buffer[7]);
        ad = unsignedToBytes(buffer[8]) * (int)(Math.pow(2,8)) + unsignedToBytes(buffer[9]);
        temp = (float)unsignedToBytes(buffer[10]) + ((float)unsignedToBytes(buffer[11]))/100;
        humi = (float)unsignedToBytes(buffer[12]) + ((float)unsignedToBytes(buffer[13]))/100;
        return -1;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "illu=" + illu +
                ", dis1=" + dis1 +
                ", dis2=" + dis2 +
                ", pw=" + pw +
                ", ad=" + ad +
                ", temp=" + temp +
                ", humi=" + humi +
                '}';
    }
}
