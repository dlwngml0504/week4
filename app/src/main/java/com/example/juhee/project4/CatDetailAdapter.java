package com.example.juhee.project4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q on 2016-07-19.
 */
public class CatDetailAdapter extends BaseAdapter{

    private Context mContext = null;
    public static ArrayList<CatDetailData> mListRank= null ;

    public CatDetailAdapter(Context context){
        super();
        this.mContext = context;
        this.mListRank = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mListRank.size();
    }

    @Override
    public Object getItem(int i) {
        return mListRank.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        CatDetailRankHolder holder;
        if (view == null){
            holder = new CatDetailRankHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cat_ranking,null);
            holder.mUserName = (TextView)view.findViewById(R.id.rank_username);
            holder.mUserRank = (TextView)view.findViewById(R.id.rankingicon);

            view.setTag(holder);
        }else {
            holder = (CatDetailRankHolder) view.getTag();
        }

        CatDetailData mData = mListRank.get(i);

        //사진 어떻게 할지 미정
        // cat name을 파일명으로 하여 드로어블에서 뽑아온다.

        holder.mUserName.setText(mData.mUserName);
        holder.mUserRank.setText(mData.mUserRank);
        if (mData.mMe){
            holder.mUserName.setTextColor(mContext.getResources().getIdentifier("colorPrimaryDark","color",mContext.getPackageName()));
        }


        return view;
    }

    public static void addItem(String name, String rank, Boolean me){
        CatDetailData addInfo;
        addInfo = new CatDetailData();
        addInfo.mUserName = name;
        addInfo.mUserRank = rank;
        addInfo.mMe = me;
        mListRank.add(addInfo);
    }

}
