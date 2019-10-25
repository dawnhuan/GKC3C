package com.example.a3cteamworkapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BasicControl extends AppCompatActivity {

    private Button go, back, left, right;
    private CarControl control;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_control);

        go = (Button) findViewById(R.id.go);
        back = (Button) findViewById(R.id.back);
        right = (Button) findViewById(R.id.right);
        left = (Button) findViewById(R.id.left);

        control = new CarControl(MainActivity.client);

        go.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    control.go();
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    control.stop();
                return false;
            }
        });
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    control.back();
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    control.stop();
                return false;
            }
        });
        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    control.left();
                    time = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    control.stop();
                    time = System.currentTimeMillis() - time;
                    //Toast.makeText(BasicControl.this, Long.toString(time), Toast.LENGTH_LONG).show();  //test for the rate between angle and time
                }
                return false;
            }
        });
        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    control.right();
                    time = System.currentTimeMillis();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    control.stop();
                    time = System.currentTimeMillis() - time;
                    //Toast.makeText(BasicControl.this, Long.toString(time), Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });


    }
}
