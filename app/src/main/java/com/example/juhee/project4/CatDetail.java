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

public class CatDetail extends AppCompatActivity {
    TextView mCatNameView;
    ImageView mCatPhotoView;

    TextView mCatAgeView;
    TextView mCatStatusView;
    TextView mCatLatView;
    TextView mCatLonView;

    ListView mCatRankListView;

    JSONObject catInfo;
    JSONArray catRank;
    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("START","SSSS");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);
        Intent intent = getIntent();
        //String  photo = intent.getStringExtra("photo");
        String  name = intent.getStringExtra("name");



        mCatNameView = (TextView)findViewById(R.id.mCatName);
        mCatPhotoView = (ImageView)findViewById(R.id.mCatPhoto);
        mCatAgeView = (TextView)findViewById(R.id.mCatAge);
        mCatStatusView = (TextView)findViewById(R.id.mCatStatus);
        mCatLatView = (TextView)findViewById(R.id.mCatLat);
        mCatLonView = (TextView)findViewById(R.id.mCatLon);


        final CatDetailAdapter catDetailAdapter = new CatDetailAdapter(this);


        mCatNameView.setText(name);
        //@@@@@photo

        final Handler handler0 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String age,status,lat,lon;
                try {
                    age = catInfo.getString("catAge");
                    status = catInfo.getString("catstatus");
                    JSONObject loc = catInfo.getJSONObject("catlocate");
                    lat = loc.getString("lat");
                    lon = loc.getString("lon");

                    mCatAgeView.setText(age);
                    mCatStatusView.setText(status);
                    mCatLatView.setText(lat);
                    mCatLonView.setText(lon);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        final Handler handler1 = new Handler(){
            @Override
            public void handleMessage(Message msg){


                try {
                    for (int i = 0; i<catRank.length(); i++){
                        JSONObject one = catRank.getJSONObject(i);
                        String rank, user;
                        Boolean me;
                        rank = one.getString("rank");
                        user = one.getString("username");
                        me = false;
                        /*if (id == user){
                            me = true;
                        }*/
                        catDetailAdapter.addItem(user,rank, me);

                    }
                    mCatRankListView.setAdapter(catDetailAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

       /* mCatNameView.setText(name);
        mCatPhotoView.setImageResource(getApplicationContext().getResources().getIdentifier(photo,"drawable",getApplicationContext().getPackageName()));
*/

        try {
            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
            mSocket.off("catInfo");
            mSocket.off("catInfoRes");
        } catch (Exception e) {}
        mSocket.connect();

        try {
            JSONObject sendname = new JSONObject().put("catName",name);
            mSocket.emit("catInfo",sendname);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        mSocket.on("catInfoRes", new Emitter.Listener(){
            @Override
            public void call(final Object... args){
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        JSONObject jsonRes = (JSONObject) args[0];
                        try {
                            catInfo = (JSONObject)jsonRes.get("catInfo");
                            catRank = (JSONArray)jsonRes.get("catRank");
                            Log.e("CAT INFO", catInfo.toString());
                            Log.e("CAT RANK", catRank.toString());
                            /// @@@catinfo 넣기@@@
                            Message msg0 = handler0.obtainMessage();
                            handler0.sendMessage(msg0);

                            // 유저 랭킹 어댑터로 리스트뷰에 넣기
                            Message msg1 = handler1.obtainMessage();
                            handler1.sendMessage(msg1);
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