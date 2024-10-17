package com.noworld.idcard.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.TextView;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.noworld.idcard.R;
import com.noworld.idcard.aidl.IRegisterCallback;
import com.noworld.idcard.data.PersonIdCard;
import com.noworld.idcard.service.IdCardSystemService;
import com.noworld.idcard.utils.Utils;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";

    private Button register;
    private RadioGroup sex_radio_group;
    private RadioButton radio_button_man;
    private RadioButton radio_button_woman;
    private EditText edit_name;
    private EditText edit_nation;
    private EditText edit_idcard;
    private EditText edit_address;
    private LinearLayout window;
    private TextView birth;
    private ImageView img_avatar;

    private PersonIdCard personIdCard = new PersonIdCard();

    private Context mContext = null;
    private IdCardSystemService mService = null;
    private boolean isBind = false;

    private static class UIHandler extends Handler {
        private final WeakReference<RegisterActivity> activityReference;

        UIHandler(RegisterActivity activity, Looper looper) {
            super(looper);
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            RegisterActivity activity = activityReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    Log.i(activity.TAG, "dasdasd   " + activity.personIdCard.getDate_of_birth());
                    activity.birth.setVisibility(View.VISIBLE);
                    activity.birth.setText(activity.personIdCard.getDate_of_birth());
                    activity.register.setEnabled(false);
                    activity.register.setText("保存成功");
                    activity.edit_name.setFocusable(false);
                    activity.edit_nation.setFocusable(false);
                    activity.edit_idcard.setFocusable(false);
                    activity.edit_address.setFocusable(false);
                    activity.img_avatar.setClickable(false);
                    activity.toastOutput("注册成功,点击任意地方退出");
                }
            }
        }
    }

    private final UIHandler uiHandler = new UIHandler(this, Looper.getMainLooper());

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBind = true;
            Log.i(TAG, "service: " + service);
            IdCardSystemService.MyBinder binder = (IdCardSystemService.MyBinder) service;
            mService = binder.getService();
            mService.registerListener(registerCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

    };

    // 错误码
    private final int NAME_ERROR = 1;
    private final int SEX_ERROR = 2;
    private final int NATION_ERROR = 3;
    private final int IDCARD_NULL_ERROR = 4;
    private final int IDCARD_INSUFFICIENT_ERROR = 5;
    private final int IDCARD_ERROR = 6;
    private final int ADRRESS_ERROR = 7;
    private final int AVATAR_ERROR = 8;

    // 请求码
    private final int REQUEST_CAMERA_PERMISSION = 101;
    private final int REQUEST_IMAGE_CAPTURE = 102;
    private final int REQUEST_PICK_IMAGE = 103;

    private IRegisterCallback registerCallback = new IRegisterCallback() {
        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public void onPersonInfoSaveSuccess() throws RemoteException {
            Log.i(TAG, "onPersonInfoSaveSuccess   ");
            Message msg = new Message();
            msg.what = 1;
            uiHandler.sendMessage(msg);
        }

        @Override
        public void onPersonInfoSaveFailed(int err) throws RemoteException {
            Log.i(TAG, "onPersonInfoSaveFailed: err " + err);
            Utils.Return.NAME_ERROR.ordinal();
            switch (err) {
                case NAME_ERROR:
                    toastOutput("名字不可为空");
                    break;
                case SEX_ERROR:
                    toastOutput("性别不可为空");
                    break;
                case NATION_ERROR:
                    toastOutput("名族不可为空");
                    break;
                case IDCARD_NULL_ERROR:
                    toastOutput("身份证号不可为空");
                    break;
                case IDCARD_INSUFFICIENT_ERROR:
                    toastOutput("身份证号不足18位");
                    break;
                case IDCARD_ERROR:
                    toastOutput("不符合身份证格式");
                    break;
                case ADRRESS_ERROR:
                    toastOutput("地址不可为空");
                    break;
                case AVATAR_ERROR:
                    toastOutput("请设置头像");
                    break;
            }
        }
    };

    public void toastOutput(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

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
        setContentView(R.layout.register_activity);
        mContext = getApplicationContext();
        window = findViewById(R.id.all);
        window.setOnClickListener(onClickListener);
        //init Button
        register = findViewById(R.id.preserve);
        register.setOnClickListener(onClickListener);

        //init radio group
        sex_radio_group = findViewById(R.id.sex_radio_group);
        sex_radio_group.setOnCheckedChangeListener(onCheckedChangeListener);
        radio_button_man = findViewById(R.id.radio_button_man);
        radio_button_woman = findViewById(R.id.radio_button_woman);

        //init edit text
        edit_name = findViewById(R.id.edit_name);
        edit_nation = findViewById(R.id.edit_nation);
        edit_idcard = findViewById(R.id.edit_idcard);
        edit_address = findViewById(R.id.edit_address);

        img_avatar = findViewById(R.id.img_avatar);
        img_avatar.setOnClickListener(onClickListener);

        birth = findViewById(R.id.birth);
        birth.setVisibility(View.GONE);

        // bind service
        Intent intent = new Intent(this, IdCardSystemService.class);
        Log.i(TAG, "ActivityA 执行 bindService");
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private ProgressDialog progressDialog;

    // 申请相机权限
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 显示加载对话框
            progressDialog = ProgressDialog.show(this, "请求权限", "正在请求相机权限，请稍候...", true);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            captureImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // 关闭加载对话框
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "需要相机权限以捕获图像", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 摄像头拍摄头像
    private void captureImage() {
        // 调用系统前置相机拍照
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "没有找到相机应用", Toast.LENGTH_SHORT).show();
        }
    }

    // 从相册选择头像
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                img_avatar.setImageBitmap(imageBitmap);
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                try {
                    Uri imageUri = data.getData();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, 100, 100);

                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    Bitmap imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, options);
                    img_avatar.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // 选择拍照或系统文件管理导入
    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择头像");
        builder.setItems(new CharSequence[]{"拍照", "从相册选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // 拍照
                        requestCameraPermission();
                        break;
                    case 1: // 从相册选择
                        pickImageFromGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    // 检查点击事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.preserve) {
                writeAttributes();
            }
            if (v.getId() == R.id.img_avatar) { // 选择拍照或系统文件管理导入
                showImagePickerDialog();
            }
            if (!register.isEnabled()) {
                finish();
            }

        }
    };

    private void writeAttributes() {
        Log.i(TAG, "writeAttributes edit_name " + edit_name.getText().length());
        personIdCard.setName(edit_name.getText().toString());
        personIdCard.setNation(edit_nation.getText().toString());
        personIdCard.setIdCard(edit_idcard.getText().toString());
        personIdCard.setAddress(edit_address.getText().toString());

        Drawable drawable = img_avatar.getDrawable();
        if (drawable != null) {
            personIdCard.setAvatar(Utils.bitmapToByte(Utils.drawableToBitmap(drawable)));
        } else {
            toastOutput("请设置头像");
            return;
        }

        mService.checkInformation(personIdCard);
    }

    // 性别选择
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.radio_button_man) {
                System.out.println(radio_button_man.getText().toString());
                personIdCard.setSex(radio_button_man.getText().toString());
            } else if (checkedId == R.id.radio_button_woman) {
                System.out.println(radio_button_woman.getText().toString());
                personIdCard.setSex(radio_button_woman.getText().toString());
            }
        }
    };

}
