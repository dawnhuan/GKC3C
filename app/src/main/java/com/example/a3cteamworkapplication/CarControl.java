package com.example.a3cteamworkapplication;

import java.util.Timer;
import java.util.TimerTask;

public class CarControl {
    private BluetoothTool client;
    public static final String GO = "a";
    public static final String BACK = "b";
    public static final String LEFT = "c";
    public static final String RIGHT = "d";
    public static final String STOP = "e";
    public static final double turnRate = 1;

    public CarControl(BluetoothTool client)
    {
        this.client = client;
    }

    public void go()
    {
        BluetoothTool.WriteTask writeTask = client.new WriteTask(GO);
        writeTask.start();
    }
    public void back()
    {
        BluetoothTool.WriteTask writeTask = client.new WriteTask(BACK);
        writeTask.start();
    }
    public void left()
    {
        BluetoothTool.WriteTask writeTask = client.new WriteTask(LEFT);
        writeTask.start();
    }
    public void right()
    {
        BluetoothTool.WriteTask writeTask = client.new WriteTask(RIGHT);
        writeTask.start();
    }
    public void stop()
    {
        BluetoothTool.WriteTask writeTask = client.new WriteTask(STOP);
        writeTask.start();
    }
    public void go(double time)
    {
        go();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, (long)(time*1000));
    }
    public void back(double time)
    {
        back();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, (long)(time*1000));
    }
    public void left(double angle)
    {
        left();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, (long)(angle*turnRate));
    }
    public void right(double angle)
    {
        right();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, (long)(angle*turnRate));
    }
}
