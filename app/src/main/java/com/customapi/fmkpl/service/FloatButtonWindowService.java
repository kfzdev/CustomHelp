package com.customapi.fmkpl.service;


import static com.customapi.fmkpl.customviews.CheckPermissionsActivity.checkFloatPermission;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.customapi.fmkpl.R;
import com.customapi.fmkpl.bean.ScreenProperty;
import com.customapi.fmkpl.tool.MyTool;


public class FloatButtonWindowService extends Service {
    private final String TAG = getClass().getSimpleName();
    private boolean init_state = true;
    private boolean mWindowManagerInit = false;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private LayoutInflater inflater;
    public static boolean isStarted = false;
    //view
    private View mFloatingLayout;    //布局View
    private TextView mFloatView_textView;
    private LinearLayout mactionBt;

    private int nolmalW = 0;
    private int nolmalH = 0;
    private int statusBarHeight = 0;
    private TextView mActionStateText;
    private AutoService mAutoService;
    private ApiService mApiService;
    private LinearLayout mGoback;
    private View mCenterLine;
    private View mDuiqiline;
    private boolean showDqline;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        showFloatingWindow();
        return new FloatButtonWindowServiceBinder();
    }
    public class FloatButtonWindowServiceBinder extends Binder {
        public FloatButtonWindowService getService(){
            return FloatButtonWindowService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        ScreenProperty screenProperty = MyTool.getAndroiodScreenProperty(this);
        nolmalW = screenProperty.getScreenWidthPx()/7;
        nolmalH = nolmalW*2;
        statusBarHeight = MyTool.stateBarHeight(this);
        initWindow();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWindowManager != null) {
            mWindowManager.removeView(mFloatingLayout);
            isStarted = false;
        }
    }
    public void init_service(AutoService service){
        mAutoService = service;
    }
    public void init_service_a(ApiService service){
        mApiService = service;
    }

    public boolean get_init_state(){
        return init_state;
    }
    public void hide_dq_line(){
        if(!showDqline){
            return;
        }
        mDuiqiline.setVisibility(View.GONE);
        showDqline = false;
    }
    public void show_dq_line(){
        if(showDqline){
            return;
        }
        mDuiqiline.setVisibility(View.VISIBLE);
        showDqline = true;
    }
    public void leftView(){
        wmParams.x = 0;
        mWindowManager.updateViewLayout(mFloatingLayout, wmParams);
    }
    public void pauseWork(){
        if(mAutoService!=null){
            mAutoService.updateMainState(false);
        }
        if(mApiService!=null){
            mApiService.updateMainState(false);
        }
        mActionStateText.setText(getString((R.string.zhunbeijiuxuasjkdsf)));
        mGoback.setVisibility(View.VISIBLE);
        update_window_size(nolmalH);
    }
    public void finish_work(){
        mactionBt.setVisibility(View.GONE);
        mGoback.setVisibility(View.VISIBLE);
//        update_window_size(nolmalW);
    }
    private void update_window_size(int height){
        if(height == nolmalH){
            mCenterLine.setVisibility(View.VISIBLE);
        }else{
            mCenterLine.setVisibility(View.GONE);
        }
        wmParams.width = nolmalW;
        wmParams.height = height;
        mWindowManager.updateViewLayout(mFloatingLayout, wmParams);
    }

    /**
     * 设置悬浮框基本参数（位置、宽高等）
     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initWindow() {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wmParams = getParam();
        inflater = LayoutInflater.from(getApplicationContext());
        mFloatingLayout = inflater.inflate(R.layout.float_button_window, null);
    }

    private WindowManager.LayoutParams getParam() {
        ScreenProperty screenProperty = MyTool.getAndroiodScreenProperty(this);
        wmParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        wmParams.format = PixelFormat.RGBA_8888;
        //设置可以显示在状态栏上
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = nolmalW;
        wmParams.height = nolmalH;
        //这是悬浮窗居中位置
        wmParams.gravity = Gravity.START | Gravity.TOP;
        //70、210是我项目中的位置哦
        wmParams.x = 0;
        wmParams.y = screenProperty.getScreenHeightPx()/2;
        return wmParams;
    }
    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断系统版本
            if (!checkFloatPermission(this)) {
                init_state = false;
                return;
            }
        }

        mWindowManager.addView(mFloatingLayout, wmParams);
        mActionStateText = mFloatingLayout.findViewById(R.id.action_state_text);
        mactionBt = mFloatingLayout.findViewById(R.id.action_bt);

        mGoback = mFloatingLayout.findViewById(R.id.goback);

        mCenterLine = mFloatingLayout.findViewById(R.id.centerLine);
        mDuiqiline = mFloatingLayout.findViewById(R.id.duiqiline);
        mWindowManagerInit = true;
        mactionBt.setOnTouchListener(new FloatingListener());
        mGoback.setOnTouchListener(new FloatingListener());
        mFloatingLayout.setOnTouchListener(new FloatingListener());
        mGoback.setOnClickListener(v -> {
            MyTool.moveAppToFront(FloatButtonWindowService.this);
            if(mAutoService!=null){
                mAutoService.break_work_from_float();
            }
            if(mApiService!=null){
                mApiService.break_work_from_float();
            }
        });
        mactionBt.setOnClickListener(v -> {
            if(mAutoService!=null){
                if(!MyTool.isStartAccessibilityService(mAutoService,"com.customapi.fmkpl")){
                    return;
                }
                if(mAutoService.isWorking()){
                    mAutoService.updateMainState(false);
                    mActionStateText.setText(getString(R.string.zhunbeijiuxuasjkdsf));
                    mGoback.setVisibility(View.VISIBLE);
                    update_window_size(nolmalH);
                }else{
                    mAutoService.updateMainState(true);
                    mActionStateText.setText(getString((R.string.zhixingzhongodsjf)));
                    mGoback.setVisibility(View.GONE);
                    update_window_size(nolmalW);
                }
            }
            if(mApiService!=null){
                if(mApiService.isWorking()){
                    mApiService.updateMainState(false);
                    mActionStateText.setText(getString(R.string.zhunbeijiuxuasjkdsf));
                    mGoback.setVisibility(View.VISIBLE);
                    update_window_size(nolmalH);
                }else{
                    mApiService.updateMainState(true);
                    mActionStateText.setText(getString((R.string.zhixingzhongodsjf)));
                    mGoback.setVisibility(View.GONE);
                    update_window_size(nolmalW);
                }
            }

        });
    }
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    private int mStartX, mStartY, mStopX, mStopY;
    private boolean isMove;

    private class FloatingListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();

                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;
                    if(wmParams.y<=statusBarHeight){
                        wmParams.y = statusBarHeight;
                    }
                    mWindowManager.updateViewLayout(mFloatingLayout, wmParams);

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    if (Math.abs(mStartX - mStopX) >= 3 || Math.abs(mStartY - mStopY) >= 3) {
                        isMove = true;
                    }
                    break;
            }
            return isMove;
        }
    }
}