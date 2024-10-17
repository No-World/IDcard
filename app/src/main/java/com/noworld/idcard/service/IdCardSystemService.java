package com.noworld.idcard.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.Binder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.database.Cursor;

import androidx.annotation.Nullable;

import com.noworld.idcard.aidl.IRegisterCallback;
import com.noworld.idcard.data.PersonIdCard;
import com.noworld.idcard.data.PublicData;
import com.noworld.idcard.database.Sqlite;
import com.noworld.idcard.utils.Utils;

import java.util.ArrayList;

public class IdCardSystemService extends Service {

    private final String TAG = "IdCardSystemService";

    private Sqlite database;
    private SQLiteDatabase db;
    private PublicData p;
    private IRegisterCallback callback;

    public class MyBinder extends Binder {
        public IdCardSystemService getService() {
            return IdCardSystemService.this;
        }
    }

    private MyBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service oncrate");
        p = (PublicData) this.getApplication();
        initDatabase();
    }

    private ArrayList<PersonIdCard> getAllPersonIdCards() {
        ArrayList<PersonIdCard> ls = new ArrayList<>();

        String sql = "select * from idcard_data";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            PersonIdCard person = new PersonIdCard();

            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex >= 0) {
                person.setName(cursor.getString(nameIndex));
            }

            int sexIndex = cursor.getColumnIndex("sex");
            if (sexIndex >= 0) {
                person.setSex(cursor.getString(sexIndex));
            }

            int nationIndex = cursor.getColumnIndex("nation");
            if (nationIndex >= 0) {
                person.setNation(cursor.getString(nationIndex));
            }

            int idCardIndex = cursor.getColumnIndex("idCard");
            if (idCardIndex >= 0) {
                person.setIdCard(cursor.getString(idCardIndex));
            }

            int addressIndex = cursor.getColumnIndex("address");
            if (addressIndex >= 0) {
                person.setAddress(cursor.getString(addressIndex));
            }

            int dateIndex = cursor.getColumnIndex("date");
            if (dateIndex >= 0) {
                person.setDate_of_birth(cursor.getString(dateIndex));
            }

            int avatarIndex = cursor.getColumnIndex("avatar");
            if (avatarIndex >= 0) {
                person.setAvatar(cursor.getBlob(avatarIndex));
            }

            ls.add(person);
        }
        return ls;
    }

    private void initDatabase() {
        db = this.openOrCreateDatabase("idcard_data", MODE_PRIVATE, null);//判断数据库是否存在不存在就创建
        database = new Sqlite(this, "idcard_data", null, 1);

        if (!database.findTable("idcard_data")) {
            String sql = "create table idcard_data(name, sex, nation, idCard, address, date, avatar)";
            db.execSQL(sql);
        } else {
            Log.i(TAG, "has table idcard_data");

            ArrayList<PersonIdCard> ls = getAllPersonIdCards();
            
            if (!ls.isEmpty()) {
                p.setPersonList(ls);
            }
            Log.i(TAG, "" + p.getPersonList().size());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    private void dumpInfo(PersonIdCard personIdCard) {
        Log.i(TAG, "" + personIdCard.getSex());
    }

    public void registerListener(IRegisterCallback callback) {
        this.callback = callback;
    }

    public void unregisterListener() {
        callback = null;
    }

    public int checkInformation(PersonIdCard personIdCard) {
        Log.i(TAG, "checkInformation");
        dumpInfo(personIdCard);
        Utils.Return err = Utils.checkInfo(personIdCard);
        if (err == Utils.Return.CHECK_OK) {
            saveDataToSql(personIdCard);
            try {
                callback.onPersonInfoSaveSuccess();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            try {
                callback.onPersonInfoSaveFailed(err.ordinal());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private synchronized void saveDataToSql(final PersonIdCard personIdCard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put("name", personIdCard.getName());
                cv.put("sex", personIdCard.getSex());
                cv.put("nation", personIdCard.getNation());
                cv.put("idCard", personIdCard.getIdCard());
                cv.put("address", personIdCard.getAddress());
                cv.put("date", personIdCard.getDate_of_birth());
                cv.put("avatar", personIdCard.getAvatar());
                db.insert("idcard_data", null, cv);
                Looper.prepare();
                Looper.loop();
            }
        }).start();
        p.add(personIdCard);
    }

    public void deleteDataFromSql(final PersonIdCard personIdCard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int rowsDeleted = db.delete("idcard_data", "idCard=?", new String[]{personIdCard.getIdCard()});
                updatePersonList();
            }
        }).start();
        p.remove(personIdCard);
    }

    public void updatePersonList() {
        ArrayList<PersonIdCard> updatedList = getAllPersonIdCards();
        p.setPersonList(updatedList);
    }

}
