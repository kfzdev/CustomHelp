package com.customapi.fmkpl;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.customapi.fmkpl.bean.UserInform;
import com.customapi.fmkpl.customviews.BaseActivity;
import com.customapi.fmkpl.customviews.MyApplication;
import com.customapi.fmkpl.okhttp.BaseEntity;
import com.customapi.fmkpl.okhttp.OkHttpCallback;
import com.customapi.fmkpl.okhttp.OkHttpClientManager;
import com.customapi.fmkpl.okhttp.UserInfo;
import com.customapi.fmkpl.tool.MyTool;
import com.customapi.fmkpl.tool.StatusBarUtil;
import com.customapi.fmkpl.tool.ToastUtil;
import com.customapi.fmkpl.config.Constant;

import java.util.Map;

import okhttp3.Headers;

public class LoginActivity extends BaseActivity {
    public final static String USERNAME_LOCAL_KEY = "username_local_key";
    public final static String PASSWORD_LOCAL_KEY = "password_local_key";
    public final static String VIPTYPE_LOCAL_KEY = "viptype_local_key";
    public final static String BSPHP_SESSL_LOCAL_KEY = "bsphp_sessl_local_key";

    private final String TAG = getClass().getSimpleName();
    private EditText mAccountEditview;
    private EditText mPasswordEditview;

    private String cache_name;
    private String cache_psd_md5;
    private String username_post;
    private String userpsd_post;
    private boolean isLoging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        QMUIStatusBarHelper.translucent(this);
        StatusBarUtil.setTranslucentStatusBar(this,1);
        addStatusBar();
        initview();
    }

    private void login_success(){
        mTinydb.putString(USERNAME_LOCAL_KEY,username_post);
        mTinydb.putString(PASSWORD_LOCAL_KEY,userpsd_post);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        hide_loading();
        finish();
    }


    public void go_login(View v){
        if(isLoging){
            return;
        }
        show_loading();
        username_post = mAccountEditview.getText().toString();
        if("".equals(username_post)){
            return;
        }
        if(!TextUtils.isEmpty(cache_psd_md5)){
            userpsd_post = cache_psd_md5;
        }else{
            userpsd_post = mPasswordEditview.getText().toString();
        }
        if("".equals(userpsd_post)){
            return;
        }
        isLoging = true;
        Uri builtUri = Uri.parse(Constant.HttpUrl.LOGIN_URL)
                .buildUpon()
                .appendQueryParameter(Constant.TextTag.APPID, Constant.Config.APPID)
                .appendQueryParameter(Constant.TextTag.ACCOUNT,username_post)
                .appendQueryParameter(Constant.TextTag.PASSWORD,userpsd_post)
                .appendQueryParameter(Constant.TextTag.MACHINE_CODE, MyApplication.getAPPUUID())
                .build();
        OkHttpClientManager.getInstance().getAsyn(builtUri.toString(), new OkHttpCallback<String>() {
            @Override
            public void onSuccess(String response, Headers headers) {
                
                Map<String,String> res_map = gson.fromJson(response, Map.class);


                String code = String.valueOf(res_map.get("code"));
                if(Double.parseDouble(code)==1.0){
                    UserInform userInform = gson.fromJson(res_map.get("data"), UserInform.class);
                    UserInfo.getInstance().setUserInform(userInform);
                    UserInfo.getInstance().setLogin(true);
                    UserInfo.getInstance().setUsername(username_post);
                    login_success();
                }else{
                    ToastUtil.showShortToast("登录失败！");
                }
                hide_loading();
                isLoging = false;

            }

            @Override
            public void onFailure(BaseEntity<String> entity, String message, int responseCode) {
                hide_loading();
                isLoging = false;
            }
        },this);


    }
    private void login_err(String message){
        MyTool.showAlertDialog(LoginActivity.this, message, false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }, null);

    }

    private void initview(){
        Intent intent = getIntent();
        boolean hide_title = intent.getBooleanExtra("hide_title",false);
        TextView goback_icon = findViewById(R.id.goback_icon);
        if(hide_title){
            goback_icon.setVisibility(View.GONE);
        }

        cache_name = mTinydb.getString(USERNAME_LOCAL_KEY);
        cache_psd_md5 = mTinydb.getString(PASSWORD_LOCAL_KEY);
        mAccountEditview = findViewById(R.id.username);
        mPasswordEditview = findViewById(R.id.password);
        if(!TextUtils.isEmpty(cache_name)){
            mAccountEditview.setText(cache_name);
        }
        if(!TextUtils.isEmpty(cache_psd_md5)){
            mPasswordEditview.setText(cache_psd_md5);
        }
//        mLoginButton = findViewById(R.id.login);
        mAccountEditview.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable edit) {
                if(!edit.toString().equals(cache_name)){
                    cache_psd_md5 = null;
                    cache_name = null;
                }


            }
        });
        mPasswordEditview.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable edit) {
                //edit  输入结束呈现在输入框中的信息
                cache_psd_md5 = null;
                cache_name = null;
            }
        });
    }
    public void goback(View v){
        finish();
    }


    private boolean isPasswordInputType(int inputType) {
        final int variation =
                inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
        return variation
                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                || variation
                == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD)
                || variation
                == (EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD);
    }

}