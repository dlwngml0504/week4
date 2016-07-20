package com.example.juhee.project4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapPane extends FragmentActivity implements OnMapReadyCallback {


    public static Context mContext;
    Intent intent = null;
    // GPSTracker class
    private GpsInfo gps;
    double latitude, longitude;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=100;

    GoogleMap _map;

    public String id ;

    public ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        mContext = this;
        intent = this.getIntent();
        try {
            JSONObject obj = new JSONObject(intent.getStringExtra("userinfo"));
            id = obj.getString("id");
        } catch (JSONException e) {
            Log.e("@@","JSON EXCEPTION");
            e.printStackTrace();
        }

        markers = new ArrayList<Marker>();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        ImageButton myinfo = (ImageButton) findViewById(R.id.rank);
        myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Myinfo activity로 넘어감
                Intent intent5 = new Intent(getApplicationContext(),MyInfo.class);
                intent5.putExtra("id",id);
                startActivity(intent5);

            }
        });

        ImageButton store = (ImageButton) findViewById(R.id.store);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),Store.class);
                intent1.putExtra("userinfo",intent.getStringExtra("userinfo"));
                startActivity(intent1);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapPane.this);

    }

        @Override
        public void onMapReady (GoogleMap map){

            _map = map;
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            //gps = new GpsInfo(getApplicationContext());
            gps = new GpsInfo(MapPane.this);

            if (gps.isGetLocation()) {

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

        // 있던 마커 삭제
        for (int i = 0; i<markers.size(); i++){
            markers.get(i).remove();
        }
/*

        [{"_id":"578ddbf77a4028834bbed542",
        "catName":"치즈냥이",
        "catlocate":{"lon":127.362619,"lat":36.369349},
        "catstatus":"Zzzzz",
        "catAge":1,
        "catRank":[]},
        {"_id":"578ddcacdf9c29024c43b516",
        "userid":"1781499372094324",
        "userlocate":{"lat":36.3740728,"lon":127.3654031},
        "userInfo":{"name":null,"money":5000},
        "userRank":[],
        "userItem":[]}]

*/

        for (int i = 0; i<jsonArray.length(); i++ ){
            final JSONObject one;
            String catName,userId;
            LatLng position;
            try {
                one = jsonArray.getJSONObject(i);
                if (one.has("catName")){
                    catName = one.getString("catName");
                    Log.e("CATNAME",catName);
                    JSONObject pos =(JSONObject)one.get("catlocate");
                    position = new LatLng(Double.parseDouble(pos.get("lat").toString()), Double.parseDouble(pos.get("lon").toString()));

                    Marker marker = _map.addMarker(new MarkerOptions().title(catName).position(position).alpha(0.7f).icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier("caticon","drawable",getPackageName()))));



                    _map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            Log.e("YOU","CLICKED MARKER");
                            // 그 마커(고양이 이미지) 클릭 시 카메라 뷰로 인텐트 넘어감 //
                            Intent intent2 = new Intent(MapPane.this,CameraView.class);
                            //인텐트에서 뭐 넘겨줄지 !!
                            try {
                                intent2.putExtra("catname",one.getString("catName"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent2.putExtra("userinfo",intent.getStringExtra("userinfo"));

                            startActivity(intent2);
                            return true;
                        }
                    });
                    markers.add(marker);
                } else {
                    //case : Other users
                    userId = one.getString("userid");
                    JSONObject pos =(JSONObject)one.get("userlocate");
                    position = new LatLng(Double.parseDouble(pos.get("lat").toString()), Double.parseDouble(pos.get("lon").toString()));

                    Marker marker = _map.addMarker(new MarkerOptions().title(userId).position(position).alpha(0.7f).icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier("caticon","drawable",getPackageName() ))));
                    markers.add(marker);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
