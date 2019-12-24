package com.example.a3cteamworkapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;



public class GravityControl extends AppCompatActivity {
    OrientationEventListener mOrientationListener;
    private CarControl control;
    public boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity_control);

        Button button1 = (Button) findViewById(R.id.button_stop);
        Button button2 = (Button) findViewById(R.id.button_start);

        control = new CarControl(MainActivity.client);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                control.stop();
                flag=false;
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=true;
            }
        });

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;  //手机平放时，检测不到有效的角度
                }

                if (flag) {
                    if (orientation > 350 || orientation < 10) {
                        orientation = 0;
                        control.go();
                    } else if (orientation > 30 && orientation < 100) {
                        control.right();
                        orientation = 90;
                    } else if (orientation > 170 && orientation < 190) {
                        orientation = 180;
                        control.back();
                    } else if (orientation > 260 && orientation < 340) {
                        orientation = 270;
                        control.left();
                    } else {
                        return;
                    }
                }
            }

        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationListener.disable();
    }

}