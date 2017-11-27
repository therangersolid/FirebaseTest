package com.team5.emergencyapp.firebasetest.view.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.team5.emergencyapp.firebasetest.R;
import com.team5.emergencyapp.firebasetest.core.controller.Run;

public class Activity_Main extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Run.initialize();
            }
        });
        t.start();


    }
}
