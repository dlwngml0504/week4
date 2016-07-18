package com.example.juhee.project4;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

public class MapPane extends FragmentActivity implements OnMapReadyCallback {


    public static Context mContext;

    // GPSTracker class
    private GpsInfo gps;
    double latitude, longitude;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=100;
    GoogleMap _map;

    private TimerTask second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        mContext = this;


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MapPane","********************");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        ImageButton rank = (ImageButton) findViewById(R.id.rank);
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ImageButton store = (ImageButton) findViewById(R.id.store);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),Store.class);
                startActivity(intent1);
            }
        });

        ImageButton myinfo = (ImageButton) findViewById(R.id.myinfo);
        myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapPane.this);

    }

        @Override
        public void onMapReady (GoogleMap map){

            _map = map;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("MapPane","onMapReady permission deny");
                return;
            }
            //gps = new GpsInfo(getApplicationContext());
            gps = new GpsInfo(MapPane.this);

            if (gps.isGetLocation()) {
                Log.e("Mappane","gps.isGetLocation() is true");
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                LatLng now = new LatLng(latitude,longitude);
                map.setMyLocationEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setRotateGesturesEnabled(false);
                map.moveCamera(CameraUpdateFactory.newLatLng(now));
                map.getUiSettings().setMyLocationButtonEnabled(true);
                map.animateCamera(CameraUpdateFactory.zoomTo(16));
                _map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            } else {
                // GPS 를 사용할수 없으므로
                gps.showSettingsAlert();
            }

        }

    public void pickMarkers(JSONArray jsonArray) {

        for (int i = 0; i<jsonArray.length(); i++ ){
            JSONObject one;
            String whichCat;
            LatLng position;
            try {
                one = jsonArray.getJSONObject(i);
                if (one.get("kind") == "CAT"){
                    whichCat = one.get("name").toString();
                    JSONObject pos =(JSONObject)one.get("position");
                    position = new LatLng(Double.parseDouble(pos.get("lat").toString()), Double.parseDouble(pos.get("lon").toString()));
                    //_map.addMarker(new MarkerOptions().position(position).icon("BITMAP ICON!!!!!"))
                    _map.addMarker(new MarkerOptions().position(position).alpha(0.7f).icon(BitmapDescriptorFactory.fromAsset(String.valueOf(R.mipmap.cat))));
                    //@@@@ 이미지  whichCat으로 각각 고양이 이미지 가져와야할듯!! @@@@
                    _map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            Log.e("MARKER", "MARKER CLICK EVENT");
                            // 그 마커(고양이 이미지) 클릭 시 카메라 뷰로 인텐트 넘어감 //
                            Intent intent = new Intent(MapPane.this,CameraView.class);
                            startActivity(intent);
                            return true;
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
