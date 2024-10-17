package com.noworld.idcard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.noworld.idcard.R;
import com.noworld.idcard.service.IdCardSystemService;

import java.util.Timer;
import java.util.TimerTask;


public class Welcome extends Activity {

    private final String TAG = "Welcome";

    //private PublicData p;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        //p = (PublicData) this.getApplication();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                next();
            }
        };
        //延时跳转
        Timer time = new Timer();
        time.schedule(t, 1500);
    }//

    //查询数据库，不存在就创建
    private void next() {
        Log.i(TAG, "next");
        Intent intentOne = new Intent(this, IdCardSystemService.class);
        startService(intentOne);

        Intent in = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(in);
        finish();
    }
}