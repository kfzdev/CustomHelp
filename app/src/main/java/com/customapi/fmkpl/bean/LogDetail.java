package com.customapi.fmkpl.bean;

public class LogDetail {
    private String datetime;
    private int no;
    private String text;
    public LogDetail(int no,String datetime,String text){
        this.no = no;
        this.datetime = datetime;
        this.text = text;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
