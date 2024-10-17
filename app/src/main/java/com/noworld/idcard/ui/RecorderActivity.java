package com.noworld.idcard.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.noworld.idcard.R;
import com.noworld.idcard.adapter.InfoAdapter;
import com.noworld.idcard.data.PersonIdCard;
import com.noworld.idcard.data.PublicData;

import java.util.ArrayList;

public class RecorderActivity extends AppCompatActivity {

    private TextView textView;
    private ListView listView;
    private InfoAdapter infoAdapter;
    private PublicData p;
    private ArrayList<PersonIdCard> info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        setTitle("身份证导入系统   设计者: NoWorld");

        initView();
        selectPublicData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initView();
        selectPublicData();
    }

    private void initView() {
        setContentView(R.layout.recorder_activity);

        textView = findViewById(R.id.no_info);
        listView = findViewById(R.id.choose_person_list);
        listView.setOnItemClickListener(onItemClickListener);

        p = (PublicData) getApplication();

    }

    private void selectPublicData() {
        info = p.getPersonList();
        if (!info.isEmpty()) {
            textView.setVisibility(View.GONE);
            infoAdapter = new InfoAdapter(this);
            infoAdapter.setPerson_list(info);
            listView.setAdapter(infoAdapter);
            dataChanged();
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            reviewPerson(position);
        }
    };

    private void dataChanged() {
        infoAdapter.notifyDataSetChanged();
    }

    private void reviewPerson(int position) {
        Intent intent = new Intent(this, PersonInformation.class);
        intent.putExtra("person", info.get(position));
        startActivity(intent);
    }
}
