package com.example.a3cteamworkapplication.face;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.a3cteamworkapplication.R;

public class FaceRecognitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        initView();
    }

    private void initView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String auth = AuthService.getAuth();
                Log.e("hedb", "initView: "+ auth);


            }
        }).start();


    }
}
