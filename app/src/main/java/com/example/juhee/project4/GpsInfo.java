package com.example.juhee.project4;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GpsInfo extends Service implements android.location.LocationListener {


    private final Context mContext;

    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;

    Location location;
    double lat; // 위도
    double lon; // 경도

    // 최소 GPS 정보 업데이트 거리 2미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;

    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 2초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;

    protected LocationManager locationManager;

    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":12345";


    public GpsInfo(Context context) {
        this.mContext = context;
        location = getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e("GpsInfo", String.valueOf(isGPSEnabled));
            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
            } else {

                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기
                if (isNetworkEnabled) {

                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return new Location("-1");
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();

                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * GPS 종료
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return ;
            }
            locationManager.removeUpdates(GpsInfo.this);
        }
    }

    /**
     * 위도값을 가져옵니다.
     */
    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    /**
     * 경도값을 가져옵니다.
     */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    /**
     * GPS 나 wife 정보가 켜져있는지 확인합니다.
     */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("GPS 사용유무셋팅");
        alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. \n 설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        JSONObject jsonObject = new JSONObject();


        // get changed location
        location = getLocation();

        // put in jason object
        try {
            jsonObject.put("lat",String.valueOf(location.getLatitude()));
            jsonObject.put("lon",String.valueOf(location.getLongitude()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Toast.makeText(mContext,"Location changed:"+location.toString(),Toast.LENGTH_SHORT).show();

        ((MapPane)MapPane.mContext)._map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(),location.getLongitude())).tilt(30).zoom(16).bearing(location.getBearing()).build();
        Toast.makeText(this.mContext,  String.valueOf(location.getBearing()),Toast.LENGTH_SHORT);
        ((MapPane)MapPane.mContext)._map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        /*@@@@@@@@@@@@2
        mSocket.off("heartbeat"); mSocket.off("heartbeatRes");

        // Send GPS information to Server using Socket
        try {
                        mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
        } catch (Exception e) {}
        mSocket.connect();
        mSocket.emit("heartbeat",jsonObject);

        // Get response
        mSocket.on("heartbeatRes", new Emitter.Listener() {
            @Override
            public void call(final Object... args){
                Log.e("HHH","HearBeat Response");
                JSONArray jsonRes = (JSONArray) args[0];
                *//*
                @@@@@@
                받은걸로 지도에 띄우기,
                띄운거에 클릭 이벤트로 카메라로 넘겨주기...?
                @@@@@@@@
                 *//*
                ((MapPane)MapPane.mContext).pickMarkers(jsonRes);

            }
        });
        @@@@@@@@@@@@@@@
        */



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   /* public void pickMarkers(JSONArray jsonArray){
        for (int i = 0; i<jsonArray.length(); i++ ){
            JSONObject one;
            String kind;
            try {
               one = jsonArray.getJSONObject(i);
                if (one.get("kind") == "CAT"){
                    mContext.getApplicationContext();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }*/
}