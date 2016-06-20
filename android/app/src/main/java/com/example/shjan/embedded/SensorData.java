package com.example.shjan.embedded;

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

    public int setDataFromByte(byte[] buffer)
    {
        illu = buffer[0] * (int)(Math.pow(2,24)) + buffer[1] * (int)(Math.pow(2,16)) + buffer[2] * (int)(Math.pow(2,8)) + buffer[3];
        dis1 = buffer[4] * (int)(Math.pow(2,24)) + buffer[5] * (int)(Math.pow(2,16)) + buffer[6] * (int)(Math.pow(2,8)) + buffer[7];
        dis2 = buffer[8] * (int)(Math.pow(2,24)) + buffer[9] * (int)(Math.pow(2,16)) + buffer[10] * (int)(Math.pow(2,8)) + buffer[11];
        pw = buffer[12] * (int)(Math.pow(2,24)) + buffer[13] * (int)(Math.pow(2,16)) + buffer[14] * (int)(Math.pow(2,8)) + buffer[15];
        ad = buffer[16] * (int)(Math.pow(2,24)) + buffer[17] * (int)(Math.pow(2,16)) + buffer[18] * (int)(Math.pow(2,8)) + buffer[19];
        temp = (float)buffer[20] + ((float)buffer[21])/100;
        humi = (float)buffer[21] + ((float)buffer[22])/100;
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
