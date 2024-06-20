/**
 * Copyright 2023 bejson.com
 */
package com.customapi.fmkpl.bean;

public class Data {

    private UserData userInfo;
    private SurplusVipTime surplusVipTime;
    private String vipExpireTimeStr;
    public void setUserInfo(UserData userInfo) {
        this.userInfo = userInfo;
    }
    public UserData getUserInfo() {
        return userInfo;
    }

    public void setSurplusVipTime(SurplusVipTime surplusVipTime) {
        this.surplusVipTime = surplusVipTime;
    }
    public SurplusVipTime getSurplusVipTime() {
        return surplusVipTime;
    }

    public void setVipExpireTimeStr(String vipExpireTimeStr) {
        this.vipExpireTimeStr = vipExpireTimeStr;
    }
    public String getVipExpireTimeStr() {
        return vipExpireTimeStr;
    }

}