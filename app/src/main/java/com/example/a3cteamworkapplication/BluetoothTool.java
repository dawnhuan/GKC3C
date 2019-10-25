package com.example.a3cteamworkapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothTool {

    private static final String TAG = "BluetoothTool";

    private BluetoothDevice device;
    private BluetoothSocket socket;

    private ReadTask readTask;

    private Handler handler;
    public static final int DISCONNECT = 6;

    public static final int CONNECT_FAILED = 1;
    public static final int CONNECT_SUCCESS = 5;
    public static final int READ_FAILED = 2;
    public static final int WRITE_FAILED = 3;
    public static final int DATA = 4;

    private boolean isConnect = false;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothTool(BluetoothDevice device, Handler handler) {
        this.device = device;
        this.handler = handler;
    }

    public void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket tmp = null;
                Method method;
                try {
                    method = device.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class);
                    tmp = (BluetoothSocket) method.invoke(device, MY_UUID);
                } catch (Exception e) {
                    setState(CONNECT_FAILED);
                    Log.e(TAG, e.toString());
                }
                socket = tmp;
                try {
                    socket.connect();
                    isConnect = true;
                    setState(CONNECT_SUCCESS);
                    BluetoothTool.this.readTask = new ReadTask();
                    BluetoothTool.this.readTask.start();
                } catch (Exception e) {
                    setState(CONNECT_FAILED);
                    Log.e(TAG, e.toString());
                }
            }
        });
        new Thread(thread).start();
    }

    public void disconnect() {
        try {
            socket.close();
            socket = null;
            if(readTask != null) {
                readTask.join();
            }
            isConnect = false;
            setState(DISCONNECT);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public class ReadTask extends Thread {
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            InputStream inputStream;
            while(true) {
                try {
                    inputStream = socket.getInputStream();
                    bytes = inputStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); // pause and wait for the rest of the data
                        bytes = inputStream.available();
                        bytes = inputStream.read(buffer, 0, bytes);
                        byte[] buf_data = new byte[bytes];
                        for(int i = 0; i < bytes; i++)
                            buf_data[i] = buffer[i];
                        String s = new String(buf_data);
                        Log.d(TAG, String.valueOf(bytes));
                        Log.d(TAG, s);
                        Message msg = handler.obtainMessage();
                        msg.what = DATA;
                        msg.obj = s;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    setState(READ_FAILED);
                    Log.e(TAG, e.toString());
                    break;
                }
            }
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    public class WriteTask extends Thread {
        private String str;

        public WriteTask(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            OutputStream outputStream = null;
            byte[] out_data = str.getBytes();
            try {
                outputStream = socket.getOutputStream();
                outputStream.write(out_data);
            } catch (Exception e) {
                setState(WRITE_FAILED);
                e.printStackTrace();
            }
        }
    }

    private void setState(int mes) {
        Message message = new Message();
        message.what = mes;
        handler.sendMessage(message);
    }
}
