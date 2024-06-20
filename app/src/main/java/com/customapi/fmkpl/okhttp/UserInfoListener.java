package com.customapi.fmkpl.okhttp;


public interface UserInfoListener {
    void onChange(UserSetting value);
    void onState(int value);
}
