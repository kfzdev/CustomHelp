package com.customapi.fmkpl.okhttp;


import com.customapi.fmkpl.bean.UserInform;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static UserInfo instance = new UserInfo();

    private String username;
    private String mjwt = "";
    private UserInform mUserInform = new UserInform();
    private int networkState = 0;
    private boolean login = false;
    private UserInfoListener mUserInfoListener;

    public UserInfo() {

    }
    public static UserInfo getInstance() {
        return instance;
    }

    public UserInform getUserInform() {
        return mUserInform;
    }

    public void setUserInform(UserInform userInform) {
        mUserInform = userInform;
    }

    public void setListener(UserInfoListener listener){
        this.mUserInfoListener = listener;
    }

    public String getJwt() {
        return mjwt;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
