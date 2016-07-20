package com.example.juhee.project4;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CatDetail extends AppCompatActivity {
    TextView mCatNameView;
    ImageView mCatPhotoView;

    TextView mCatAgeView;
    TextView mCatStatusView;
    TextView mCatLatView;
    TextView mCatLonView;

    ListView mCatRankListView;

    JSONObject jsonRes;
    JSONObject catInfo;
    JSONArray catRank;

    CatDetailAdapter catDetailAdapter;

    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";
    public String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("START","SSSS");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);
        Intent intent = getIntent();
        //String  photo = intent.getStringExtra("photo");
        String  name = intent.getStringExtra("name");
        id = intent.getStringExtra("id");



        mCatNameView = (TextView)findViewById(R.id.mCatName);
        mCatPhotoView = (ImageView)findViewById(R.id.mCatPhoto);
        mCatAgeView = (TextView)findViewById(R.id.mCatAge);
        mCatStatusView = (TextView)findViewById(R.id.mCatStatus);
        mCatLatView = (TextView)findViewById(R.id.mCatLat);
        mCatLonView = (TextView)findViewById(R.id.mCatLon);

        mCatRankListView = (ListView) findViewById(R.id.mCatRankingList);





        mCatNameView.setText(name);
        //@@@@@photo

        final Handler handler0 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String catName,age,status,st,lat,lon;
                try {
                    Log.e("H!","!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    age = catInfo.getString("catAge");
                    status = catInfo.getString("catstatus");
                    JSONObject loc = catInfo.getJSONObject("catlocate");
                    lat = loc.getString("lat");
                    lon = loc.getString("lon");
                    catName = catInfo.getString("catName");
                    String Status ="";

                    // 고양이 이름으로 마커 이미지 변경
                    if(catName.equals("아름이")) Status = "0";
                    else if(catName.equals("진리")) Status="1";
                    else if(catName.equals("소망냥")) Status="2";
                    else if(catName.equals("치즈냥이")) Status="3";
                    else if(catName.equals("서측")) Status="4";
                    else if(catName.equals("교수회관냥")) Status="5";
                    else if(catName.equals("패컬티")) Status="6";


                    st = (new JSONObject(status)).getString("satiety")+"/"+(new JSONObject(status).getString("lastMealTime"));
                    mCatPhotoView.setImageResource(getResources().getIdentifier("marker"+Status,"drawable",getPackageName()));


                    mCatAgeView.setText(age);
                    mCatStatusView.setText(st);
                    mCatLatView.setText(lat);
                    mCatLonView.setText(lon);
                    Log.e("H2","@@@@@@@@@@@@@@@2");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        catDetailAdapter = new CatDetailAdapter(this);
        final Handler handler1 = new Handler(){
            @Override
            public void handleMessage(Message msg){

                Log.e("@@","@@@@@@@@@@@@@");
                try {
                    for (int i = 0; i<catRank.length(); i++){
                        JSONObject one = catRank.getJSONObject(i);
                        String rank, user;
                        Boolean me;
                        rank = one.getString("rank");
                        user = one.getString("username");
                        me = false;
                        if (id == user){
                            me = true;
                        }
                        catDetailAdapter.addItem(user,rank, me);

                    }
                    mCatRankListView.setAdapter(catDetailAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };



        try {
            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
            mSocket.off("catInfo");
            mSocket.off("catInfoRes");
            Log.e("REQU","SENDDDDDDDDDDDDDdd");
        } catch (Exception e) {}
        mSocket.connect();

        try {
            JSONObject sendname = new JSONObject().put("catName",name);
            mSocket.emit("catInfo",sendname);
            Log.e("REQU","SENDDDDDDDDDDDDDdd");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        mSocket.on("catInfoRes", new Emitter.Listener(){
            @Override
            public void call(final Object... args){
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        jsonRes = (JSONObject) args[0];
                        catInfo = jsonRes;
                        Log.e("RESPONSE",jsonRes.toString());
                        try {
                            catRank = new JSONArray(jsonRes.getString("catRank"));

                            Log.e("CAT RANK", catRank.toString());
                            /// @@@catinfo 넣기@@@
                            Message msg0 = handler0.obtainMessage();
                            handler0.sendMessage(msg0);

                            // 유저 랭킹 어댑터로 리스트뷰에 넣기
                            Message msg1 = handler1.obtainMessage();
                            handler1.sendMessage(msg1);

                            Log.e("**","**********");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();


            }
        });

    }
}