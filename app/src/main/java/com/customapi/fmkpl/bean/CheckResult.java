package com.customapi.fmkpl.bean;

public class CheckResult {
    private String msg;
    private Boolean state;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = "("+msg+")";
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}
