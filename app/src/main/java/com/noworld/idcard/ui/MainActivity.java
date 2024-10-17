package com.noworld.idcard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.noworld.idcard.R;

public class MainActivity extends AppCompatActivity {


    private static final String REGISTER_ACTIVITY_ACTION_NAME
            = "com.noworld.idcard.ui.RegisterActivity";
    private static final String RECORDER_ACTIVITY_ACTION_NAME
            = "com.noworld.idcard.ui.RecorderActivity";

    private Button register_button;
    private Button record_button;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent();
                if (v.getId() == R.id.register_button) {
//                    intent.setAction(REGISTER_ACTIVITY_ACTION_NAME);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(new Intent(REGISTER_ACTIVITY_ACTION_NAME));
                } else if (v.getId() == R.id.record_button) {
                    intent.setAction(RECORDER_ACTIVITY_ACTION_NAME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        setTitle("身份证导入系统   设计者: NoWorld");

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(onClickListener);
        record_button = findViewById(R.id.record_button);
        record_button.setOnClickListener(onClickListener);
    }
}
