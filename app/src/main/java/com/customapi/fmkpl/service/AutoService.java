package com.customapi.fmkpl.service;


import static com.customapi.fmkpl.MainActivity.LOCAL_CONFIG_KEY;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.baidu.location.LocationClient;
import com.google.gson.Gson;
import com.customapi.fmkpl.BuildConfig;
import com.customapi.fmkpl.MainActivity;
import com.customapi.fmkpl.R;
import com.customapi.fmkpl.bean.WorkConfig;
import com.customapi.fmkpl.datas.WorkId;
import com.customapi.fmkpl.tool.MyTool;
import com.customapi.fmkpl.tool.TinyDB;

public class AutoService extends AccessibilityService {
    public static final String TAG = "AutoService";
    public static final String COM_BROADCAST_NOTIFICE = BuildConfig.APPLICATION_ID + ".broadcast.notifice";
    public static final String BROADCAST_MSG_KEY = "autoservice_message";
    public static final String START_AUTO_WORK = "start_auto_work";
    public static final String CLOSE_AUTO_WORK = "close_auto_work";
    public final int UPDATE_FLOAT_VIEW_HANDEL_TAG = 0;
    public final int UPDATE_FLOAT_BUTTON_HANDEL_PAUSE_TAG = 2;
    public final int SUCCESS_GRAB_ORDER_HANDEL_PAUSE_TAG = 3;


    public static final String EDJ_PAKG = "cn.edaijia.android.driverclient";
    public TinyDB mTinydb;
    private AutoBroadcastReceiver mAutoBroadcastReceiver;
    public static AutoService mAutoService;
    private AccessbilityFloatView mAccessbilityFloatView;
    private boolean main_work_state;
    private boolean main_working = false;
    private WorkId mWorkId = new WorkId();
    private FloatButtonWindowService mFloatButtonWindowService;
    private WorkConfig mWorkConfig;
    private Gson mGson;
    private MainActivity mainActivityUi;
    private AccManager mAccManager;
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        LocationClient.setAgreePrivacy(true);

        mTinydb = new TinyDB(this);

        mAutoBroadcastReceiver = new AutoBroadcastReceiver();
        //2.创建intent-filter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(AutoService.COM_BROADCAST_NOTIFICE);
        //3.注册广播接收者
        registerReceiver(mAutoBroadcastReceiver, filter);
        mAccManager = new AccManager(new LogInterface() {
            @Override
            public void logShow(String value) {
                logDebug(value);
            }

            @Override
            public void finish() {
                success_grab();
            }
        });
        super.onCreate();
    }

    public void setMainActivityUi(MainActivity ui) {
        if (mainActivityUi == null) {
            mainActivityUi = ui;
        }
    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mAutoService = this;

        MyTool.moveAppToFront(this);
        mGson = new Gson();
        startGetEvent();
    }
    private void success_grab() {
        mHandler.sendEmptyMessage(SUCCESS_GRAB_ORDER_HANDEL_PAUSE_TAG);
    }

    private void startGetEvent() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mAutoService != null) {
                    if (main_work_state) {
                        try {
                            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                            if (rootNode != null) {
                                mAccManager.find_order_one(rootNode);
                            }


                        }catch (Exception e){

                        }

                        sleep_app(50);
                    }

                }
            }
        }).start();
    }

    private void sleep_app(int time) {
        if (time < 1) {
            return;
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


    }
    public void updateMainState(boolean state) {
        main_work_state = state;

    }

    public void break_work_from_float() {
        main_work_state = false;
        close_float();
    }

    public void openButtonWindow() {
        if (mFloatButtonWindowService == null) {
            Intent intent = new Intent(AutoService.this, FloatButtonWindowService.class);
            if (bindService(intent, mFloatButtonWindowConnection, Context.BIND_AUTO_CREATE)) {
                Log.e(TAG, "悬浮窗服务开启成功！");
            } else {
                Log.e(TAG, "悬浮窗服务开启失败！");
            }
        } else {
            mFloatButtonWindowService.init_service(mAutoService);
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

    private void close_float() {
        try {
            unbindService(mFloatButtonWindowConnection);
        } catch (Exception ignored) {
        }
        try {
            mAccessbilityFloatView.removeView();
        } catch (Exception ignored) {
        }
        mAccessbilityFloatView = null;
        mFloatButtonWindowService = null;
    }

    private final ServiceConnection mFloatButtonWindowConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            mFloatButtonWindowService = ((FloatButtonWindowService.FloatButtonWindowServiceBinder) service).getService();
            if (!mFloatButtonWindowService.get_init_state()) {
                close_float();
            } else {
                mFloatButtonWindowService.init_service(mAutoService);
            }

        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i("mFloatButtonConnection", "服务关闭了");
        }
    };

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(mAutoBroadcastReceiver);
        mAutoBroadcastReceiver = null;
        return super.onUnbind(intent);
    }

    public static void close() {
        if (mAutoService != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mAutoService.disableSelf();
            }
        }
    }

    public boolean isWorking() {
        return main_work_state;
    }


    private class AutoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (TextUtils.equals(intent.getAction(), COM_BROADCAST_NOTIFICE)) {
                    Bundle msgBundle = intent.getBundleExtra(AutoService.BROADCAST_MSG_KEY);
                    String type = msgBundle.getString("type");
                    switch (type) {
                        case START_AUTO_WORK:
                            String local_config = mTinydb.getString(LOCAL_CONFIG_KEY);
                            mWorkConfig = mGson.fromJson(local_config, WorkConfig.class);

                            mAccManager.updateConfig(mWorkConfig);
//                            openButtonWindow();

                            break;
                        case CLOSE_AUTO_WORK:
                            close();
                            close_float();
                            break;
                    }
                }

            }
        }

    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text;
            switch (msg.what) {
                case UPDATE_FLOAT_VIEW_HANDEL_TAG:
                    if (mAccessbilityFloatView != null) {
                        text = msg.obj.toString();
                        mAccessbilityFloatView.updateView(text);
                    }
                    break;
                case UPDATE_FLOAT_BUTTON_HANDEL_PAUSE_TAG:
                    if (mFloatButtonWindowService != null) {
                        mFloatButtonWindowService.pauseWork();
                    }
                    break;
                case SUCCESS_GRAB_ORDER_HANDEL_PAUSE_TAG:
                    if (mFloatButtonWindowService != null) {
                        mFloatButtonWindowService.pauseWork();
                    }
                    break;
            }
        }
    };

    private void logDebug(String msg) {
        Log.d(TAG, msg);
        if (mainActivityUi != null) {
            mainActivityUi.updateLog(msg);
        }
    }
}
