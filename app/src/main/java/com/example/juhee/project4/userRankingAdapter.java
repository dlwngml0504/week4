package com.example.juhee.project4;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by q on 2016-07-18.
 */
public class userRankingAdapter extends BaseAdapter {

    private Context mContext = null;
    public static ArrayList<OneRank> mListRank= null ;

    public userRankingAdapter(Context context){
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

        OneRankHolder holder;
        if (view == null){
            holder = new OneRankHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.one_ranking,null);

            holder.mCatPhoto = (ImageView)view.findViewById(R.id.mPhoto);
            holder.mCatName = (TextView)view.findViewById(R.id.mCatName);
            holder.mCatMyRank = (TextView)view.findViewById(R.id.mMyRanking);
            view.setTag(holder);
        }else {
            holder = (OneRankHolder)view.getTag();
        }

        OneRank mData = mListRank.get(i);

        //사진 어떻게 할지 미정
        // cat name을 파일명으로 하여 드로어블에서 뽑아온다.

        holder.mCatPhoto.setImageResource(mContext.getResources().getIdentifier(mData.mCatName,"drawable",mContext.getPackageName()));
        holder.mCatName.setText(mData.mCatName);
        holder.mCatMyRank.setText(mData.mCatMyRank);

        return view;
    }

    public static void addItem(String photo, String name, String rank){
        OneRank addInfo;
        addInfo = new OneRank();
        addInfo.mCatPhoto = photo;
        addInfo.mCatName = name;
        addInfo.mCatMyRank = rank;
        mListRank.add(addInfo);
    }




}
