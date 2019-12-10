package com.example.a3cteamworkapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaintControl extends AppCompatActivity
{
    TextView toastText;
    Button clearBtn, goBtn;
    PaintView paintView;
    private CarControl control;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        paintView = findViewById(R.id.view_paint);
        clearBtn = findViewById(R.id.btn_paint_clear);
        goBtn = findViewById(R.id.btn_paint_go);
        toastText = findViewById(R.id.text_paint);

        control = new CarControl(MainActivity.client);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
                showStatus("Draw your next route");
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatus("Sending Commands...");

                //Todo: Complete logic that calculates route and sends them

                showStatus("Successfully sent all commands!");
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

}