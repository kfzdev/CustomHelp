package com.customapi.fmkpl.bean;

public class WorkConfig {
    private boolean sys_log_state;
    private String eqd_url = "";
    private String order_types = "";
    private int fresh_time = 1000;
    private int grab_time = 0;
    private int fresh_type = 0;

    private boolean type_one_state;
    private boolean type_two_state;
    private boolean type_three_state;
    private boolean type_four_state;
    private boolean type_five_state;
    private float max_distance_one = 0;
    private float max_distance_two = 0;
    private float max_distance_three = 0;
    private float max_distance_four = 0;
    private float max_distance_five = 0;
    private float min_money_one = 0;
    private float min_money_two = 0;
    private float min_money_three = 0;
    private float min_money_four = 0;
    private float min_money_five = 0;
    private String from_place_one = "";
    private String from_place_two = "";
    private String from_place_three = "";
    private String from_place_four = "";
    private String from_place_five = "";
    private String to_place_one = "";
    private String to_place_two = "";
    private String to_place_three = "";
    private String to_place_four = "";
    private String to_place_five = "";
    private int time_minute_one = 0;
    private int time_minute_two = 0;
    private int time_minute_three = 0;
    private int time_minute_four = 0;
    private int time_minute_five = 0;

    private float danjia_one = 0;
    private float danjia_two = 0;
    private float danjia_three = 0;
    private float danjia_four = 0;
    private float danjia_five = 0;
    private double myLat = 0;
    private double myLng = 0;
    private String start_time = "";
    private String end_time = "";
    private long startTimeMillis=0L;
    private long endTimeMillis=0L;

    public String getOrder_types() {
        return order_types;
    }

    public void setOrder_types(String order_types) {
        this.order_types = order_types;
    }

    public String getEqd_url() {
        return eqd_url;
    }

    public void setEqd_url(String eqd_url) {
        this.eqd_url = eqd_url;
    }

    public int getFresh_time() {
        return fresh_time;
    }

    public void setFresh_time(int fresh_time) {
        this.fresh_time = fresh_time;
    }

    public boolean isType_one_state() {
        return type_one_state;
    }

    public void setType_one_state(boolean type_one_state) {
        this.type_one_state = type_one_state;
    }

    public boolean isType_two_state() {
        return type_two_state;
    }

    public void setType_two_state(boolean type_two_state) {
        this.type_two_state = type_two_state;
    }

    public boolean isType_three_state() {
        return type_three_state;
    }

    public void setType_three_state(boolean type_three_state) {
        this.type_three_state = type_three_state;
    }

    public boolean isType_four_state() {
        return type_four_state;
    }

    public void setType_four_state(boolean type_four_state) {
        this.type_four_state = type_four_state;
    }

    public boolean isType_five_state() {
        return type_five_state;
    }

    public void setType_five_state(boolean type_five_state) {
        this.type_five_state = type_five_state;
    }

    public float getMax_distance_one() {
        return max_distance_one;
    }

    public void setMax_distance_one(float max_distance_one) {
        this.max_distance_one = max_distance_one;
    }

    public float getMax_distance_two() {
        return max_distance_two;
    }

    public void setMax_distance_two(float max_distance_two) {
        this.max_distance_two = max_distance_two;
    }

    public float getMax_distance_three() {
        return max_distance_three;
    }

    public void setMax_distance_three(float max_distance_three) {
        this.max_distance_three = max_distance_three;
    }

    public float getMax_distance_four() {
        return max_distance_four;
    }

    public void setMax_distance_four(float max_distance_four) {
        this.max_distance_four = max_distance_four;
    }

    public float getMax_distance_five() {
        return max_distance_five;
    }

    public void setMax_distance_five(float max_distance_five) {
        this.max_distance_five = max_distance_five;
    }

    public float getMin_money_one() {
        return min_money_one;
    }

    public void setMin_money_one(float min_money_one) {
        this.min_money_one = min_money_one;
    }

    public float getMin_money_two() {
        return min_money_two;
    }

    public void setMin_money_two(float min_money_two) {
        this.min_money_two = min_money_two;
    }

    public float getMin_money_three() {
        return min_money_three;
    }

    public void setMin_money_three(float min_money_three) {
        this.min_money_three = min_money_three;
    }

    public float getMin_money_four() {
        return min_money_four;
    }

    public void setMin_money_four(float min_money_four) {
        this.min_money_four = min_money_four;
    }

    public float getMin_money_five() {
        return min_money_five;
    }

    public void setMin_money_five(float min_money_five) {
        this.min_money_five = min_money_five;
    }

    public String getFrom_place_one() {
        return from_place_one;
    }

    public void setFrom_place_one(String from_place_one) {
        this.from_place_one = from_place_one;
    }

    public String getFrom_place_two() {
        return from_place_two;
    }

    public void setFrom_place_two(String from_place_two) {
        this.from_place_two = from_place_two;
    }

    public String getFrom_place_three() {
        return from_place_three;
    }

    public void setFrom_place_three(String from_place_three) {
        this.from_place_three = from_place_three;
    }

    public String getFrom_place_four() {
        return from_place_four;
    }

    public void setFrom_place_four(String from_place_four) {
        this.from_place_four = from_place_four;
    }

    public String getFrom_place_five() {
        return from_place_five;
    }

    public void setFrom_place_five(String from_place_five) {
        this.from_place_five = from_place_five;
    }

    public String getTo_place_one() {
        return to_place_one;
    }

    public void setTo_place_one(String to_place_one) {
        this.to_place_one = to_place_one;
    }

    public String getTo_place_two() {
        return to_place_two;
    }

    public void setTo_place_two(String to_place_two) {
        this.to_place_two = to_place_two;
    }

    public String getTo_place_three() {
        return to_place_three;
    }

    public void setTo_place_three(String to_place_three) {
        this.to_place_three = to_place_three;
    }

    public String getTo_place_four() {
        return to_place_four;
    }

    public void setTo_place_four(String to_place_four) {
        this.to_place_four = to_place_four;
    }

    public String getTo_place_five() {
        return to_place_five;
    }

    public void setTo_place_five(String to_place_five) {
        this.to_place_five = to_place_five;
    }

    public int getTime_minute_one() {
        return time_minute_one;
    }

    public void setTime_minute_one(int time_minute_one) {
        this.time_minute_one = time_minute_one;
    }

    public int getTime_minute_two() {
        return time_minute_two;
    }

    public void setTime_minute_two(int time_minute_two) {
        this.time_minute_two = time_minute_two;
    }

    public int getTime_minute_three() {
        return time_minute_three;
    }

    public void setTime_minute_three(int time_minute_three) {
        this.time_minute_three = time_minute_three;
    }

    public int getTime_minute_four() {
        return time_minute_four;
    }

    public void setTime_minute_four(int time_minute_four) {
        this.time_minute_four = time_minute_four;
    }

    public int getTime_minute_five() {
        return time_minute_five;
    }

    public void setTime_minute_five(int time_minute_five) {
        this.time_minute_five = time_minute_five;
    }

    public int getFresh_type() {
        return fresh_type;
    }

    public void setFresh_type(int fresh_type) {
        this.fresh_type = fresh_type;
    }

    public boolean isSys_log_state() {
        return sys_log_state;
    }

    public void setSys_log_state(boolean sys_log_state) {
        this.sys_log_state = sys_log_state;
    }

    public float getDanjia_one() {
        return danjia_one;
    }

    public void setDanjia_one(float danjia_one) {
        this.danjia_one = danjia_one;
    }

    public float getDanjia_two() {
        return danjia_two;
    }

    public void setDanjia_two(float danjia_two) {
        this.danjia_two = danjia_two;
    }

    public float getDanjia_three() {
        return danjia_three;
    }

    public void setDanjia_three(float danjia_three) {
        this.danjia_three = danjia_three;
    }

    public float getDanjia_four() {
        return danjia_four;
    }

    public void setDanjia_four(float danjia_four) {
        this.danjia_four = danjia_four;
    }

    public float getDanjia_five() {
        return danjia_five;
    }

    public void setDanjia_five(float danjia_five) {
        this.danjia_five = danjia_five;
    }

    public double getMyLat() {
        return myLat;
    }

    public void setMyLat(double myLat) {
        this.myLat = myLat;
    }

    public double getMyLng() {
        return myLng;
    }

    public void setMyLng(double myLng) {
        this.myLng = myLng;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public int getGrab_time() {
        return grab_time;
    }

    public void setGrab_time(int grab_time) {
        this.grab_time = grab_time;
    }
}
