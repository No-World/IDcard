// IRegisterCallback.aidl
package com.noworld.idcard.aidl;

// Declare any non-default types here with import statements

interface IRegisterCallback {
    void onPersonInfoSaveSuccess();
    void onPersonInfoSaveFailed(int err);
}
