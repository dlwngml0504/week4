package com.example.juhee.project4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Looper;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by juhee on 2016. 7. 18..
 */
public class StoreAdapter extends BaseAdapter {
    private ArrayList<String> m_List;
    private Context m_Context;
    private JSONObject buyer;
    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";

    public StoreAdapter(Context context,String user) {
        m_List = new ArrayList<String>();
        m_Context = context;
        try {
            buyer = new JSONObject(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getCount() {
        return m_List.size();
    }
    @Override
    public Object getItem(int position) {
        return m_List.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public void add(String _msg) {
        m_List.add(_msg);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        if ( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.store_listview, parent, false);

            TextView mItemName = (TextView) convertView.findViewById(R.id.mItemName);
            TextView mItemCatalogue = (TextView) convertView.findViewById(R.id.mItemCatalogue);
            TextView mItemCost = (TextView) convertView.findViewById(R.id.mItemCost);
            ImageView mItemImage = (ImageView) convertView.findViewById(R.id.mItemImage);
            String iteminfo =  m_List.get(position);
            try {
                JSONObject jo = new JSONObject(iteminfo);
                mItemName.setText(jo.getString("itemName")+"("+jo.getString("itemcatalog")+")");
                mItemCatalogue.setText("친밀도 상승 +"+jo.getString("efficacy"));
                mItemCost.setText("$"+jo.getString("itemcost"));
                mItemImage.setImageResource(m_Context.getResources().getIdentifier(jo.getString("drawable"),"drawable",m_Context.getPackageName()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Button btn = (Button) convertView.findViewById(R.id.mBuy);
            final View finalConvertView = convertView;
            final View finalConvertView1 = convertView;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText input = new EditText(m_Context);
                    AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
                    builder.setTitle("수량을 선택하세요")
                            .setView(input)
                            .setCancelable(false)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    Log.e("Emit Buy","확인");
                                    String value = input.getText().toString();
                                    try {
                                        mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
                                        mSocket.off("buy");
                                        mSocket.off("buyRes");
                                    } catch (Exception e) {}
                                    mSocket.connect();
                                    JSONObject jo = new JSONObject();
                                    try {
                                        jo.put("quantity",value);
                                        jo.put("userid",buyer.getString("id"));
                                        jo.put("iteminfo", new JSONObject( m_List.get(position)));
                                        Log.e("Emit Buy",jo.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    mSocket.emit("buy",jo);
                                    mSocket.on("buyRes", new Emitter.Listener() {
                                        @Override
                                        public void call(final Object... args){
                                            JSONObject buyResJO = (JSONObject) args[0];
                                            try {
                                                Boolean bool = (Boolean) buyResJO.get("isSucceed");
                                                if (bool==true) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Looper.prepare();
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);

                                                            builder.setTitle("구매 성공하셨습니다.")
                                                                    .setNegativeButton("확인", new DialogInterface.OnClickListener(){
                                                                        public void onClick(DialogInterface dialog, int whichButton){
                                                                            dialog.cancel();
                                                                        }
                                                                    });

                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();
                                                            Looper.loop();
                                                        }
                                                    }).start();

                                                }
                                                else {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Looper.prepare();
                                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(m_Context);

                                                            builder2.setTitle("cash가 부족하여 구매 실패하셨습니다.")
                                                                    .setNegativeButton("확인", new DialogInterface.OnClickListener(){
                                                                        public void onClick(DialogInterface dialog, int whichButton){
                                                                            dialog.cancel();
                                                                        }
                                                                    });

                                                            AlertDialog dialog = builder2.create();
                                                            dialog.show();
                                                            Looper.loop();
                                                        }
                                                    }).start();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    });
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }



        return convertView;
    }
}
