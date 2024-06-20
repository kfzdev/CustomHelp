package com.customapi.fmkpl;

import static com.customapi.fmkpl.service.AutoService.START_AUTO_WORK;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.customapi.fmkpl.bean.UserInform;
import com.customapi.fmkpl.config.Constant;
import com.customapi.fmkpl.customviews.MyApplication;
import com.customapi.fmkpl.dialog.PrivacyDialog;
import com.customapi.fmkpl.okhttp.BaseEntity;
import com.customapi.fmkpl.okhttp.OkHttpCallback;
import com.customapi.fmkpl.okhttp.OkHttpClientManager;
import com.customapi.fmkpl.okhttp.UserInfo;
import com.customapi.fmkpl.service.ApiService;
import com.donkingliang.labels.LabelsView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.customapi.fmkpl.bean.WorkConfig;
import com.customapi.fmkpl.customviews.BaseActivity;
import com.customapi.fmkpl.fragment.FifthconfigFragment;
import com.customapi.fmkpl.fragment.FragmentAdapter;
import com.customapi.fmkpl.fragment.LogFragment;
import com.customapi.fmkpl.fragment.NormalconfigFragment;

import com.customapi.fmkpl.service.AutoService;
import com.customapi.fmkpl.tool.KeyboardsUtils;
import com.customapi.fmkpl.tool.MyTool;
import com.customapi.fmkpl.tool.StatusBarUtil;
import com.customapi.fmkpl.tool.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class MainActivity extends BaseActivity {
    public static final String TAG = "AutoService";
    public final int UPDATE_LOG_TAG = 0;

    private Switch sys_baseSwitch;
    private WorkConfig mWorkConfig;
    public static final String LOCAL_CONFIG_KEY = "local_config_key";
    private LabelsView mOrderTypes;
    private final List<String> ordertype_label = Arrays.asList("取送车订单","充电订单","验车订单","预约订单");
    private EditText mFreshTime;
    private EditText mGrabTime;
    private EditText mEqd_url;
    private Map<String, String> params = new HashMap<>();
    private ViewPager mViewPager;
    private NormalconfigFragment oneNormalconfigFragment;
    private NormalconfigFragment twoNormalconfigFragment;
    private NormalconfigFragment threeNormalconfigFragment;
    private NormalconfigFragment fourNormalconfigFragment;
    private FifthconfigFragment fiveconfigFragment;
    private LogFragment mLogFragment;
    private boolean isRunning = false;
    private Button mStartWorkBt;
    private ApiService mApiService;
    private PrivacyDialog mPrivacyDialog;
    private boolean canAccept = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QMUIStatusBarHelper.translucent(this);
        StatusBarUtil.setTranslucentStatusBar(this,1);
        addStatusBar();
        init_view();
        show_privacy_dialog();
        get_user_valid();


    }
    private void show_privacy_dialog(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                canAccept = true;
            }
        },6000);
        mPrivacyDialog = new PrivacyDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canAccept){
                    if(!checkout()){
                        MyTool.showAlertDialog(MainActivity.this, "请打开权限中的定位授权(始终允许);存储权限;访问照片和视频", false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.super.go_app_settting();
                            }
                        },null);
                    }
                    mPrivacyDialog.dismiss();
                    mPrivacyDialog = null;
                }else{
                    ToastUtil.showShortToast("请至少阅读6秒");
                }

            }
        },new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);

            }
        });
        mPrivacyDialog.setCanotBackPress();
        mPrivacyDialog.setCanceledOnTouchOutside(false);
        mPrivacyDialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (sys_baseSwitch != null) {
            sys_baseSwitch.setChecked(isOpenAutoSearve());
            sys_baseSwitch.setSwitchTextAppearance(MainActivity.this,isOpenAutoSearve()?R.style.s_true:R.style.s_false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void network_err_dialig(){
        MyTool.showAlertDialog(this, "网络错误，请重试或重新打开APP", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        },null);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility","UseSwitchCompatOrMaterialCode"})
    private void init_view(){
        mStartWorkBt = findViewById(R.id.startWork);
        boolean temp_sys_state = isOpenAutoSearve();
        sys_baseSwitch = (Switch) findViewById(R.id.sys_base_status);
        sys_baseSwitch.setChecked(temp_sys_state);
        sys_baseSwitch.setSwitchTextAppearance(this,temp_sys_state?R.style.s_true:R.style.s_false);
        sys_baseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!compoundButton.isPressed()){
                    return;
                }
                if (b) {
                    sys_baseSwitch.setSwitchTextAppearance(MainActivity.this,R.style.s_true);
                    go_open_setting();
                }else {
                    sys_baseSwitch.setSwitchTextAppearance(MainActivity.this,R.style.s_false);
                    AutoService.close();
                }

            }
        });



        mOrderTypes = findViewById(R.id.order_type);
        mOrderTypes.setLabels(ordertype_label);

        mFreshTime = findViewById(R.id.fresh_time);
        mGrabTime = findViewById(R.id.grab_time);
        mEqd_url = findViewById(R.id.eqd_url);


        String local_config = mTinydb.getString(LOCAL_CONFIG_KEY);
        mWorkConfig = gson.fromJson(local_config, WorkConfig.class);
        if(mWorkConfig==null){
            mWorkConfig = new WorkConfig();
        }

        mFreshTime.setText(mWorkConfig.getFresh_time()+"");
        mGrabTime.setText(mWorkConfig.getGrab_time()+"");
        if(!mWorkConfig.getEqd_url().isEmpty()){
            mEqd_url.setText(mWorkConfig.getEqd_url()+"");
        }
        String[] s_types = mWorkConfig.getOrder_types().split("#");
        List<Integer> select_type = new ArrayList<>();
        for (String s_type : s_types) {
            int temp_index = ordertype_label.indexOf(s_type);
            if (temp_index > -1) {
                select_type.add(temp_index);
            }
        }
        mOrderTypes.setSelects(select_type);

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = new ArrayList<>();
        oneNormalconfigFragment = new NormalconfigFragment(1,mWorkConfig);
        twoNormalconfigFragment = new NormalconfigFragment(2,mWorkConfig);
        threeNormalconfigFragment = new NormalconfigFragment(3,mWorkConfig);
        fourNormalconfigFragment = new NormalconfigFragment(4,mWorkConfig);
        fiveconfigFragment = new FifthconfigFragment(mWorkConfig);
        mLogFragment = new LogFragment();
        fragments.add(oneNormalconfigFragment);
        fragments.add(twoNormalconfigFragment);
        fragments.add(threeNormalconfigFragment);
        fragments.add(fourNormalconfigFragment);
        fragments.add(fiveconfigFragment);
        fragments.add(mLogFragment);
        List<String> tab = new ArrayList<>();
        tab.add("一档");
        tab.add("二档");
        tab.add("三档");
        tab.add("四档");
        tab.add("五档");
        tab.add("日志");
        FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, fragments, tab);

        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(fragmentAdapter);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // 页面滑动过程中的位置变化
                        Log.d("ViewPager", "onPageScrolled: position=" + position + ", positionOffset=" + positionOffset);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // 选中的页面变化
                        Log.d("ViewPager", "onPageSelected: position=" + position);
                        oneNormalconfigFragment.hide_gb();
                        twoNormalconfigFragment.hide_gb();
                        threeNormalconfigFragment.hide_gb();
                        fourNormalconfigFragment.hide_gb();
//                        fiveconfigFragment = new FifthconfigFragment();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        // 滑动状态的变化
                        Log.d("ViewPager", "onPageScrollStateChanged: state=" + state);
                    }
                });

    }

    private boolean isOpenAutoSearve(){
        return MyTool.isStartAccessibilityService(this,BuildConfig.APPLICATION_ID);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void open_tb(View v){
        if(MyTool.needbreakapp()){
            return;
        }
        if(!checkFloatPermission(this)){
            showGlobalToast("请打开悬浮窗权限", Toast.LENGTH_SHORT);
            requestFloatPermission(100);
            return;
        }

        if(mOrderTypes.getSelectLabels().size()>0){
            List<String> ordertypes = new ArrayList<>();
            for(Integer index : mOrderTypes.getSelectLabels()){
                ordertypes.add(ordertype_label.get(index));
            }
            mWorkConfig.setOrder_types(String.join("#",ordertypes));
        }else{
            showGlobalToast("请选择类型", Toast.LENGTH_SHORT);
            return;
        }
        if(mEqd_url.getText().toString().isEmpty()){
            showGlobalToast("链接必填", Toast.LENGTH_SHORT);
            return;
        }

        if(mFreshTime.getText().toString().isEmpty()){
            mWorkConfig.setFresh_time(10);
        }else{
            mWorkConfig.setFresh_time(Integer.parseInt(mFreshTime.getText().toString()));
        }

        if(mGrabTime.getText().toString().isEmpty()){
            mWorkConfig.setGrab_time(0);
        }else{
            mWorkConfig.setGrab_time(Integer.parseInt(mGrabTime.getText().toString()));
        }

        mWorkConfig.setSys_log_state(false);
        mWorkConfig.setEqd_url(mEqd_url.getText().toString());
        oneNormalconfigFragment.setWorkConfig(mWorkConfig);
        twoNormalconfigFragment.setWorkConfig(mWorkConfig);
        threeNormalconfigFragment.setWorkConfig(mWorkConfig);
        fourNormalconfigFragment.setWorkConfig(mWorkConfig);
        fiveconfigFragment.setWorkConfig(mWorkConfig);
        mTinydb.putString(LOCAL_CONFIG_KEY,gson.toJson(mWorkConfig));
//        sendstart2autoservice();
        if(!UserInfo.getInstance().isLogin()){
            go_login_page(null);
            return;
        }
        checkExpirationDate((state, msg, now_date) -> {
            if(state==200){

                sendstart2autoservice();
            }else{
                ToastUtil.showShortToast(msg);
            }
        });


    }
    private final ServiceConnection mApiServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            mApiService = ((ApiService.ApiServiceBinder) service).getService();
            mApiService.start_work();
            mApiService.setMainActivityUi(MainActivity.this);
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i("mFloatButtonConnection", "服务关闭了");
        }
    };
    private void sendstart2autoservice() {
        if (isOpenAutoSearve()){
            Bundle bundle = new Bundle();
            bundle.putString("type", START_AUTO_WORK);
            Intent intent = new Intent();
            intent.setAction(AutoService.COM_BROADCAST_NOTIFICE);//用隐式意图来启动广播
            intent.putExtra(AutoService.BROADCAST_MSG_KEY, bundle);
            sendBroadcast(intent);
            if(AutoService.mAutoService!=null){
                AutoService.mAutoService.setMainActivityUi(this);
            }
        }
        if(mApiService!=null){
            mApiService.start_work();
        }else{
            Intent s_intent = new Intent(this, ApiService.class);
            bindService(s_intent, mApiServiceConnection, Context.BIND_AUTO_CREATE);
        }
        mViewPager.setCurrentItem(5);
    }
    public void go_login_page(View v){
        Intent intent = new Intent();
        if(UserInfo.getInstance().isLogin()){
            intent.setClass(this, MeActivity.class);
        }else{
            intent.setClass(this, LoginActivity.class);
        }
        startActivity(intent);
    }
    private void go_open_setting(){
        if (!isOpenAutoSearve()) {
            try {
                Intent intent_abs = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent_abs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_abs);
            } catch (Exception e) {
                Intent intent_abs = new Intent(Settings.ACTION_SETTINGS);
                intent_abs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_abs);
                e.printStackTrace();
            }
        }
    }
    @CallSuper
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() ==  MotionEvent.ACTION_DOWN ) {
            View view = getCurrentFocus();
            if (KeyboardsUtils.isShouldHideKeyBord(view, ev)) {
                KeyboardsUtils.hintKeyBoards(view);
                mFreshTime.setCursorVisible(false);
                mFreshTime.clearFocus();
                mGrabTime.setCursorVisible(false);
                mGrabTime.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void updateLog(String log){
        Message message=mHandler.obtainMessage();
        message.what=UPDATE_LOG_TAG;
        message.obj=log;
        mHandler.sendMessage(message);
    }

    private void get_user_valid(){
        String username = mTinydb.getString(LoginActivity.USERNAME_LOCAL_KEY);
        String password = mTinydb.getString(LoginActivity.PASSWORD_LOCAL_KEY);
        if(username.isEmpty()||password.isEmpty()){
            return;
        }
        super.show_loading();
        Uri builtUri = Uri.parse(Constant.HttpUrl.LOGIN_URL)
                .buildUpon()
                .appendQueryParameter(Constant.TextTag.APPID, Constant.Config.APPID)
                .appendQueryParameter(Constant.TextTag.ACCOUNT,username)
                .appendQueryParameter(Constant.TextTag.PASSWORD,password)
                .appendQueryParameter(Constant.TextTag.MACHINE_CODE, MyApplication.getAPPUUID())
                .build();
        OkHttpClientManager.getInstance().getAsyn(builtUri.toString(), new OkHttpCallback<String>() {
            @Override
            public void onSuccess(String response, Headers headers) {
                Gson gson = new Gson();
                Map<String,String> res_map = gson.fromJson(response, Map.class);

                String code = String.valueOf(res_map.get("code"));
                if(Double.parseDouble(code)==1.0){
                    UserInform userInform = gson.fromJson(res_map.get("data"), UserInform.class);
                    UserInfo.getInstance().setUserInform(userInform);
                    UserInfo.getInstance().setLogin(true);
                    UserInfo.getInstance().setUsername(username);
                }
                hide_loading();
            }

            @Override
            public void onFailure(BaseEntity<String> entity, String message, int responseCode) {
                hide_loading();
            }
        },this);
    }
    private final Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text;
            switch (msg.what){
                case UPDATE_LOG_TAG:
                    text = msg.obj.toString();
                    mLogFragment.updateView(text);
                    break;
            }
        }
    };
}