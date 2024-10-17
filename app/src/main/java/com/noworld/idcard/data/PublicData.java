package com.noworld.idcard.data;

import android.app.Application;

import java.util.ArrayList;

public class PublicData extends Application {

    private ArrayList<PersonIdCard> personList = new ArrayList<>();

    public void setPersonList(ArrayList<PersonIdCard> personList) {
        this.personList = personList;
    }

    public ArrayList<PersonIdCard> getPersonList() {
        return personList;
    }

    // 添加身份证信息
    public void add(PersonIdCard personIdCard) {
        personList.add(personIdCard);
    }

    // 删除身份证信息
    public void remove(PersonIdCard personIdCard) {
        personList.remove(personIdCard);
    }
}
