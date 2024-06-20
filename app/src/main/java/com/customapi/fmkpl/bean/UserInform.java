/**
 * Copyright 2023 bejson.com
 */
package com.customapi.fmkpl.bean;

public class UserInform {

    private int user_id;
    private String user_token;
    private Data data;
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public int getUser_id() {
        return user_id;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }
    public String getUser_token() {
        return user_token;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

}