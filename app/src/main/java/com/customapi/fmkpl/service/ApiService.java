package com.customapi.fmkpl.service;

import static com.customapi.fmkpl.MainActivity.LOCAL_CONFIG_KEY;

import android.app.Service;
import com.customapi.fmkpl.BuildConfig;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.customapi.fmkpl.MainActivity;
import com.customapi.fmkpl.R;
import com.customapi.fmkpl.bean.CheckResult;
import com.customapi.fmkpl.bean.OdersResopnse;
import com.customapi.fmkpl.bean.OrderDetail;
import com.customapi.fmkpl.bean.WorkConfig;
import com.customapi.fmkpl.okhttp.BaseEntity;
import com.customapi.fmkpl.okhttp.OkHttpCallback;
import com.customapi.fmkpl.okhttp.OkHttpClientManager;
import com.customapi.fmkpl.tool.MyTool;
import com.customapi.fmkpl.tool.TinyDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Headers;

public class ApiService extends Service {
    public static final String TAG = "AutoService";
    public static final String COM_BROADCAST_NOTIFICE = BuildConfig.APPLICATION_ID + ".broadcast.notificecc";
    public static final String BROADCAST_MSG_KEY = "autoservice_message";
    public static final String START_AUTO_WORK = "start_auto_work";
    public static final String CLOSE_AUTO_WORK = "close_auto_work";

    public final int UPDATE_FLOAT_VIEW_HANDEL_TAG = 0;
    public final int UPDATE_FLOAT_BUTTON_HANDEL_PAUSE_TAG = 2;
    public final int SUCCESS_GRAB_ORDER_HANDEL_PAUSE_TAG = 3;

    private FloatButtonWindowService mFloatButtonWindowService;
    public TinyDB mTinydb;
    private boolean main_work_state;
    private boolean grabing;
    private WorkConfig mWorkConfig;
    private Gson mGson;
    private List<String> orderTypes = new ArrayList<>();
    private List<String> fromWordsone = new ArrayList<>();
    private List<String> toWordsone = new ArrayList<>();

    private List<String> fromWordstwo = new ArrayList<>();
    private List<String> toWordstwo = new ArrayList<>();
    private List<String> fromWordsthree = new ArrayList<>();
    private List<String> toWordsthree = new ArrayList<>();
    private List<String> fromWordsfour = new ArrayList<>();
    private List<String> toWordsfour = new ArrayList<>();
    private List<String> fromWordsfive = new ArrayList<>();
    private List<String> toWordsfive = new ArrayList<>();
    private LinkedHashMap<String, String> mOrderlistParam;
    private Timer networkTimer;
    private final String grab_url = "/grabOrder";
    private double nowLat = 0;
    private double nowLng = 0;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private List<String> wuxiaoOrder = new ArrayList<>();
    private MainActivity mainActivityUi;
    private MediaPlayer mediaPlayer;
    private int last_order_count = -1;
    public ApiService() {
    }
    public class ApiServiceBinder extends Binder {
        public ApiService getService(){
            return ApiService.this;
        }
    }
    public void setMainActivityUi(MainActivity ui) {
        if (mainActivityUi == null) {
            mainActivityUi = ui;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopNetwork();
        try {
            mLocationClient.stop();
            stopIncoming();
        } catch (Exception e) {

        }
        close_float();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LocationClient.setAgreePrivacy(true);
        mGson = new Gson();
        mTinydb = new TinyDB(this);
        init_gps();
    }
    public void playIncoming() {
        mediaPlayer = MediaPlayer.create(this, R.raw.order_ok);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopIncoming();
            }
        }, 60000);
    }
    public void stopIncoming() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void init_gps() {
        try {
            mLocationClient = new LocationClient(getApplicationContext());
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    int cc_code = bdLocation.getLocType();
                    if (cc_code == 161 || cc_code == 61 || cc_code == 66) {
                        nowLat = bdLocation.getLatitude();
                        nowLng = bdLocation.getLongitude();
                        if(bdLocation.getLatitude()!=nowLat){
                            logDebug("("+nowLat+","+nowLng+")");
                        }

                    }

                    Log.d(TAG, nowLat + "," + nowLng);
                }
            });
        } catch (Exception e) {
        }
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(60000);
        option.setOpenGnss(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(true);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGnss(false);
        option.setNeedNewVersionRgc(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

    }
    public void updateMainState(boolean state) {
        main_work_state = state;
    }
    public boolean isWorking() {
        return main_work_state;
    }
    public void break_work_from_float() {
        main_work_state = false;
        close_float();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new ApiServiceBinder();
    }
    public void openButtonWindow() {
        if (mFloatButtonWindowService == null) {
            Intent intent = new Intent(this, FloatButtonWindowService.class);
            bindService(intent, mFloatButtonWindowConnection, Context.BIND_AUTO_CREATE);
        } else {
            mFloatButtonWindowService.init_service_a(ApiService.this);
        }
        StringBuilder dw_str = new StringBuilder("档位");
        if(mWorkConfig.isType_one_state()){
            dw_str.append("一");
        }
        if(mWorkConfig.isType_two_state()){
            dw_str.append("二");
        }
        if(mWorkConfig.isType_three_state()){
            dw_str.append("三");
        }
        if(mWorkConfig.isType_four_state()){
            dw_str.append("四");
        }
        if(mWorkConfig.isType_five_state()){
            dw_str.append("五");
        }
        dw_str.append("已开启");
        logDebug(dw_str.toString());
    }
    private void logDebug(String msg) {
        Log.d(TAG, msg);
        if (mainActivityUi != null) {
            mainActivityUi.updateLog(msg);
        }
    }
    private void close_float() {
        try {
            unbindService(mFloatButtonWindowConnection);
        } catch (Exception ignored) {
        }
        mFloatButtonWindowService = null;
    }

    private final ServiceConnection mFloatButtonWindowConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            mFloatButtonWindowService = ((FloatButtonWindowService.FloatButtonWindowServiceBinder) service).getService();
            if (!mFloatButtonWindowService.get_init_state()) {
                close_float();
            } else {
                mFloatButtonWindowService.init_service_a(ApiService.this);
            }

        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i("mFloatButtonConnection", "服务关闭了");
        }
    };
    private void stopNetwork() {
        if (networkTimer != null) {
            networkTimer.cancel();
        }
    }
    private void startNetwork(long period) {
        stopNetwork();
        TimerTask task = new TimerTask() {
            public void run() {
                if (main_work_state) {
                    get_order_list();
                }
            }
        };
        networkTimer = new Timer();
        networkTimer.schedule(task, 0, period);
    }
    private void get_order_list() {
        if(grabing){
            return;
        }
        long noew_m = new Date().getTime();
        LinkedHashMap<String, String> new_param = new LinkedHashMap<>();

        StringBuilder urlParams = new StringBuilder("/bookingOrderList");
        OkHttpClientManager.getInstance().getAsyn(urlParams.toString(), new OkHttpCallback<String>() {
            @Override
            public void onSuccess(String response, Headers headers) {
                if (main_work_state) {
                    OdersResopnse odersResopnse = mGson.fromJson(response, OdersResopnse.class);
                    if (odersResopnse != null) {
                        if (odersResopnse.getCode() == 0 && odersResopnse.getMessage().equals("成功")) {
                            List<OrderDetail> orderDetail = odersResopnse.getList();
                            grab_order(orderDetail);
                        } else {
                            logDebug(odersResopnse.getMessage());
                        }
                    }
                }

            }

            @Override
            public void onFailure(BaseEntity<String> entity, String message, int responseCode) {
                Log.d(TAG, message);
            }
        }, noew_m);

    }

    private void success_grab() {
        mHandler.sendEmptyMessage(SUCCESS_GRAB_ORDER_HANDEL_PAUSE_TAG);
    }

    private void sleepTime(){
        try {
            Thread.sleep(mWorkConfig.getGrab_time());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void post_order(OrderDetail orderDetail) {
        grabing = true;
        LinkedHashMap<String, String> new_post_param = new LinkedHashMap<>();;

        sleepTime();
        OkHttpClientManager.getInstance().postJsonAsyn(grab_url, new_post_param, new OkHttpCallback<String>(){
            @Override
            public void onSuccess(String response, Headers headers) {
                grabing = false;
                StringBuilder orderInform = new StringBuilder(getOrderString(orderDetail));
                try{
                    if(response.contains("抢单成功")){

                        orderInform.append("抢单成功");
                        logDebug(orderInform.toString());
                        success_grab();
                    }else{
                        orderInform.append("抢单失败");
                        orderInform.append(response);
                        logDebug(orderInform.toString());
                    }
                }catch (Exception e){
                    orderInform.append(response);
                    logDebug("系统错误");
                }

            }

            @Override
            public void onFailure(BaseEntity<String> entity, String message, int responseCode) {
                grabing = false;
            }
        });
    }

    private void grab_order(List<OrderDetail> orderDetails) {
        if (orderDetails.size() == 0&&last_order_count!=0) {
            logDebug("当前订单数为0");
            last_order_count = 0;
            return;
        }
        for (int i = 0; i < orderDetails.size(); i++) {
            if (wuxiaoOrder.contains(orderDetails.get(i).getBookingId())) {
                continue;
            }
            int check_result = checkorder(orderDetails.get(i));
            if (check_result>0) {
                logDebug(check_result+"档符合要求，正在抢单......");
                post_order(orderDetails.get(i));
                break;
            } else {
                wuxiaoOrder.add(orderDetails.get(i).getBookingId());
            }
        }
        if(last_order_count!=orderDetails.size()){
            logDebug("当前订单总数："+orderDetails.size()+"--订单刷新中......");
        }
        last_order_count = orderDetails.size();

    }
    private String getOrderString(OrderDetail orderDetail){
        StringBuilder orderInform = new StringBuilder("订单信息：")
                .append(orderDetail.getSourceDesc() + "-----")
                .append("距起点：" + orderDetail.getStartAddress() + "，")
                .append(orderDetail.getDistance() + "公里 ")
                .append("终点：" + orderDetail.getEndAddress() + " ")
                .append("时间：")
                .append(orderDetail.getBookingDate() + " ")
                .append("收入：" + orderDetail.getMoney() + " ");
        return orderInform.toString();
    }
    private int checkorder(OrderDetail orderDetail) {
        if (!orderTypes.contains(orderDetail.getSourceDesc())) {
            logDebug(getOrderString(orderDetail)+"订单类型不符合:" + orderDetail.getSourceDesc() + "\n***************");
            return 0;
        }
        StringBuilder orderInform = new StringBuilder(getOrderString(orderDetail));
        float distance = MyTool.getFloatValue(orderDetail.getDistance());
        if (orderDetail.getDistance().contains("公里")) {
            distance = distance * 1000;
        }
        if (mWorkConfig.isType_one_state()) {
            CheckResult checkResultOne = check_type_one(orderDetail, distance);
            if (checkResultOne.getState()) {
                return 1;
            }else{
                orderInform.append(checkResultOne.getMsg());
            }
        }
        if (mWorkConfig.isType_two_state()) {
            CheckResult checkResultTwo = check_type_two(orderDetail, distance);
            if (checkResultTwo.getState()) {
                return 2;
            }else{
                orderInform.append(checkResultTwo.getMsg());
            }
        }
        if (mWorkConfig.isType_three_state()) {
            CheckResult checkResultThree = check_type_three(orderDetail, distance);
            if (checkResultThree.getState()) {
                return 3;
            }else{
                orderInform.append(checkResultThree.getMsg());
            }
        }
        if (mWorkConfig.isType_four_state()) {
            CheckResult checkResultFour = check_type_four(orderDetail, distance);
            if (checkResultFour.getState()) {
                return 4;
            }else{
                orderInform.append(checkResultFour.getMsg());
            }
        }
        if (mWorkConfig.isType_five_state()) {
            CheckResult checkResultFive = check_type_five(orderDetail);
            if (checkResultFive.getState()) {
                return 5;
            }else{
                orderInform.append(checkResultFive.getMsg());
            }
        }
        logDebug(orderInform.toString());
        return 0;
    }

    private CheckResult check_type_one(OrderDetail orderDetail, float distance) {
        CheckResult checkResult = new CheckResult();
        checkResult.setState(false);
        if (distance < (mWorkConfig.getMax_distance_one() * 1000)) {
            if (orderDetail.getMoney() >= mWorkConfig.getMin_money_one()) {
                float gl = (float) MyTool.calculateDistance(orderDetail.getStartLat(), orderDetail.getStartLng(), orderDetail.getEndLat(), orderDetail.getEndLng());
                if (orderDetail.getMoney() / gl > mWorkConfig.getDanjia_one()) {
                    long differenceTime = orderDetail.getBookingTime() - new Date().getTime();
                    if (differenceTime < (mWorkConfig.getTime_minute_one() * 60000L)) {
                        boolean from_result = true;
                        for (String word : fromWordsone) {
                            if (orderDetail.getStartAddress().contains(word)) {
                                from_result = false;
                                break;
                            }
                        }
                        if (from_result) {
                            boolean to_result = true;
                            for (String word : toWordsone) {
                                if (orderDetail.getEndAddress().contains(word)) {
                                    to_result = false;
                                    break;
                                }
                            }
                            if (to_result) {
                                checkResult.setState(true);
                                return checkResult;
                            }

                        }

                    } else {
                        checkResult.setMsg("一档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_one());
//                        logDebug(getOrderString(orderDetail)+"一档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_one() + "\n***************");
                    }
                } else {
                    checkResult.setMsg("一档单价不符合:" + (orderDetail.getMoney() / gl));
//                    logDebug(getOrderString(orderDetail)+"一档单价不符合:" + (orderDetail.getMoney() / gl) + " | 设置值:" + mWorkConfig.getDanjia_one() + "\n***************");
                }


            } else {
                checkResult.setMsg("一档金额太小");
//                logDebug(getOrderString(orderDetail)+"一档金额不符合:" + orderDetail.getMoney() + " | 设置值:" + mWorkConfig.getMin_money_one() + "\n***************");
            }
        } else {
            checkResult.setMsg("一档距离太远");
//            logDebug(getOrderString(orderDetail)+"一档距离不符合:" + distance / 1000 + " | 设置值:" + mWorkConfig.getMax_distance_one() + "\n***************");
        }
        return checkResult;
    }

    private CheckResult check_type_two(OrderDetail orderDetail, float distance) {
        CheckResult checkResult = new CheckResult();
        checkResult.setState(false);
        if (distance < (mWorkConfig.getMax_distance_two() * 1000)) {
            if (orderDetail.getMoney() >= mWorkConfig.getMin_money_two()) {
                float gl = (float) MyTool.calculateDistance(orderDetail.getStartLat(), orderDetail.getStartLng(), orderDetail.getEndLat(), orderDetail.getEndLng());
                if (orderDetail.getMoney() / gl > mWorkConfig.getDanjia_two()) {
                    long differenceTime = orderDetail.getBookingTime() - new Date().getTime();
                    if (differenceTime < (mWorkConfig.getTime_minute_two() * 60000L)) {
                        boolean from_result = true;
                        for (String word : fromWordstwo) {
                            if (orderDetail.getStartAddress().contains(word)) {
                                from_result = false;
                                break;
                            }
                        }
                        if (from_result) {
                            boolean to_result = true;
                            for (String word : toWordstwo) {
                                if (orderDetail.getEndAddress().contains(word)) {
                                    to_result = false;
                                    break;
                                }
                            }
                            if (to_result) {
                                checkResult.setState(true);
                                return checkResult;
                            }

                        }

                    } else {
                        checkResult.setMsg("二档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_two());
//                        logDebug(getOrderString(orderDetail)+"二档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_two() + "\n***************");
                    }
                } else {
                    checkResult.setMsg("二档单价不符合:" + (orderDetail.getMoney() / gl));
//                    logDebug(getOrderString(orderDetail)+"二档单价不符合:" + (orderDetail.getMoney() / gl) + " | 设置值:" + mWorkConfig.getDanjia_two() + "\n***************");
                }


            } else {
                checkResult.setMsg("二档金额太小");
//                logDebug(getOrderString(orderDetail)+"二档金额不符合:" + orderDetail.getMoney() + " | 设置值:" + mWorkConfig.getMin_money_two() + "\n***************");
            }
        } else {
            checkResult.setMsg("二档距离太远");
//            logDebug(getOrderString(orderDetail)+"二档距离不符合:" + distance / 1000 + " | 设置值:" + mWorkConfig.getMax_distance_two() + "\n***************");
        }
        return checkResult;
    }

    private CheckResult check_type_three(OrderDetail orderDetail, float distance) {
        CheckResult checkResult = new CheckResult();
        checkResult.setState(false);
        if (distance < (mWorkConfig.getMax_distance_three() * 1000)) {
            if (orderDetail.getMoney() >= mWorkConfig.getMin_money_three()) {
                float gl = (float) MyTool.calculateDistance(orderDetail.getStartLat(), orderDetail.getStartLng(), orderDetail.getEndLat(), orderDetail.getEndLng());
                if (orderDetail.getMoney() / gl > mWorkConfig.getDanjia_three()) {
                    long differenceTime = orderDetail.getBookingTime() - new Date().getTime();
                    if (differenceTime < (mWorkConfig.getTime_minute_three() * 60000L)) {
                        boolean from_result = true;
                        for (String word : fromWordsthree) {
                            if (orderDetail.getStartAddress().contains(word)) {
                                from_result = false;
                                break;
                            }
                        }
                        if (from_result) {
                            boolean to_result = true;
                            for (String word : toWordsthree) {
                                if (orderDetail.getEndAddress().contains(word)) {
                                    to_result = false;
                                    break;
                                }
                            }
                            if (to_result) {
                                checkResult.setState(true);
                                return checkResult;
                            }

                        }

                    } else {
                        checkResult.setMsg("三档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_three());
//                        logDebug(getOrderString(orderDetail)+"三档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_three() + "\n***************");
                    }
                } else {
                    checkResult.setMsg("三档单价不符合:" + (orderDetail.getMoney() / gl));
//                    logDebug(getOrderString(orderDetail)+"三档单价不符合:" + (orderDetail.getMoney() / gl) + " | 设置值:" + mWorkConfig.getDanjia_three() + "\n***************");
                }


            } else {
                checkResult.setMsg("三档金额太小");
//                logDebug(getOrderString(orderDetail)+"三档金额不符合:" + orderDetail.getMoney() + " | 设置值:" + mWorkConfig.getMin_money_three() + "\n***************");
            }
        } else {
            checkResult.setMsg("三档距离太远");
//            logDebug(getOrderString(orderDetail)+"三档距离不符合:" + distance / 1000 + " | 设置值:" + mWorkConfig.getMax_distance_three() + "\n***************");
        }
        return checkResult;
    }

    private CheckResult check_type_four(OrderDetail orderDetail, float distance) {
        CheckResult checkResult = new CheckResult();
        checkResult.setState(false);
        if (distance < (mWorkConfig.getMax_distance_four() * 1000)) {
            if (orderDetail.getMoney() >= mWorkConfig.getMin_money_four()) {
                float gl = (float) MyTool.calculateDistance(orderDetail.getStartLat(), orderDetail.getStartLng(), orderDetail.getEndLat(), orderDetail.getEndLng());
                if (orderDetail.getMoney() / gl > mWorkConfig.getDanjia_four()) {
                    long differenceTime = orderDetail.getBookingTime() - new Date().getTime();
                    if (differenceTime < (mWorkConfig.getTime_minute_four() * 60000L)) {
                        boolean from_result = true;
                        for (String word : fromWordsfour) {
                            if (orderDetail.getStartAddress().contains(word)) {
                                from_result = false;
                                break;
                            }
                        }
                        if (from_result) {
                            boolean to_result = true;
                            for (String word : toWordsfour) {
                                if (orderDetail.getEndAddress().contains(word)) {
                                    to_result = false;
                                    break;
                                }
                            }
                            if (to_result) {
                                checkResult.setState(true);
                                return checkResult;
                            }

                        }

                    } else {
                        checkResult.setMsg("四档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_four());
//                        logDebug(getOrderString(orderDetail)+"四档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_four() + "\n***************");
                    }
                } else {
                    checkResult.setMsg("四档单价不符合:" + (orderDetail.getMoney() / gl));
//                    logDebug(getOrderString(orderDetail)+"四档单价不符合:" + (orderDetail.getMoney() / gl) + " | 设置值:" + mWorkConfig.getDanjia_four() + "\n***************");
                }


            } else {
                checkResult.setMsg("四档金额太小");
//                logDebug(getOrderString(orderDetail)+"四档金额不符合:" + orderDetail.getMoney() + " | 设置值:" + mWorkConfig.getMin_money_four() + "\n***************");
            }
        } else {
            checkResult.setMsg("四档距离太远");
//            logDebug(getOrderString(orderDetail)+"四档距离不符合:" + distance / 1000 + " | 设置值:" + mWorkConfig.getMax_distance_four() + "\n***************");
        }
        return checkResult;
    }

    private CheckResult check_type_five(OrderDetail orderDetail) {
        CheckResult checkResult = new CheckResult();
        checkResult.setState(false);
        double distance_gl = MyTool.calculateDistance(mWorkConfig.getMyLat(), mWorkConfig.getMyLng(), orderDetail.getStartLat(), orderDetail.getStartLng());
        float distance = (float) distance_gl * 1000;
        if (distance < (mWorkConfig.getMax_distance_five() * 1000)) {
            if (orderDetail.getMoney() >= mWorkConfig.getMin_money_five()) {
                float gl = (float) MyTool.calculateDistance(orderDetail.getStartLat(), orderDetail.getStartLng(), orderDetail.getEndLat(), orderDetail.getEndLng());
                if (orderDetail.getMoney() / gl > mWorkConfig.getDanjia_five()) {
                    if (orderDetail.getBookingTime() > mWorkConfig.getStartTimeMillis() && orderDetail.getBookingTime() < mWorkConfig.getEndTimeMillis()) {
                        boolean from_result = true;
                        for (String word : fromWordsfive) {
                            if (orderDetail.getStartAddress().contains(word)) {
                                from_result = false;
                                break;
                            }
                        }
                        if (from_result) {
                            boolean to_result = true;
                            for (String word : toWordsfive) {
                                if (orderDetail.getEndAddress().contains(word)) {
                                    to_result = false;
                                    break;
                                }
                            }
                            if (to_result) {
                                checkResult.setState(true);
                                return checkResult;
                            }

                        }

                    } else {
                        checkResult.setMsg("五档时间不符合:" + orderDetail.getBookingTime());
//                        logDebug(getOrderString(orderDetail)+"五档时间不符合:" + orderDetail.getBookingTime() + " | 设置值:" + mWorkConfig.getTime_minute_five() + "\n***************");
                    }
                } else {
                    checkResult.setMsg("五档单价不符合:" + (orderDetail.getMoney() / gl));
//                    logDebug(getOrderString(orderDetail)+ "五档单价不符合:" + (orderDetail.getMoney() / gl) + " | 设置值:" + mWorkConfig.getDanjia_five() + "\n***************");
                }


            } else {
                checkResult.setMsg("五档金额太小");
//                logDebug(getOrderString(orderDetail)+"五档金额不符合:" + orderDetail.getMoney() + " | 设置值:" + mWorkConfig.getMin_money_five() + "\n***************");
            }
        } else {
            checkResult.setMsg("五档距离太远");
//            logDebug(getOrderString(orderDetail)+"五档距离不符合:" + distance / 1000 + " | 设置值:" + mWorkConfig.getMax_distance_five() + "\n***************");
        }
        return checkResult;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text;
            switch (msg.what) {
                case UPDATE_FLOAT_BUTTON_HANDEL_PAUSE_TAG:
                    if (mFloatButtonWindowService != null) {
                        mFloatButtonWindowService.pauseWork();
                    }
                    break;
                case SUCCESS_GRAB_ORDER_HANDEL_PAUSE_TAG:
                    if (mFloatButtonWindowService != null) {
                        mFloatButtonWindowService.pauseWork();
                    }
                    playIncoming();
                    break;
            }
        }
    };
    public void start_work(){
        String local_config = mTinydb.getString(LOCAL_CONFIG_KEY);
        mWorkConfig = mGson.fromJson(local_config, WorkConfig.class);
        if (mWorkConfig.getOrder_types().isEmpty()) {
            orderTypes = new ArrayList<>();
        } else {
            orderTypes = Arrays.asList(mWorkConfig.getOrder_types().split("#"));
        }
        if (mWorkConfig.getFrom_place_one().isEmpty()) {
            fromWordsone = new ArrayList<>();
        } else {
            fromWordsone = Arrays.asList(mWorkConfig.getFrom_place_one().split("#"));
        }
        if (mWorkConfig.getTo_place_one().isEmpty()) {
            toWordsone = new ArrayList<>();
        } else {
            toWordsone = Arrays.asList(mWorkConfig.getTo_place_one().split("#"));
        }
        if (mWorkConfig.getFrom_place_two().isEmpty()) {
            fromWordstwo = new ArrayList<>();
        } else {
            fromWordstwo = Arrays.asList(mWorkConfig.getFrom_place_two().split("#"));
        }
        if (mWorkConfig.getTo_place_two().isEmpty()) {
            toWordstwo = new ArrayList<>();
        } else {
            toWordstwo = Arrays.asList(mWorkConfig.getTo_place_two().split("#"));
        }


        if (mWorkConfig.getFrom_place_three().isEmpty()) {
            fromWordsthree = new ArrayList<>();
        } else {
            fromWordsthree = Arrays.asList(mWorkConfig.getFrom_place_three().split("#"));
        }
        if (mWorkConfig.getTo_place_three().isEmpty()) {
            toWordsthree = new ArrayList<>();
        } else {
            toWordsthree = Arrays.asList(mWorkConfig.getTo_place_three().split("#"));
        }

        if (mWorkConfig.getFrom_place_four().isEmpty()) {
            fromWordsfour = new ArrayList<>();
        } else {
            fromWordsfour = Arrays.asList(mWorkConfig.getFrom_place_four().split("#"));
        }
        if (mWorkConfig.getTo_place_four().isEmpty()) {
            toWordsfour = new ArrayList<>();
        } else {
            toWordsfour = Arrays.asList(mWorkConfig.getTo_place_four().split("#"));
        }

        if (mWorkConfig.getFrom_place_five().isEmpty()) {
            fromWordsfive = new ArrayList<>();
        } else {
            fromWordsfive = Arrays.asList(mWorkConfig.getFrom_place_five().split("#"));
        }
        if (mWorkConfig.getTo_place_five().isEmpty()) {
            toWordsfive = new ArrayList<>();
        } else {
            toWordsfive = Arrays.asList(mWorkConfig.getTo_place_five().split("#"));
        }
        mOrderlistParam = MyTool.get_url_param(mWorkConfig.getEqd_url());
        startNetwork(mWorkConfig.getFresh_time()+800);
        wuxiaoOrder = new ArrayList<>();
        openButtonWindow();
    }
}