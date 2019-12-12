package com.example.a3cteamworkapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Vector;

import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

public class PaintControl extends AppCompatActivity
{
    TextView toastText;
    Button clearBtn, goBtn;
    PaintView paintView;
    private Vector<PaintView.pointT> localRoute;

    private CarControl control;
    static final private double moveTimeRatio = 0.5;
    static final private double turnTimeRatio = 1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        paintView = findViewById(R.id.view_paint);
        clearBtn = findViewById(R.id.btn_paint_clear);
        goBtn = findViewById(R.id.btn_paint_go);
        toastText = findViewById(R.id.text_paint);
        localRoute = new Vector<>();
        control = new CarControl(MainActivity.client);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
                showStatus("Cleared.\nPlease draw the next route here.");
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatus("Read " + paintView.route.size() +
                        " sample points.\nSending messages...");

                goBtn.setEnabled(false);
                localRoute = new Vector<PaintView.pointT>(paintView.route.size());

                localRoute = (Vector) paintView.route.clone();
                moveStraight(0);

                for(int pStrt = 1; pStrt < paintView.route.size() - 2; pStrt++){
                    turnAway(pStrt);
                    moveStraight(pStrt);
                }

                localRoute.clear();
                goBtn.setEnabled(true);

                showStatus("Last command read " + paintView.route.size() +
                        " sample points.\nPlease draw your next route here.");
            }
        });
    }

    private void showStatus(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toastText.setText(string);
            }
        });
    }

    private void moveStraight(int sindex){
        if(sindex+1 < localRoute.size() && sindex>0){
            double time = moveTimeRatio * Dist(localRoute.elementAt(sindex), localRoute.elementAt(sindex+1));
            control.go_wait(time);
        }
    }

    private void turnAway(int sindex){
        if(sindex+1 < localRoute.size() && sindex>0){
            double angle = turnTimeRatio * Angle(
                    localRoute.elementAt(sindex-1),
                    localRoute.elementAt(sindex),
                    localRoute.elementAt(sindex+1)
            );
            if(angle > 0)
                control.left_wait(angle);
            else
                control.right_wait(-angle);
        }
    }

    private double Dist(PaintView.pointT p1, PaintView.pointT p2){
        return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    private double Angle(PaintView.pointT p1, PaintView.pointT p2, PaintView.pointT p3){
        double prod = (p2.x - p1.x)*(p3.x - p2.x) + (p2.y - p1.y)*(p3.y - p2.y);
        int crossSign = ((p2.x - p1.x)*(p3.y - p2.y) - (p2.y - p1.y)*(p3.x - p2.x) > 0)? 1 : -1;
        return crossSign * acos(prod / (Dist(p1, p2) * Dist(p2, p3)));
    }
}