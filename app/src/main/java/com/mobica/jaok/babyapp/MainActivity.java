package com.mobica.jaok.babyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private static final String CLASS_NAME = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new MainGamePanel(this));

        int rId =  getResources().getIdentifier("ant", "mipmap", getPackageName());
        Log.d(CLASS_NAME, "View was added");
//        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStop() {
        Log.d(CLASS_NAME, "Stopping...");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(CLASS_NAME, "Destroying...");
        super.onDestroy();
    }
}
