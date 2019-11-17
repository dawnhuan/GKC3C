package com.example.a3cteamworkapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
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

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int SCAN_PERIOD = 1000;

    private TextView statusTV;
    private Button searchBtn;
    private Button stopSearchBtn;
    private Button disconnectBtn;
    private ListView devicesLV;
    private Button BasicBtn, VoiceBtn, takeoffBtn, killBtn, disarmBtn, manualBtn;
    private ScrollView scrollView;
    private TextView receiveTV;
    private EditText sendET;
    private Button sendBtn;

    private ArrayAdapter<String> adapter;

    private boolean searching = false;

    private List<String> devicesArrayList = new ArrayList<>();

    private BluetoothReceiver bluetoothReceiver;
    private BluetoothAdapter bluetoothAdapter;

    public static BluetoothTool client;

    private static final int REQUEST_ENABLE = 1;
    private static final int REQUEST_DISCOVERABLE = 2;

    private StringBuilder receiveStringBuilder = new StringBuilder();

    private long exitTime;

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
                    takeoffBtn.setEnabled(true);
                    killBtn.setEnabled(true);
                    disarmBtn.setEnabled(true);
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
                    receiveStringBuilder.append((String)msg.obj);
                    showReceiveString(receiveStringBuilder.toString());
                    break;
                case BluetoothTool.DISCONNECT:
                    showStatus("已断开连接");
                    searchBtn.setEnabled(true);
                    stopSearchBtn.setEnabled(false);
                    disconnectBtn.setEnabled(false);
                    BasicBtn.setEnabled(false);
                    VoiceBtn.setEnabled(false);
                    takeoffBtn.setEnabled(false);
                    killBtn.setEnabled(false);
                    disarmBtn.setEnabled(false);
                    manualBtn.setEnabled(false);
                    devicesLV.setEnabled(true);
                    sendBtn.setEnabled(false);
                    break;
            }
        }
    };

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

    private void setupUI() {
        statusTV = (TextView) findViewById(R.id.statusTV);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        stopSearchBtn = (Button) findViewById(R.id.stopSearchBtn);
        disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        devicesLV = (ListView) findViewById(R.id.devicesLV);

        BasicBtn = (Button) findViewById(R.id.BasicBtn);
        VoiceBtn = (Button) findViewById(R.id.VoiceBtn);
        takeoffBtn = (Button) findViewById(R.id.oneMoreBtn);
        killBtn = (Button) findViewById(R.id.againOneBtn);
        disarmBtn = (Button) findViewById(R.id.dioBtn);
        manualBtn = (Button) findViewById(R.id.finalBtn);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        receiveTV = (TextView) findViewById(R.id.receiveTV);
        sendET = (EditText) findViewById(R.id.sendET);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        stopSearchBtn.setEnabled(false);
        disconnectBtn.setEnabled(false);
        BasicBtn.setEnabled(false);
        VoiceBtn.setEnabled(false);
        takeoffBtn.setEnabled(false);
        killBtn.setEnabled(false);
        disarmBtn.setEnabled(false);
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
                if(bluetoothAdapter != null)
                    bluetoothAdapter.startDiscovery();
            }
        });

        stopSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searching = false;
                stopSearchBtn.setEnabled(false);
                searchBtn.setEnabled(true);
                if(bluetoothAdapter != null)
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
                if(searching) {
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

        takeoffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog("arm\n");
                showConfirmDialog("takeoff\n");
            }
        });

        killBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showConfirmDialog("kill\n");
                BluetoothTool.WriteTask writeTask = client.new WriteTask("disarm\n");
                writeTask.start();
                Toast.makeText(MainActivity.this, "已发送", Toast.LENGTH_SHORT).show();
            }
        });

        disarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog("kill\n");
            }
        });

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog("manual\n");
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
        if(!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(enable, REQUEST_DISCOVERABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DISCOVERABLE:
                if(resultCode == Activity.RESULT_CANCELED) {
                    showStatus("蓝牙已关闭");
                } else {
                    showStatus("蓝牙已打开");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String str = device.getName() + " | " + device.getAddress();
                Log.d(TAG, str);
                if(devicesArrayList.indexOf(str) == -1)
                    devicesArrayList.add(str);
                showDevices();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                showStatus("已停止搜索");
                searching = false;
                searchBtn.setEnabled(true);
                stopSearchBtn.setEnabled(false);
            }
        }
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

    private void showReceiveString(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receiveTV.setText(string);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void showConfirmDialog(final String action) {
        String str = "";
        if(action.equals("start\n")) {
            str = "启动";
        } else if(action.equals("arm\n")) {
            str = "解锁";
        } else if(action.equals("takeoff\n")) {
            str = "起飞";
        } else if(action.equals("kill\n")) {
            str = "紧急停止";
        } else if(action.equals("disarm\n")) {
            str = "锁定";
        } else if(action.equals("manual\n")) {
            str = "手动控制";
        } else if(action.equals("test\n")) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
        return true;
    }

    public void requestPermissions()
    {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }

                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
