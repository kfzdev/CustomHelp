package com.customapi.fmkpl.customviews;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.customapi.fmkpl.R;
import com.customapi.fmkpl.bean.Data;
import com.customapi.fmkpl.dialog.LoadingDialog;
import com.customapi.fmkpl.okhttp.BaseEntity;
import com.customapi.fmkpl.okhttp.OkHttpCallback;
import com.customapi.fmkpl.okhttp.OkHttpClientManager;
import com.customapi.fmkpl.okhttp.UserInfo;
import com.customapi.fmkpl.tool.MyTool;
import com.customapi.fmkpl.tool.TinyDB;
import com.customapi.fmkpl.tool.ToastUtil;
import com.customapi.fmkpl.config.Constant;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public abstract class BaseActivity extends CheckPermissionsActivity {
    public TinyDB mTinydb;
    private LoadingDialog mLoadingDialog;
    private Toast globalToast;
    public Gson gson;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        mTinydb = new TinyDB(this);
        gson = new Gson();
    }
    public void addStatusBar(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View statusBarView = layoutInflater.inflate(R.layout.statusbar, null);

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,

                statusBarHeight);

        statusBarView.setLayoutParams(params);

        LinearLayout appbar = findViewById(R.id.appbar);
        if(appbar!=null){
            appbar.addView(statusBarView,0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(globalToast!=null){
            globalToast.cancel();
            globalToast = null;
        }
        super.onDestroy();
    }


    public void showGlobalToast(String text,int duration){
        try {
            if(globalToast!=null){
                globalToast.cancel();
                globalToast = null;
            }
            globalToast = Toast.makeText(this, text, duration);
            globalToast.show();
        }catch (Exception ignored){

        }
    }
    public void showShortGlobalToast(String text){
        try {
            if(globalToast!=null){
                globalToast.cancel();
                globalToast = null;
            }
            globalToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            globalToast.show();
        }catch (Exception ignored){

        }
    }
    public void showLongGlobalToast(String text){
        try {
            if(globalToast!=null){
                globalToast.cancel();
                globalToast = null;
            }
            globalToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            globalToast.show();
        }catch (Exception ignored){

        }
    }

    public void show_loading(){
        if(mLoadingDialog==null){
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setCanotBackPress();
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mLoadingDialog!=null){
                    hide_loading();
                }
            }
        }, 20000);
    }
    public void hide_loading(){
        if(mLoadingDialog!=null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }


    public void check_timeout(BsverificationCallback bsverificationCallback){


        show_loading();
        Uri builtUri = Uri.parse(Constant.HttpUrl.GET_USER_INFO)
                .buildUpon()
                .appendQueryParameter(Constant.TextTag.APPID, Constant.Config.APPID)
                .appendQueryParameter(Constant.TextTag.USER_ID,UserInfo.getInstance().getUserInform().getUser_id()+"")
                .appendQueryParameter(Constant.TextTag.USER_TOKEN,UserInfo.getInstance().getUserInform().getUser_token())
                .build();
        OkHttpClientManager.getInstance().getAsyn(builtUri.toString(), new OkHttpCallback<String>() {
            @Override
            public void onSuccess(String response, Headers headers) {

                Map<String,String> res_map = gson.fromJson(response, Map.class);

                String code = String.valueOf(res_map.get("code"));
                if(Double.parseDouble(code)==1.0){
                    Data userInform = gson.fromJson(res_map.get("data"), Data.class);
                    UserInfo.getInstance().getUserInform().setData(userInform);
                    if((UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getDay()+
                            UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getHour()+
                            UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getMinute()+
                            UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getSecond())>0){
                        bsverificationCallback.onResponse(200,"","");
                    }else{
                        bsverificationCallback.onResponse(0,"不允许使用","");
                    }
                }
                hide_loading();

            }

            @Override
            public void onFailure(BaseEntity<String> entity, String message, int responseCode) {
                hide_loading();
            }
        },this);
    }
    public void checkExpirationDate(BsverificationCallback bsverificationCallback){

        if(!UserInfo.getInstance().isLogin()){
            bsverificationCallback.onResponse(0,"请先登录","");
            return;
        }
        check_timeout(bsverificationCallback);

    }



}