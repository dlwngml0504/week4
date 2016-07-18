package com.example.juhee.project4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.juhee.project4.R.color.selectedBtn;
import static com.example.juhee.project4.R.color.unselectedBtn;

public class MyInfo extends AppCompatActivity {

    TextView mMyName;
    TextView mMyMoney;
    ListView mMyRanking;
    ListView mMyItems;

    userRankingAdapter userRankingAdapter;

    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";

    private  String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");

        mMyName = (TextView)findViewById(R.id.userName);
        mMyMoney = (TextView)findViewById(R.id.userMoney);
        mMyRanking = (ListView)findViewById(R.id.userRanking);
        mMyItems = (ListView)findViewById(R.id.userItem);

        final Button myRanking = (Button)findViewById(R.id.rankBtn);
        final Button myItems = (Button)findViewById(R.id.itemBtn);

        //@@@최초는 랭킹 버튼이 선택된 상태여야함
        myRanking.setBackgroundColor(getResources().getColor(selectedBtn));
        myItems.setBackgroundColor(getResources().getColor(unselectedBtn));

        // 먼저 소켓으로 정보 다 받아옴
        try {
            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
            mSocket.off("userInfo");
            mSocket.off("userInfo");
        } catch (Exception e) {}
        mSocket.connect();

        // 아이디 보냄
        try {
            JSONObject sendid = new JSONObject().put("ID",id);
            mSocket.emit("userInfo",sendid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get response
        mSocket.on("userInfoRes", new Emitter.Listener() {
            /*
                           @@@@@@
                           {“userInfo” :  {“name” : 유저이름, “money”:유저가 가진 돈 },
                           “userRank” : 유저가 관계 맺고 있는 고양이 이름과 유저의 순위를 어레이로 ,
                           “userItem”: 유저가 가지고 있는 아이템 목록을 어레이 리스트로 }
                           @@@@@@@@
                            */
            @Override
            public void call(final Object... args){
                Log.e("HHH","userInfo Response");
                JSONObject jsonRes = (JSONObject) args[0];
                try {
                    // response를 받아옴
                    JSONObject userInfo = (JSONObject)jsonRes.get("userInfo");
                    JSONArray userRank = (JSONArray)jsonRes.get("userRank");
                    JSONArray userItem = (JSONArray)jsonRes.get("userItem");

                    // user information 우선 넣음

                    mMyName.setText(userInfo.getString("name"));
                    mMyMoney.setText(userInfo.getString("money"));

                    // 유저 랭킹 어댑터로 리스트뷰에 넣기
                    userRankingAdapter = new userRankingAdapter(getApplicationContext());//???
                    for ( int i = 0; i<userRank.length(); i++) {
                        String photo, name, rank;
                        JSONObject one = userRank.getJSONObject(i);
                        //photo = one.getString("")
                        photo = null;
                        name = one.getString("catname");
                        rank = one.getString("myrank");

                        userRankingAdapter.addItem(photo,name,rank);
                    }


                    mMyRanking.setAdapter(userRankingAdapter);
                    ListViewRankClickListener listViewRankClickListener = new ListViewRankClickListener();
                    mMyRanking.setOnItemClickListener(listViewRankClickListener);


                    // 아이템 리스트 어댑터로 리스트 뷰에 넣기




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        myRanking.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mMyRanking.setVisibility(View.VISIBLE);
                mMyItems.setVisibility(View.INVISIBLE);
                myRanking.setBackgroundColor(getResources().getColor(selectedBtn));
                myItems.setBackgroundColor(getResources().getColor(unselectedBtn));

            }


        });

        myItems.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view){

                mMyItems.setVisibility(View.VISIBLE);
                mMyRanking.setVisibility(View.INVISIBLE);
                myItems.setBackgroundColor(getResources().getColor(selectedBtn));
                myRanking.setBackgroundColor(getResources().getColor(unselectedBtn));

            }
        });




    }
    public class ListViewRankClickListener implements AdapterView.OnItemClickListener{
        String  pn;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*Intent intent = new Intent(getActivity(),PostDetail.class);
//            Log.e("**","*************");
            OnePost mData = PostViewAdapter.mListPost.get(position);
            intent.putExtra("title",mData.mTitle);
            intent.putExtra("writer",mData.mWriter);
            intent.putExtra("contents",mData.mContents);
            intent.putExtra("password",mData.mPassword);
            intent.putExtra("keynum",mData.mKeyNum);

            startActivity(intent);*/

        }
    }
}
