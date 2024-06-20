package com.customapi.fmkpl.tool;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;

import com.customapi.fmkpl.bean.OrderDetail;
import com.customapi.fmkpl.bean.ScreenProperty;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class MyTool {
    public static ScreenProperty getAndroiodScreenProperty(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return new ScreenProperty(width, height);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static float getFloatValue(String str) {
        double d = 0;

        if (str != null && str.length() != 0) {
            StringBuffer bf = new StringBuffer();

            char[] chars = str.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (c >= '0' && c <= '9') {
                    bf.append(c);
                } else if (c == '.') {
                    if (bf.length() == 0) {
                        continue;
                    } else if (bf.indexOf(".") != -1) {
                        break;
                    } else {
                        bf.append(c);
                    }
                } else {
                    if (bf.length() != 0) {
                        break;
                    }
                }
            }
            try {
                d = Double.parseDouble(bf.toString());
            } catch (Exception e) {
            }
        }

        return Double.valueOf(d).floatValue();
    }

    public static int stateBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static String char2string(CharSequence charSequence) {
        if (charSequence != null) {
            return charSequence.toString();
        } else {
            return "";
        }
    }

    public static String millitimestampToDate(long time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        Date date;
        try {
            date = sdf.parse(sdf.format(time));
            assert date != null;
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getNowDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//要转换的时间格式
        Date date = new Date();
        return sdf.format(date);
    }

    public static long convertDateTimeToTimestamp(String dateTime) {
        // 定义日期时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");

        // 将日期时间字符串转换为Date对象
        Date date = null;
        try {
            date = format.parse(dateTime);
            // 返回时间戳（自1970年1月1日以来的毫秒数）
            assert date != null;
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;


    }


    public static boolean isStartAccessibilityService(Context context, String name) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : serviceInfos) {
            String id = info.getId();
            if (id.contains(name)) {
                return true;
            }
        }
        return false;
    }


    public static boolean needbreakapp() {
        return false;
    }

    public static void showAlertDialog(Context context, String content, Boolean showCancel, DialogInterface.OnClickListener confirmListener, @Nullable DialogInterface.OnClickListener cancelListener) {

        try {
            AlertDialog.Builder defaultBuilder = new AlertDialog.Builder(context);
            defaultBuilder.setTitle("提示");
            defaultBuilder.setMessage(content);
            defaultBuilder.setPositiveButton("确定", confirmListener);
            if (showCancel) {
                defaultBuilder.setNegativeButton("取消", cancelListener);
            }
            AlertDialog alertDialog = defaultBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FC632B"));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#707070"));
        } catch (Exception ignored) {

        }
    }

    public static Disposable disposable;

    public static void moveAppToFront(Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
        for (final ActivityManager.RunningTaskInfo runningTask : runningTasks) {
            assert runningTask.topActivity != null;
            if (runningTask.topActivity.getPackageName().equals(context.getPackageName())) {
                activityManager.moveTaskToFront(runningTask.id, 0);

                disposable = Observable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS).subscribe(aLong -> {
                    if (isAppRunningForeground(context)) {
                        disposable.dispose();
                    } else {
                        activityManager.moveTaskToFront(runningTask.id, 0);
                    }
                });


                break;
            }
        }
    }

    public static boolean isAppRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        assert activityManager != null;
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessList = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessList) {
            if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && runningAppProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                return true;
            }
        }

        return false;
    }

    public static LinkedHashMap<String, String> get_url_param(String s_url){
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        URL url = null;
        try {
            url = new URL(s_url);
            URLConnection connection = url.openConnection();

            // 获取URL参数

            String paramString = connection.getURL().getQuery();
            String[] paramsArray = paramString.split("&");
            for (String param : paramsArray) {
                String[] keyValue = param.split("=");
                if(!keyValue[0].equals("sig")){
                    params.put(keyValue[0], (keyValue.length > 1) ? java.net.URLDecoder.decode(keyValue[1], "UTF-8") : "");
                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
    public static double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // 返回距离，单位：公里
    }
}
