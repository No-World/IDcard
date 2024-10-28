package com.noworld.idcard.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.noworld.idcard.data.PersonIdCard;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

public class Utils {

    private static final String TAG = "Utils";

    private static String date = null;

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public enum Return {
        CHECK_OK,
        NAME_ERROR,
        SEX_ERROR,
        NATION_ERROR,
        IDCARD_NULL_ERROR,
        IDCARD_INSUFFICIENT_ERROR,
        IDCARD_ERROR,
        ADDRESS_ERROR,
        AVATAR_ERROR
    }


    public static Return checkInfo(PersonIdCard personIdCard) {

        // check name
        Log.i(TAG, "idcard name :" + personIdCard.getName());
        if (personIdCard.getName().equals("")) {
            return Return.NAME_ERROR;
        }

        // check sex
        if (personIdCard.getSex() == null) {
            return Return.SEX_ERROR;
        }

        // check nation
        if (personIdCard.getNation().equals("")) {
            return Return.NATION_ERROR;
        }

        // check id card
        String idCard = personIdCard.getIdCard();
        if (!idCard.equals("")) {
            // 18 bit
            // date no error
            // not have Space
            // All but the last one can't be letters
            Log.i(TAG, "idcard size " + idCard.length());
            if (idCard.length() != 18) {
                return Return.IDCARD_INSUFFICIENT_ERROR;
            } else if (checkSpace(idCard) || checkNumber(idCard) || checkDate(idCard)) {
                return Return.IDCARD_ERROR;
            } // 校验日期是否小于系统日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (date != null && date.compareTo(sdf.format(System.currentTimeMillis())) > 0) {
                return Return.IDCARD_ERROR;
            }
            personIdCard.setDate_of_birth(date);
            date = null;
        } else {
            return Return.IDCARD_NULL_ERROR;
        }

        // check address
        if (personIdCard.getAddress().equals("")) {
            return Return.ADDRESS_ERROR;
        }

        // check avatar
        if (personIdCard.getAvatar() == null) {
            return Return.AVATAR_ERROR;
        }

        return Return.CHECK_OK;
    }

    private static boolean checkNumber(String idCard) {
        if (idCard == null) return false;
        try {
            String re = idCard.substring(0, 17);
            Log.i(TAG, "re: " + re + "  " + idCard.indexOf("x"));

            if (idCard.indexOf("x") == 17 && idCard.indexOf("x") != -1) {
                try {
                    Long i = Long.valueOf(re);
                    Log.i(TAG, "i: " + i);
                } catch (Exception ex) {
                    Log.e(TAG, "last is 'x', but others have letters");
                    return true;
                }
            } else {
                try {
                    Long s = Long.valueOf(idCard);
                    Log.i(TAG, "s: " + s);
                } catch (Exception ex) {
                    Log.e(TAG, "Except for the last one, idCard have other letters");
                    return true;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "last is 'x', but others have letters");
            return true;
        }
        return false;
    }

    private static boolean checkDate(String idCard) {
        String year = idCard.substring(6, 10);
        String mon = idCard.substring(10, 12);
        String day = idCard.substring(12, 14);
        Log.i(TAG, "year: " + year + "mon: " + mon + "day: " + day);
        date = year + "-" + mon + "-" + day;
        if (Integer.parseInt(year) > 1900) {
            switch (Integer.parseInt(mon)) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    if (Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 31) {
                        return false;
                    }
                    break;
                case 2:
                    boolean isLeapyear = (Integer.parseInt(year) % 4) > 0 ? false : true;
                    if (isLeapyear) {
                        if (Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 29) {
                            return false;
                        }
                    } else {
                        if (Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 28) {
                            return false;
                        }
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    if (Integer.parseInt(day) >= 1 && Integer.parseInt(day) <= 30) {
                        return false;
                    }
                    break;
                default:
                    return true;
            }
        }
        return true;
    }

    private static boolean checkSpace(final String idCard) {
        String[] strCodes = idCard.trim().split(" ");
        return strCodes.length > 1;
    }

    public static boolean isInArray(String value, String[] array) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
