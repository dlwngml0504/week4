package com.example.juhee.project4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    ListView mCatInfoListView;
    ListView mCatRankListView;

    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_detail);
        Intent intent = getIntent();
        String  photo = intent.getStringExtra("photo");
        String  name = intent.getStringExtra("name");
        final String id = intent.getStringExtra("id");


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

        final CatDetailAdapter catDetailAdapter = new CatDetailAdapter(this);

        mSocket.on("catInfoRes", new Emitter.Listener(){
            @Override
            public void call(final Object... args){
                JSONObject jsonRes = (JSONObject)args[0];

                try {
                    JSONObject catInfo = (JSONObject)jsonRes.get("catInfo");
                    JSONArray catRank = (JSONArray)jsonRes.get("catRank");

                    /// @@@catinfo 넣기@@@

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
        });

    }
}
