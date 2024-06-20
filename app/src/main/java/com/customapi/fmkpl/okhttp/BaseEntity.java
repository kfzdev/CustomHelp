package com.customapi.fmkpl.okhttp;

public class BaseEntity<T> {

    private String msg;
    private int code;
    private T data;

    public BaseEntity() {
    }

    public BaseEntity(String message) {
        this.msg = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
