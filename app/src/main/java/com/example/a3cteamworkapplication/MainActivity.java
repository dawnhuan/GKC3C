package com.example.a3cteamworkapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.a3cteamworkapplication.face.AuthService;
import com.example.a3cteamworkapplication.face.FaceBean;
import com.example.a3cteamworkapplication.face.utils.GsonUtils;
import com.example.a3cteamworkapplication.face.utils.ImageToBase64;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;

@TargetApi(11)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int SCAN_PERIOD = 1000;
    private static final int REQUEST_ENABLE = 1;
    private static final int REQUEST_DISCOVERABLE = 2;
    public static BluetoothTool client;
    private TextView statusTV;
    private Button searchBtn;
    private Button stopSearchBtn;
    private Button disconnectBtn;
    private ListView devicesLV;
    private Button BasicBtn, VoiceBtn, FaceBtn, killBtn, PaintBtn, manualBtn;
    private ScrollView scrollView;
    private TextView receiveTV;
    private EditText sendET;
    private Button sendBtn;
    private ArrayAdapter<String> adapter;
    private boolean searching = false;
    private List<String> devicesArrayList = new ArrayList<>();
    private BluetoothReceiver bluetoothReceiver;
    private BluetoothAdapter bluetoothAdapter;
    private StringBuilder receiveStringBuilder = new StringBuilder();
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothTool.CONNECT_FAILED:
                    showStatus("连接失败");
                    try {
                        client.connect();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    break;
                case BluetoothTool.CONNECT_SUCCESS:
                    showStatus("连接成功");
                    searchBtn.setEnabled(false);
                    disconnectBtn.setEnabled(true);
                    BasicBtn.setEnabled(true);
                    VoiceBtn.setEnabled(true);
                    FaceBtn.setEnabled(true);
                    killBtn.setEnabled(true);
                    PaintBtn.setEnabled(true);
                    manualBtn.setEnabled(true);
                    devicesLV.setEnabled(false);
                    sendBtn.setEnabled(true);
                    break;
                case BluetoothTool.READ_FAILED:
                    showStatus("读取失败");
                    break;
                case BluetoothTool.WRITE_FAILED:
                    showStatus("写入失败");
                    break;
                case BluetoothTool.DATA:
                    receiveStringBuilder.append((String) msg.obj);
                    showReceiveString(receiveStringBuilder.toString());
                    break;
                case BluetoothTool.DISCONNECT:
                    showStatus("已断开连接");
                    searchBtn.setEnabled(true);
                    stopSearchBtn.setEnabled(false);
                    disconnectBtn.setEnabled(false);
                    BasicBtn.setEnabled(false);
                    VoiceBtn.setEnabled(false);
                    FaceBtn.setEnabled(false);
                    killBtn.setEnabled(false);
                    PaintBtn.setEnabled(false);
                    manualBtn.setEnabled(false);
                    devicesLV.setEnabled(true);
                    sendBtn.setEnabled(false);
                    break;
            }
        }
    };
    private long exitTime;
    private ProgressDialog progressDialog;
    private CarControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();

        setupUI();

        enableBluetooth();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        bluetoothReceiver = new BluetoothReceiver();
        registerReceiver(bluetoothReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
        return true;
    }

    public void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS}, 0x0010);
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        statusTV = (TextView) findViewById(R.id.statusTV);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        stopSearchBtn = (Button) findViewById(R.id.stopSearchBtn);
        disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        devicesLV = (ListView) findViewById(R.id.devicesLV);

        BasicBtn = (Button) findViewById(R.id.BasicBtn);
        VoiceBtn = (Button) findViewById(R.id.VoiceBtn);
        FaceBtn = (Button) findViewById(R.id.FaceBtn);
        killBtn = (Button) findViewById(R.id.againOneBtn);
        PaintBtn = (Button) findViewById(R.id.paintBtn);
        manualBtn = (Button) findViewById(R.id.finalBtn);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        receiveTV = (TextView) findViewById(R.id.receiveTV);
        sendET = (EditText) findViewById(R.id.sendET);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        stopSearchBtn.setEnabled(false);
        disconnectBtn.setEnabled(false);
        BasicBtn.setEnabled(false);
        VoiceBtn.setEnabled(false);
        FaceBtn.setEnabled(false);
        killBtn.setEnabled(false);
        PaintBtn.setEnabled(false);
        manualBtn.setEnabled(false);
        sendBtn.setEnabled(false);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searching = true;
                searchBtn.setEnabled(false);
                stopSearchBtn.setEnabled(true);
                showStatus("搜索中...");
                devicesArrayList.clear();
                showDevices();
                if (bluetoothAdapter != null)
                    bluetoothAdapter.startDiscovery();
            }
        });

        stopSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searching = false;
                stopSearchBtn.setEnabled(false);
                searchBtn.setEnabled(true);
                if (bluetoothAdapter != null)
                    bluetoothAdapter.cancelDiscovery();
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devicesArrayList);
        devicesLV.setAdapter(adapter);
        devicesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (searching) {
                    bluetoothAdapter.cancelDiscovery();
                    searching = false;
                    searchBtn.setEnabled(true);
                    stopSearchBtn.setEnabled(false);
                }
                String str = devicesArrayList.get(position);
                String macAddress = str.split(" \\| ", 2)[1];
                Toast.makeText(MainActivity.this, macAddress, Toast.LENGTH_SHORT).show();
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
                client = new BluetoothTool(device, handler);
                try {
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        BasicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BasicControl.class);
                startActivity(intent);
            }
        });

        VoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VoiceControl.class);
                startActivity(intent);
            }
        });

        FaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCamera();
            }
        });


        killBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GravityControl.class);
                startActivity(intent);
            }
        });

        PaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PaintControl.class);
                startActivity(intent);
            }
        });

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothTool.WriteTask writeTask = client.new WriteTask(sendET.getText().toString() + "\n");
                writeTask.start();
            }
        });
    }

    private void enableBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(enable, REQUEST_DISCOVERABLE);
    }

    private void showStatus(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTV.setText(string);
            }
        });
    }

    private void showDevices() {
        adapter.notifyDataSetChanged();
        devicesLV.smoothScrollToPosition(devicesArrayList.size() - 1);
    }

    /**
     * 调用相机
     */
    private void takeCamera() {
        RxGalleryFinalApi.openZKCamera(this);
    }

    private void showConfirmDialog(final String action) {
        String str = "";
        if (action.equals("start\n")) {
            str = "启动";
        } else if (action.equals("arm\n")) {
            str = "解锁";
        } else if (action.equals("takeoff\n")) {
            str = "起飞";
        } else if (action.equals("kill\n")) {
            str = "紧急停止";
        } else if (action.equals("disarm\n")) {
            str = "锁定";
        } else if (action.equals("manual\n")) {
            str = "手动控制";
        } else if (action.equals("test\n")) {
            str = "启用视觉";
        }

        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(MainActivity.this);
        confirmDialog.setTitle("确认");
        confirmDialog.setMessage("确认 " + str + " ?");
        confirmDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BluetoothTool.WriteTask writeTask = client.new WriteTask(action);
                writeTask.start();
                Toast.makeText(MainActivity.this, "已发送", Toast.LENGTH_SHORT).show();
            }
        });
        confirmDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirmDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DISCOVERABLE:
                if (resultCode == Activity.RESULT_CANCELED) {
                    showStatus("蓝牙已关闭");
                } else {
                    showStatus("蓝牙已打开");
                }
                break;

            case RxGalleryFinalApi.TAKE_IMAGE_REQUEST_CODE:
                startFaceDetect();
                break;
            default:
                break;
        }
    }

    /**
     * 调用百度接口开始人脸识别
     */
    private void startFaceDetect() {
        final String imagePath = RxGalleryFinalApi.fileImagePath.getPath();  //image 工具类
        buildProgressDialog();
        //开启子线程进行网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String authToken = AuthService.getAuth();
                Log.e("hedb", "initView: " + authToken);
                String base64 = ImageToBase64.imgToBase64(ImageToBase64.compressImage(imagePath));
                String faceDetect = AuthService.faceDetect(base64, authToken);
                Log.e("hedb", "run: " + faceDetect);
                final FaceBean faceBean = GsonUtils.fromJson(faceDetect, FaceBean.class);
                cancelProgressDialog();
                //主线程中更新UI操作
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        type 里面返回 smile 和 laugh 的时候 发出 control.stop (); none 的时候不发出
//                        yaw  左旋转角度超过30的时候发出 control.left();  右旋转角度超过30时候发出 control.right()
//                        pitch 上旋转超过 8时候发出control.go () ; 下旋转角度超过8时候发出 control.back();
//
                        if (faceBean == null) {
                            return;
                        }

                        if (client!=null){
                            control = new CarControl(MainActivity.client);
                        }else {
                            Toast.makeText(MainActivity.this, "蓝牙设备未连接", Toast.LENGTH_SHORT).show();
                        }

                        if (faceBean.getError_code() == 0) {
                            FaceBean.ResultBean.FaceListBean faceListBean = faceBean.getResult().getFace_list().get(0);
                            FaceBean.ResultBean.FaceListBean.AngleBean angle = faceListBean.getAngle();
                            FaceBean.ResultBean.FaceListBean.ExpressionBean expression = faceListBean.getExpression();
                            double pitch = angle.getPitch();
                            double yaw = angle.getYaw();
                            String type = expression.getType();
                            double priority =0;
                            //none:不笑；smile:微笑；laugh:大笑
                            switch (type) {
                                case "none":
                                    Toast.makeText(MainActivity.this, "not smile时不发出", Toast.LENGTH_SHORT).show();
                                    break;
                                case "smile":
                                case "laugh":
                                    Toast.makeText(MainActivity.this, "smile的时候旋转180", Toast.LENGTH_SHORT).show();
                                    if (control!=null){
                                        control.left(180);
                                    }
                                    priority =1;
                                    break;
                            }
                            if (priority==0) {
                                if (pitch > 0 && pitch > 15) {//下旋转角度超过8时候发出 control.back();
                                    Toast.makeText(MainActivity.this, "下旋转角度超过15时候后退", Toast.LENGTH_SHORT).show();
                                    if (control!=null){
                                        control.back(1);
                                    }

                                } else if (pitch < 0 && pitch < -8) {//上旋转超过 30时候发出control.go ()
                                    Toast.makeText(MainActivity.this, "上旋转超过8时候发出前进", Toast.LENGTH_SHORT).show();
                                    if (control!=null){
                                        control.go(1);
                                    }
                                }

                                if (yaw < 0 && yaw < -30) {//左旋转角度超过30的时候发出 control.left()
                                    Toast.makeText(MainActivity.this, "左旋转角度超过30的时候左转", Toast.LENGTH_SHORT).show();
                                    if (control!=null){

                                        control.right(90);
                                    }
                                } else if (yaw > 0 && yaw > 30) {// 右旋转角度超过30时候发出 control.right()
                                    Toast.makeText(MainActivity.this, "右旋转角度超过30时候右转", Toast.LENGTH_SHORT).show();
                                    if (control!=null){

                                        control.left(90);
                                    }
                                }
                            }



                        } else {
                            Toast.makeText(MainActivity.this, "人脸识别失败 " + faceBean.getError_msg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                // 使用文件 File 获取 Compress 实例
                Bitmap smallBitmap = ImageToBase64.getSmallBitmap(imagePath);

            }
        }).start();
    }

    public void buildProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("人脸识别中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    public void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }

    private void showReceiveString(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receiveTV.setText(string);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = device.getName() + " | " + device.getAddress();
                Log.d(TAG, str);
                if (devicesArrayList.indexOf(str) == -1)
                    devicesArrayList.add(str);
                showDevices();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showStatus("已停止搜索");
                searching = false;
                searchBtn.setEnabled(true);
                stopSearchBtn.setEnabled(false);
            }
        }
    }
}
