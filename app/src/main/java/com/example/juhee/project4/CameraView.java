package com.example.juhee.project4;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class CameraView extends AppCompatActivity {
    private final static String TAG = "Camera2testJ";
    private Size mPreviewSize;
    Intent intent = null;
    public Socket mSocket;
    final String SERVER_IP = "52.78.66.95";
    final String SERVER_PORT = ":8124";
    private TextureView mTextureView;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    JSONObject user = null;
    Integer item1_num;
    Integer item2_num;
    Integer item3_num;
    Integer item4_num;
    Integer item5_num;
    Integer item6_num;
    Integer item7_num;


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.camera_view_activity);
        intent = getIntent();
        try {
            user = new JSONObject(intent.getStringExtra("userinfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
            mSocket.off("useritem");
            mSocket.off("useritemRes");
        } catch (Exception e) {}
        mSocket.connect();
        JSONObject jo = new JSONObject();
        try {
            jo.put("userid",user.getString("id"));
            Log.e("CameraView-----",jo.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("useritem",jo);
        mSocket.on("useritemRes", new Emitter.Listener() {
            @Override
            public void call(final Object... args){
                JSONArray ja = (JSONArray) args[0];
                try {
                    item1_num = ja.getJSONObject(0).getInt("1");
                    item2_num = ja.getJSONObject(1).getInt("2");
                    item3_num = ja.getJSONObject(2).getInt("3");
                    item4_num = ja.getJSONObject(3).getInt("4");
                    item5_num = ja.getJSONObject(4).getInt("5");
                    item6_num = ja.getJSONObject(5).getInt("6");
                    item7_num = ja.getJSONObject(6).getInt("7");
                    Log.e("CameraView+++++",ja.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button item1 = (Button)findViewById(R.id.item1);
        Button item2 = (Button)findViewById(R.id.item2);
        Button item3 = (Button)findViewById(R.id.item3);
        Button item4 = (Button)findViewById(R.id.item4);
        Button item5 = (Button)findViewById(R.id.item5);
        Button item6 = (Button)findViewById(R.id.item6);
        Button item7 = (Button)findViewById(R.id.item7);

        if (item1!=null) {
            item1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(1,item1_num);
                }
            });
        }
        if (item2!=null) {
            item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(2,item2_num);
                }
            });
        }
        if (item3!=null) {
            item3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(3,item3_num);
                }
            });
        }
        if (item4!=null) {
            item4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(4,item4_num);
                }
            });
        }
        if (item5!=null) {
            item5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(5,item5_num);
                }
            });
        }
        if (item6!=null) {
            item6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(6,item6_num);
                }
            });
        }
        if (item7!=null) {
            item7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemInfo(7,item7_num);
                }
            });
        }
        mTextureView = (TextureView) findViewById(R.id.texture);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    private void getItemInfo(final int i, final int item_num){
        Log.e("USEITEM","***************");
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraView.this);
        if (item_num==0) {
            builder.setTitle("아이템을 사용하시겠습니까?")
                    .setMessage("보유하고 있는 아이템 수 : 0 \n아이템을 사용하실 수 없습니다.")
                    .setCancelable(false)
                    .setNegativeButton("돌아가기", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int whichButton){
                            dialog.cancel();
                        }
                    });
        }
        else {
            builder.setTitle("아이템을 사용하시겠습니까?")
                    .setMessage("보유하고 있는 아이템 수 : " +item_num)
                    .setCancelable(false)
                    .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int whichButton){
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("사용하기", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int whichButton){
                            try {
                                mSocket = IO.socket("http://"+SERVER_IP+SERVER_PORT);
                                mSocket.off("useitem");
                            } catch (Exception e) {}
                            mSocket.connect();
                            JSONObject jo = new JSONObject();
                            try {
                                jo.put("iteminfo",i);

                                jo.put("userid",user.getString("id"));
                                jo.put("catname",intent.getStringExtra("catname"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSocket.emit("useitem",jo);
                            Log.e("Cameraview Alert",jo.toString());
                            if (i==1){
                                item1_num--;
                            }
                            else if (i==2){
                                item2_num--;
                            }
                            else if (i==3){
                                item3_num--;
                            }
                            else if (i==4){
                                item4_num--;
                            }
                            else if (i==5){
                                item5_num--;
                            }
                            else if (i==6){
                                item6_num--;
                            }
                            else if (i==7){
                                item7_num--;
                            }
                            dialog.cancel();
                        }
                    });

        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void openCamera() {

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "openCamera E");
        try {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            ActivityCompat.requestPermissions(CameraView.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);

            if (ActivityCompat.checkSelfPermission(CameraView.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                Log.e("MainActivity","************");
                //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.e(TAG, "onSurfaceTextureAvailable, width="+width+",height="+height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            //Log.e(TAG, "onSurfaceTextureUpdated");
        }

    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {

            Log.e(TAG, "onOpened");
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

            Log.e(TAG, "onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {

            Log.e(TAG, "onError");
        }

    };

    @Override
    protected void onPause() {

        Log.e(TAG, "onPause");
        super.onPause();
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    protected void startPreview() {

        if(null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            Log.e(TAG, "startPreview fail, return");
            return;
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if(null == texture) {
            Log.e(TAG,"texture is null, return");
            return;
        }

        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {

            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {

                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                    Toast.makeText(CameraView.this, "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {

            e.printStackTrace();
        }
    }

    protected void updatePreview() {

        if(null == mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {

            e.printStackTrace();
        }
    }
}