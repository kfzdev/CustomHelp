package com.customapi.fmkpl.tool;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.customapi.fmkpl.customviews.MyApplication;


public class ToastUtil {
    private static Toast toast = null;
    public static void showShortToast(String text){
        try {
            if(toast!=null){
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }catch (Exception ignored){
            Looper.prepare();
            if(toast!=null){
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(MyApplication.getContext(), text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Looper.loop();
        }
    }
}
