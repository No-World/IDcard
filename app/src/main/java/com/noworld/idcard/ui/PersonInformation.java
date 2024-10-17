package com.noworld.idcard.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.noworld.idcard.R;
import com.noworld.idcard.data.PersonIdCard;
import com.noworld.idcard.service.IdCardSystemService;

public class PersonInformation extends AppCompatActivity {

    private TextView name;
    private TextView sex;
    private TextView nation;
    private TextView idcard;
    private TextView address;
    private TextView date;
    private ImageView avatar;

    private PersonIdCard personIdCard;
    private IdCardSystemService mService = null;
    private boolean isBind = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
        setTitle("身份证导入系统   设计者: NoWorld");

        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        nation = findViewById(R.id.nation);
        idcard = findViewById(R.id.idcard);
        address = findViewById(R.id.address);
        date = findViewById(R.id.date);
        avatar = findViewById(R.id.img_avatar);

        personIdCard = getIntent().getParcelableExtra("person");
        name.setText(personIdCard.getName());
        sex.setText(personIdCard.getSex());
        nation.setText(personIdCard.getNation());
        idcard.setText(personIdCard.getIdCard());
        address.setText(personIdCard.getAddress());
        date.setText(personIdCard.getDate_of_birth());
        byte[] avatarData = personIdCard.getAvatar();

        Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarData, 0, avatarData.length);
        avatar.setImageBitmap(avatarBitmap);

        Intent intent = new Intent(this, IdCardSystemService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IdCardSystemService.MyBinder binder = (IdCardSystemService.MyBinder) service;
            mService = binder.getService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBind = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBind) {
            unbindService(mConnection);
            isBind = false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_person_infomation, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        if (item.getItemId() == R.id.Delete) {
            // delete person
            if (mService != null) {
                mService.deleteDataFromSql(personIdCard);
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK); // 设置结果以指示数据已删除
            finish();
        }
        return true;
    }

}
