package com.noworld.idcard.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Blob;

public class PersonIdCard implements Parcelable {


    private String name;
    private String sex;
    private String nation;
    private String idCard;
    private String address;
    private String date_of_birth;
    private byte[] avatar;

    public PersonIdCard() {
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getNation() {
        return nation;
    }

    public String getIdCard() {
        return idCard;
    }

    public String getAddress() {
        return address;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    protected PersonIdCard(Parcel in) {
        name = in.readString();
        sex = in.readString();
        nation = in.readString();
        idCard = in.readString();
        address = in.readString();
        date_of_birth = in.readString();
        avatar = in.createByteArray();
    }

    public static final Creator<PersonIdCard> CREATOR = new Creator<PersonIdCard>() {
        @Override
        public PersonIdCard createFromParcel(Parcel in) {
            return new PersonIdCard(in);
        }

        @Override
        public PersonIdCard[] newArray(int size) {
            return new PersonIdCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(sex);
        dest.writeString(nation);
        dest.writeString(idCard);
        dest.writeString(address);
        dest.writeString(date_of_birth);
        dest.writeByteArray(avatar);
    }

    // check information.

}
