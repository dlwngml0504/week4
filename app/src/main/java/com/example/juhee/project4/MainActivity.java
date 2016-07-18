package com.example.juhee.project4;

import android.*;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity{
    CallbackManager callbackManager;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private static final String TAG = "AppPermission";

    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create(); //로그인 응답을 처리할 콜백 관리자 생성
        LoginManager.getInstance().logOut();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","user_friends"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(MainActivity.this, "Login Succeed~!!", Toast.LENGTH_SHORT).show();
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                 /* handle the result */
                                // {Response:  responseCode: 200, graphObject: {"name":"Lee  Ju Hee","id":"991091207678109"}, error: null}\




                                // Send Login information to Server using Socket
                                try {
                                    mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
                                    mSocket.off("init");
                                    mSocket.off("initRes");
                                } catch (Exception e) {}
                                mSocket.connect();

                                mSocket.emit("init",response.getJSONObject());


                                // Get response
                                mSocket.on("initRes", new Emitter.Listener() {
                                    @Override
                                    public void call(final Object... args){
                                        Log.e("HHH","HearBeat Response");
                                        JSONObject jsonRes = (JSONObject) args[0];
                                    }
                                });


                                Intent intent = new Intent(MainActivity.this,MapPane.class);
                                intent.putExtra("userinfo",response.getJSONObject().toString());
                                startActivity(intent);
                            }
                        }
                ).executeAsync();
            }


            @Override
            public void onCancel() {
                // App code
                Toast.makeText(getApplicationContext(), "Login Cancel", Toast.LENGTH_SHORT).show();
                Log.e("After","Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getApplicationContext(), "Login Error", Toast.LENGTH_SHORT).show();
                Log.e("After","Error");
            }
        });

        Button mapButton = (Button)findViewById(R.id.mapBtn);
        mapButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity.this,MapPane.class);
                startActivity(intent);
            }
        });

        Button cameraButton = (Button)findViewById(R.id.cameraBtn);
        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(MainActivity.this,CameraView.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        Log.i(TAG, "CheckPermission : " + checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            Log.e(TAG, "permission deny");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! do the
                    // calendar task you need to do.


                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}

