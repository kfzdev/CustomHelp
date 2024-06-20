package com.customapi.fmkpl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.customapi.fmkpl.bean.Data;
import com.customapi.fmkpl.bean.UserInform;
import com.customapi.fmkpl.customviews.BaseActivity;
import com.customapi.fmkpl.okhttp.BaseEntity;
import com.customapi.fmkpl.okhttp.OkHttpCallback;
import com.customapi.fmkpl.okhttp.OkHttpClientManager;
import com.customapi.fmkpl.okhttp.UserInfo;
import com.customapi.fmkpl.tool.StatusBarUtil;
import com.customapi.fmkpl.config.Constant;

import java.util.Map;

import okhttp3.Headers;

public class MeActivity extends BaseActivity {

    private TextView mUsername;
    private TextView mDatetime;
    private Integer danjia = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        QMUIStatusBarHelper.translucent(this);
        StatusBarUtil.setTranslucentStatusBar(this,1);
        addStatusBar();
        init_view();
    }

    @Override
    protected void onResume() {
        super.onResume();
        get_user_inform();
    }

    private void init_view(){
        mUsername = findViewById(R.id.username);
        mDatetime = findViewById(R.id.datetime);
        init_data();
    }
    private void init_data(){
        mUsername.setText(UserInfo.getInstance().getUserInform().getData().getUserInfo().getAccount());
        String datetm;
        if((UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getDay()+
                UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getHour()+
                UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getMinute()+
                UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getSecond())>0){
            datetm = UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getDay()+"天"+
                    UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getHour()+"小时"+
                    UserInfo.getInstance().getUserInform().getData().getSurplusVipTime().getMinute()+"分钟";
        }else{
            datetm = "已过期";
        }

        mDatetime.setText(UserInfo.getInstance().getUserInform().getData().getVipExpireTimeStr());
    }
    private void get_user_inform(){
        super.show_loading();
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
                    init_data();
                }
                hide_loading();

            }

            @Override
            public void onFailure(BaseEntity<String> entity, String message, int responseCode) {
                hide_loading();
            }
        },this);
    }
    public void refresh(View v){
        get_user_inform();
    }
    public void logout(View v){
        UserInfo.getInstance().setUserInform(new UserInform());
        UserInfo.getInstance().setLogin(false);
        UserInfo.getInstance().setUsername("");
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void goback(View v){
        finish();
    }

}