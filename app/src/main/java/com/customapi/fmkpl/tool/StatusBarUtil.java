package com.customapi.fmkpl.tool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StatusBarUtil {
    public static void setTranslucentStatusBar(Activity activity,int modelType){
        QMUIStatusBarHelper.translucent(activity);
        if(modelType==0){
            QMUIStatusBarHelper.setStatusBarDarkMode(activity);
        }else if(modelType==1){
            QMUIStatusBarHelper.setStatusBarLightMode(activity);
        }
    }

    /**
     * 设置状态栏为透明
     * @param activity
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * @param activity
     * @param colorId
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        //Android6.0（API 23）以上，系统方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTint库使4.4版本状态栏变色，需要先将状态栏设置为透明
            setTranslucentStatus(activity);
            //设置状态栏颜色
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorId);
        }
    }
    public  static void quanpingstatus(Activity activity) {

        Window window = activity.getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                    WindowManager.LayoutParams. FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
    public static void setXJStatusBar(Activity activity, boolean isTextDark) {
        Window window = activity.getWindow();
        if(!isTextDark){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(com.customapi.fmkpl.tool.OSUtil.isMIUI()) {
                //小米MIUI系统
                setMIUIStatusBarTextMode(activity, isTextDark);
            } else if(com.customapi.fmkpl.tool.OSUtil.isFlyme()) {
                //魅族flyme系统
                setFlymeStatusBarTextMode(activity, isTextDark);
            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                将状态栏文字变黑色
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            } else {
                //4.4以上6.0以下的其他系统，暂时没有修改状态栏的文字图标颜色的方法，有可以加上
            }
        }
    }
    /**
     * 设置状态栏模式
     * @param activity
     * @param isTextDark 文字、图标是否为黑色 （false为默认的白色）
     * @param colorId 状态栏颜色
     * @return
     */
    public static void setStatusBarMode(Activity activity, boolean isTextDark, int colorId) {
        Window window = activity.getWindow();
        if(Build.MANUFACTURER.equals("Meizu")) {
            //魅族flyme系统
            setFlymeStatusBarTextMode(activity, isTextDark);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                将状态栏文字变黑色
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            if (isTextDark) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }

    /**
     * 设置Flyme系统状态栏的文字图标颜色
     * @param activity
     * @param isDark 状态栏文字及图标是否为深色
     * @return
     */
    public static boolean setFlymeStatusBarTextMode(Activity activity, boolean isDark) {
        Window window = activity.getWindow();
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isDark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置MIUI系统状态栏的文字图标颜色（MIUIV6以上）
     * @param activity
     * @param isDark 状态栏文字及图标是否为深色
     * @return
     */
    public static boolean setMIUIStatusBarTextMode(Activity activity, boolean isDark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (isDark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View
                                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
                                .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View
                                .SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return result;
    }
}
