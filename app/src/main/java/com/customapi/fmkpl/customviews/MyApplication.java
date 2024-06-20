package com.customapi.fmkpl.customviews;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.customapi.fmkpl.tool.TinyDB;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class MyApplication extends Application {
    private final static String TAG = "MyApplication";
    private static String APPUUID = "";
    private static boolean APPISUSED = true;
    private TinyDB mTinydb;
    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        mTinydb = new TinyDB(this);
        context =getApplicationContext();
        final String temp_local_uuid = mTinydb.getString("app_uuid");
        if(temp_local_uuid.isEmpty()){
            final String uuid = get_uuid();
            if(!uuid.isEmpty()){
                mTinydb.putString("app_uuid",uuid);
                APPUUID = uuid;
                setAPPISUSED(true);
            }else{
                setAPPISUSED(false);
            }
        }else{
            APPUUID = temp_local_uuid;
            setAPPISUSED(true);
        }
    }
    private String get_uuid(){
        try {
            DeviceIdentifier.register(this);
            String reslut = "";
            if(!DeviceIdentifier.getAndroidID(this).isEmpty()){
                reslut = DeviceIdentifier.getAndroidID(this);
            }else if(!DeviceIdentifier.getWidevineID().isEmpty()){
                reslut = DeviceIdentifier.getWidevineID();
            }else if(!DeviceIdentifier.getPseudoID().isEmpty()){
                reslut = DeviceIdentifier.getPseudoID();
            }else if(!DeviceIdentifier.getGUID(this).isEmpty()){
                reslut = DeviceIdentifier.getGUID(this);
            }
            return toMD5(reslut);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private String toMD5(String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            int digestInt = digest[i] & 0xff;
            String hexString = Integer.toHexString(digestInt);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }


    public static Context getContext() {
        return context;
    }
    public static String getAPPUUID() {
        return APPUUID;
    }

    public static void setAPPISUSED(boolean APPISUSED) {
        MyApplication.APPISUSED = APPISUSED;
    }

}
