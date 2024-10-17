package com.noworld.idcard.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Sqlite extends SQLiteOpenHelper {


    public Sqlite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean findTable(String table) {

        boolean result = false;
        if (table.equals("null"))
            return result;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String sql = "select name from sqlite_master where name ='" + table.trim() + "' and type='table'";
            cursor = db.rawQuery(sql, null);
            if (cursor.getCount() != 0) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
        }
        return result;
    }

    // 转换图片为二进制数据
    public byte[] imageToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
