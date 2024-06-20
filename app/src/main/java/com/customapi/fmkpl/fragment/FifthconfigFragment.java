package com.customapi.fmkpl.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.customapi.fmkpl.R;
import com.customapi.fmkpl.bean.WorkConfig;
import com.customapi.fmkpl.tool.MyTool;
import com.customapi.fmkpl.tool.ToastUtil;

public class FifthconfigFragment extends Fragment {
    private EditText mMax_distance;
    private EditText mMin_money;
    private EditText mFrom_words;
    private EditText mTo_words;
    private EditText mMax_time;
    private EditText mDanjia;
    private EditText mStart_time;
    private EditText mEnd_time;
    private EditText mLat;
    private EditText mLng;
    private Switch aSwitch;
    private WorkConfig mWorkConfig;
    public FifthconfigFragment(WorkConfig workConfig){
        mWorkConfig = workConfig;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fifthconfig, container,false);
        mMax_distance = view.findViewById(R.id.max_distance);
        mMin_money = view.findViewById(R.id.min_money);
        mFrom_words = view.findViewById(R.id.from_words);
        mTo_words = view.findViewById(R.id.to_words);
        mMax_time = view.findViewById(R.id.max_time);
        mDanjia = view.findViewById(R.id.danjia);
        mStart_time = view.findViewById(R.id.start_time);
        mEnd_time = view.findViewById(R.id.end_time);
        mLat = view.findViewById(R.id.lat);
        mLng = view.findViewById(R.id.lng);
        aSwitch = view.findViewById(R.id.config_state);
        difficute_t();
        setLocalValue();
        return view;
    }
    private void setLocalValue(){
        if(mWorkConfig.getTime_minute_five()>0){
            mMax_time.setText(mWorkConfig.getTime_minute_five()+"");
        }
        mMax_distance.setText(mWorkConfig.getMax_distance_five()>0?mWorkConfig.getMax_distance_five()+"":"");
        mMin_money.setText(mWorkConfig.getMin_money_five()>0?mWorkConfig.getMin_money_five()+"":"");
        mFrom_words.setText(mWorkConfig.getFrom_place_five());
        mTo_words.setText(mWorkConfig.getTo_place_five());
        mDanjia.setText(mWorkConfig.getDanjia_five()>0?mWorkConfig.getDanjia_five()+"":"");
        mStart_time.setText(mWorkConfig.getStart_time().isEmpty()?"":mWorkConfig.getStart_time());
        mEnd_time.setText(mWorkConfig.getEnd_time().isEmpty()?"":mWorkConfig.getEnd_time());
        mLat.setText(mWorkConfig.getMyLat()==0?"":mWorkConfig.getMyLat()+"");
        mLng.setText(mWorkConfig.getMyLng()==0?"":mWorkConfig.getMyLng()+"");
        aSwitch.setChecked(mWorkConfig.isType_five_state());
    }
    public void setWorkConfig(WorkConfig mWorkConfig){
        if(aSwitch!=null&&aSwitch.isChecked()){
            mWorkConfig.setMax_distance_five(Float.parseFloat(mMax_distance.getText().toString()));
            mWorkConfig.setMin_money_five(Float.parseFloat(mMin_money.getText().toString()));
            mWorkConfig.setFrom_place_five(mFrom_words.getText().toString());
            mWorkConfig.setTo_place_five(mTo_words.getText().toString());
            if(mMax_time.getText().toString().isEmpty()){
                mWorkConfig.setTime_minute_five(0);
            }else{
                mWorkConfig.setTime_minute_five(Integer.parseInt(mMax_time.getText().toString()));
            }
            mWorkConfig.setType_five_state(true);

            mWorkConfig.setStart_time(mStart_time.getText().toString());
            mWorkConfig.setEnd_time(mEnd_time.getText().toString());
            mWorkConfig.setStartTimeMillis(MyTool.convertDateTimeToTimestamp(mStart_time.getText().toString()));
            mWorkConfig.setEndTimeMillis(MyTool.convertDateTimeToTimestamp(mEnd_time.getText().toString()));

            mWorkConfig.setMyLat(Double.parseDouble(mLat.getText().toString()));
            mWorkConfig.setMyLng(Double.parseDouble(mLng.getText().toString()));
            mWorkConfig.setDanjia_five(mDanjia.getText().toString().isEmpty()?0:Float.parseFloat(mDanjia.getText().toString()));


        }else{
            mWorkConfig.setType_five_state(false);
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private void difficute_t(){
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed()){
                    if(isChecked){
                        if(mMax_distance.getText().toString().isEmpty()||
                                mMin_money.getText().toString().isEmpty()||
                                mStart_time.getText().toString().isEmpty()||
                                mEnd_time.getText().toString().isEmpty()||
                                mLat.getText().toString().isEmpty()||
                                mLng.getText().toString().isEmpty()
                        ){
                            aSwitch.setChecked(false);
                            ToastUtil.showShortToast("必填栏位丢失");
                            return;
                        }
                        if(!mStart_time.getText().toString().isEmpty()){
                            if(MyTool.convertDateTimeToTimestamp(mStart_time.getText().toString())==0){
                                aSwitch.setChecked(false);
                                ToastUtil.showShortToast("时间范围格式错误");
                                return;
                            }
                        }
                        if(!mEnd_time.getText().toString().isEmpty()){
                            if(MyTool.convertDateTimeToTimestamp(mEnd_time.getText().toString())==0){
                                aSwitch.setChecked(false);
                                ToastUtil.showShortToast("时间范围格式错误");
                                return;
                            }
                        }
                    }
                }
            }
        });
        mMax_distance.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                mMax_distance.setCursorVisible(true);// 再次点击显示光标
            }
            return false;
        });
        mMin_money.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                mMin_money.setCursorVisible(true);// 再次点击显示光标
            }
            return false;
        });

        mFrom_words.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                mFrom_words.setCursorVisible(true);// 再次点击显示光标
            }
            return false;
        });
        mTo_words.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                mTo_words.setCursorVisible(true);// 再次点击显示光标
            }
            return false;
        });
        mMax_time.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                mMax_time.setCursorVisible(true);// 再次点击显示光标
            }
            return false;
        });

        mDanjia.setOnTouchListener((view, motionEvent) -> {
            if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                mDanjia.setCursorVisible(true);// 再次点击显示光标
            }
            return false;
        });
    }
    public void hide_gb(){
        if(mMax_time==null){
            return;
        }
        mMax_time.setCursorVisible(false);
        mMax_time.clearFocus();

        mMax_distance.setCursorVisible(false);
        mMax_distance.clearFocus();

        mMin_money.setCursorVisible(false);
        mMin_money.clearFocus();

        mFrom_words.setCursorVisible(false);
        mFrom_words.clearFocus();

        mTo_words.setCursorVisible(false);
        mTo_words.clearFocus();
        mDanjia.setCursorVisible(false);
        mDanjia.clearFocus();
    }
}
