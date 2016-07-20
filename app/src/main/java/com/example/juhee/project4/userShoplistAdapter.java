package com.example.juhee.project4;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by juhee on 2016. 7. 19..
 */
public class userShoplistAdapter extends BaseAdapter{
    private ArrayList<String> m_List;
    private Context m_Context;
    private String id;
    public userShoplistAdapter(Context context,String user) {
        m_List = new ArrayList<String>();
        m_Context = context;
        id = user;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if ( convertView == null ) {
            Log.e("userShoppingAdapter","convertView == null");
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.one_shopping, parent, false);

            TextView mItemName = (TextView) convertView.findViewById(R.id.mBasketName);
            TextView mItemEfficacy = (TextView) convertView.findViewById(R.id.mBasketEfficacy);
            ImageView mItemImage = (ImageView) convertView.findViewById(R.id.mBasketImage);
            String iteminfo =  m_List.get(position);
            Log.e("userShoppingAdapter",iteminfo.toString());
            try {
                JSONObject jo = new JSONObject(iteminfo);
                mItemName.setText(jo.getString("itemName")+"("+jo.getString("itemcatalog")+")");
                mItemEfficacy.setText("친밀도 상승 +"+jo.getString("efficacy"));
                mItemImage.setImageResource(m_Context.getResources().getIdentifier(jo.getString("drawable"),"drawable",m_Context.getPackageName()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }
}
