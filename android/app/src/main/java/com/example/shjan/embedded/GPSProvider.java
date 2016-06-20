package com.example.shjan.embedded;

/**
 * Created by Administrator on 2016-06-21.
 */
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class GPSProvider {
    LocationManager mlocManager;
    LocationListener mlocListener;
    String provider;
    Location location;
    String bestProvider;
    Criteria criteria;

    public GPSProvider(LocationManager mlocManager){ // 생성자
        this.mlocManager = mlocManager; // GPS값을 받아오기 위해서는 LocationManager 클래스의 오브젝트가 반드시 필요하다.
        mlocListener = new LocationListener(){ // 위치와 관련된 디바이스의 다양한 Event에 따른 리스너를 정의해주어야 한다.
            public void onLocationChanged(Location loc) {  // 사용자의 위치가 변할때마다 그를 감지해내는 메소드
                loc.getLatitude();
                loc.getLongitude();
            }

            public void onProviderDisabled(String provider){}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras){}
        };
        criteria = new Criteria(); // Criteria는 위치 정보를 가져올 때 고려되는 옵션 정도로 생각하면 된다.
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        bestProvider = mlocManager.getBestProvider(criteria,true);
    }
    public double getLatitude(){
        try {
            bestProvider = mlocManager.getBestProvider(criteria, true); // 적합한 위치 정보 제공자를 찾아내고
            if(bestProvider == null)
                return 0;
            mlocManager.requestLocationUpdates(bestProvider, 0, 0, mlocListener); // 정보 제공자를 통해 외치 업데이트를 한 다음
            location = mlocManager.getLastKnownLocation(bestProvider);  // 최종 위치 정보를 파악해내고

            return location.getLatitude(); //Latitude 값을 리턴
        }
        catch (SecurityException e)
        {
            return 0;
        }
    }
    public double getLongitude(){ //Latitude와 원리는 같다.
        try {
            bestProvider = mlocManager.getBestProvider(criteria, true);
            if(bestProvider == null)
                return 0;
            mlocManager.requestLocationUpdates(bestProvider, 0, 0, mlocListener);
            location = mlocManager.getLastKnownLocation(bestProvider);

            return location.getLongitude();
        }
        catch (SecurityException e)
        {
            return 0;
        }
    }
}