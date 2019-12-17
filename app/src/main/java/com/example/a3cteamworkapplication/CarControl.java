package com.example.a3cteamworkapplication;

import java.util.Timer;
import java.util.TimerTask;
import java.lang.Thread;

public class CarControl {
    private BluetoothTool client;
    public static final String GO = "a";
    public static final String BACK = "g";
    public static final String LEFT = "b";
    public static final String RIGHT = "c";
    public static final String STOP = "s";
    public static final double leftRate = 1050.0 / 90.0;
    public static final double rightRate = 1200.0 / 90.0;

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
    public void go(double time) //前进时间
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
    public void left(double angle) //转向的角度
    {
        left();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, (long)(angle*leftRate));
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
        timer.schedule(task, (long)(angle*rightRate));
    }
    public void go_wait(double time) //前进时间
    {
        go();

        try{
            Thread.sleep((long)time);
        }catch (Exception e){}
        stop();
    }
    public void back_wait(double time)
    {
        back();

        try{
            Thread.sleep((long)time);
        }catch (Exception e){}
        stop();
    }
    public void left_wait(double angle) //转向的角度
    {
        left();

        try{
            Thread.sleep((long)(angle*leftRate));
        }catch (Exception e){}
        stop();
    }
    public void right_wait(double angle)
    {
        right();

        try{
            Thread.sleep((long)(angle*rightRate));
        }catch (Exception e){}
        stop();
    }
}
