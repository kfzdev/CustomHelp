/**
 * Copyright 2023 bejson.com
 */
package com.customapi.fmkpl.bean;

import java.util.List;

public class OdersResopnse {

    private int code;
    private String message;
    private String data;
    private List<OrderDetail> list;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setData(String data) {
        this.data = data;
    }
    public String getData() {
        return data;
    }

    public void setList(List<OrderDetail> list) {
        this.list = list;
    }
    public List<OrderDetail> getList() {
        return list;
    }

}