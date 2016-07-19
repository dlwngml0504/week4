package com.example.juhee.project4;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.Collections;

public class Store extends AppCompatActivity {
    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";
    private StoreAdapter m_Adapter;
    private ListView m_ListView;
    JSONArray jsonRes = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_activity);

        Button btn = (Button)findViewById(R.id.select_btn);
        final CheckBox chFood = (CheckBox)findViewById(R.id.checkfood);
        final CheckBox chSnack = (CheckBox)findViewById(R.id.checksnack);
        final CheckBox chToy = (CheckBox)findViewById(R.id.checktoy);
        final CheckBox chEtc = (CheckBox)findViewById(R.id.checketc);
        final Intent intent = getIntent();


        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.e("handler",jsonRes.toString());
                try {
                    //jsonRes = new JSONArray(msg);
                    m_Adapter = new StoreAdapter(Store.this,intent.getStringExtra("userinfo"));
                    m_ListView = (ListView)findViewById(R.id.storeList);
                    m_ListView.setAdapter(m_Adapter);
                    for (int i =0; i<jsonRes.length();i++) {
                        m_Adapter.add(jsonRes.getJSONObject(i).toString());
                        Log.e("Store", jsonRes.getJSONObject(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        if (btn!=null) {
            btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    JSONObject jo = new JSONObject();
                    try {
                        if (chFood.isChecked()) {
                            jo.put("food",true);
                        }
                        if (chSnack.isChecked()) {
                            jo.put("snack",true);
                        }
                        if (chToy.isChecked()) {
                            jo.put("toy",true);
                        }
                        if (chEtc.isChecked()) {
                            jo.put("etc",true);
                        }

                        try {
                            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
                            mSocket.off("store");
                            mSocket.off("storeRes");
                        } catch (Exception e) {}
                        mSocket.connect();

                        mSocket.emit("store",jo);

                        // Get response
                        mSocket.on("storeRes", new Emitter.Listener() {
                            @Override
                            public void call(final Object... args){
                                Log.e("Store","Store Response"+args[0].toString());
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        jsonRes = (JSONArray) args[0];
                                        Log.e("Store",jsonRes.toString());
                                        Message msg = handler.obtainMessage();
                                        handler.sendMessage(msg);
                                    }
                                };
                                thread.start();
                            }
                        });

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
