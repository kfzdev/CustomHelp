package com.customapi.fmkpl.service;


import static com.customapi.fmkpl.customviews.CheckPermissionsActivity.checkFloatPermission;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.customapi.fmkpl.R;
import com.customapi.fmkpl.adpter.SysLogadp;
import com.customapi.fmkpl.bean.ScreenProperty;
import com.customapi.fmkpl.tool.MyTool;

import java.util.ArrayList;


public class AccessbilityFloatView {
    private final String TAG = getClass().getSimpleName();
    private final AccessibilityService mAccessibilityService;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private LayoutInflater inflater;
    public static boolean isStarted = false;
    //view
    private View mFloatingLayout;    //布局View
    private int nolmalW = 0;
    private String syslog = "";
    private ListView mSyslogs;
    private ArrayList<String> log_list;
    private SysLogadp mSysLogadp;

    public AccessbilityFloatView(AccessibilityService accessibilityService) {
        isStarted = true;
        ScreenProperty screenProperty = MyTool.getAndroiodScreenProperty(accessibilityService);
        nolmalW = screenProperty.getScreenWidthPx()/2;
        mAccessibilityService = accessibilityService;
        log_list = new ArrayList<>();
        initWindow();
        showFloatingWindow();

    }
    public void showView() {
        if (mWindowManager != null) {
            mWindowManager.addView(mFloatingLayout,wmParams);
        }
    }
    public void hideView() {
        if (mWindowManager != null) {
            mWindowManager.removeView(mFloatingLayout);
        }
    }


    public void removeView() {
        if (mWindowManager != null) {
            mWindowManager.removeView(mFloatingLayout);
            isStarted = false;
        }
    }
    public void updateView(String text){
        log_list.add(text);
        mSysLogadp.refreshData(log_list);
    }
    private void initWindow() {
        mWindowManager = (WindowManager) mAccessibilityService.getSystemService(Context.WINDOW_SERVICE);
        wmParams = getParam();
        inflater = LayoutInflater.from(mAccessibilityService.getApplicationContext());
        mFloatingLayout = inflater.inflate(R.layout.float_view_window, null);
    }

    private WindowManager.LayoutParams getParam() {
        wmParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            wmParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        wmParams.format = PixelFormat.TRANSLUCENT;
        //设置可以显示在状态栏上
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = nolmalW;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //这是悬浮窗居中位置
        wmParams.gravity = Gravity.START | Gravity.BOTTOM;
        //70、210是我项目中的位置哦
        wmParams.x = 0;
        wmParams.y = MyTool.dip2px(mAccessibilityService,20);
        return wmParams;
    }
    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断系统版本
            if (!checkFloatPermission(mAccessibilityService)) {
                return;
            }
        }
        mWindowManager.addView(mFloatingLayout, wmParams);
        mSyslogs = mFloatingLayout.findViewById(R.id.syslog);
        mSysLogadp = new SysLogadp(mAccessibilityService,log_list);
        mSyslogs.setAdapter(mSysLogadp);
    }

}