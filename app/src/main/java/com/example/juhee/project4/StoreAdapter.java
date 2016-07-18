package com.example.juhee.project4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by juhee on 2016. 7. 18..
 */
public class StoreAdapter extends BaseAdapter {
    private ArrayList<String> m_List;
    private Context m_Context;
    private String buyer;

    public StoreAdapter(Context context,String user) {
        m_List = new ArrayList<String>();
        m_Context = context;
        buyer = user;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        if ( convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.store_listview, parent, false);

            TextView mItemName = (TextView) convertView.findViewById(R.id.mItemName);
            TextView mItemCatalogue = (TextView) convertView.findViewById(R.id.mItemCatalogue);
            TextView mItemCost = (TextView) convertView.findViewById(R.id.mItemCost);
            ImageView mItemImage = (ImageView) convertView.findViewById(R.id.mItemImage);
            String musicinfo =  m_List.get(position);
            try {
                JSONObject jo = new JSONObject(musicinfo);
                mItemName.setText(jo.getString("itemname"));
                mItemCatalogue.setText(jo.getString("itemcatalog"));
                mItemCost.setText("$"+jo.getString("itemcost"));
                Log.e("StoreAdapter",jo.getString("drawable"));
                mItemImage.setImageResource(m_Context.getResources().getIdentifier(jo.getString("drawable"),"drawable",m_Context.getPackageName()));
                //mItemImage.setImageDrawable(jo.getString("drawable")
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Button btn = (Button) convertView.findViewById(R.id.mBuy);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] items = {"1", "2", "3"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);
                    builder.setTitle("수량을 선택하세요")
                            .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener(){
                                // 목록 클릭시 설정
                                public void onClick(DialogInterface dialog, int index){
                                    Toast.makeText(m_Context, items[index], Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setCancelable(false)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            });;

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });



            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("StoreAdapter","hehe");
                    AlertDialog.Builder builder = new AlertDialog.Builder(m_Context);

                    builder.setTitle("Item 효과")
                            .setMessage("친밀도 상승 **")
                            .setCancelable(false)
                            .setNegativeButton("뒤로가기", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int whichButton){
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
