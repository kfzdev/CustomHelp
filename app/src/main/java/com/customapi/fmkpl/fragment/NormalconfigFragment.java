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
import com.customapi.fmkpl.tool.ToastUtil;

public class NormalconfigFragment extends Fragment {
    private int type = 0;
    private EditText mMax_distance;
    private EditText mMin_money;
    private EditText mFrom_words;
    private EditText mTo_words;
    private EditText mMax_time;
    private EditText mDanjia;
    private Switch aSwitch;
    private WorkConfig mWorkConfig;
    public NormalconfigFragment(int type,WorkConfig workConfig){
        this.type = type;
        mWorkConfig = workConfig;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normalconfig, container,false);
        mMax_distance = view.findViewById(R.id.max_distance);
        mMin_money = view.findViewById(R.id.min_money);
        mFrom_words = view.findViewById(R.id.from_words);
        mTo_words = view.findViewById(R.id.to_words);
        mMax_time = view.findViewById(R.id.max_time);
        mDanjia = view.findViewById(R.id.danjia);
        aSwitch = view.findViewById(R.id.config_state);
        difficute_t();
        setLocalValue();
        return view;
    }
    private void setLocalValue(){
        if(type==1){
            if(mWorkConfig.getTime_minute_one()>0){
                mMax_time.setText(mWorkConfig.getTime_minute_one()+"");
            }
            mMax_distance.setText(mWorkConfig.getMax_distance_one()>0?mWorkConfig.getMax_distance_one()+"":"");
            mMin_money.setText(mWorkConfig.getMin_money_one()>0?mWorkConfig.getMin_money_one()+"":"");
            mFrom_words.setText(mWorkConfig.getFrom_place_one());
            mTo_words.setText(mWorkConfig.getTo_place_one());
            mDanjia.setText(mWorkConfig.getDanjia_one()>0?mWorkConfig.getDanjia_one()+"":"");
            aSwitch.setChecked(mWorkConfig.isType_one_state());



        }else if(type==2){
            if(mWorkConfig.getTime_minute_two()>0){
                mMax_time.setText(mWorkConfig.getTime_minute_two()+"");
            }
            mMax_distance.setText(mWorkConfig.getMax_distance_two()>0?mWorkConfig.getMax_distance_two()+"":"");
            mMin_money.setText(mWorkConfig.getMin_money_two()>0?mWorkConfig.getMin_money_two()+"":"");
            mFrom_words.setText(mWorkConfig.getFrom_place_two());
            mTo_words.setText(mWorkConfig.getTo_place_two());
            mDanjia.setText(mWorkConfig.getDanjia_two()>0?mWorkConfig.getDanjia_two()+"":"");
            aSwitch.setChecked(mWorkConfig.isType_two_state());

        }else if(type==3){
            if(mWorkConfig.getTime_minute_three()>0){
                mMax_time.setText(mWorkConfig.getTime_minute_three()+"");
            }
            mMax_distance.setText(mWorkConfig.getMax_distance_three()>0?mWorkConfig.getMax_distance_three()+"":"");
            mMin_money.setText(mWorkConfig.getMin_money_three()>0?mWorkConfig.getMin_money_three()+"":"");
            mFrom_words.setText(mWorkConfig.getFrom_place_three());
            mTo_words.setText(mWorkConfig.getTo_place_three());
            mDanjia.setText(mWorkConfig.getDanjia_three()>0?mWorkConfig.getDanjia_three()+"":"");
            aSwitch.setChecked(mWorkConfig.isType_three_state());

        }else if(type==4){
            if(mWorkConfig.getTime_minute_four()>0){
                mMax_time.setText(mWorkConfig.getTime_minute_four()+"");
            }
            mMax_distance.setText(mWorkConfig.getMax_distance_four()>0?mWorkConfig.getMax_distance_four()+"":"");
            mMin_money.setText(mWorkConfig.getMin_money_four()>0?mWorkConfig.getMin_money_four()+"":"");
            mFrom_words.setText(mWorkConfig.getFrom_place_four());
            mTo_words.setText(mWorkConfig.getTo_place_four());
            mDanjia.setText(mWorkConfig.getDanjia_four()>0?mWorkConfig.getDanjia_four()+"":"");
            aSwitch.setChecked(mWorkConfig.isType_four_state());
        }
    }
    public void setWorkConfig(WorkConfig mWorkConfig){
        if(aSwitch!=null&&aSwitch.isChecked()){
            if(type==1){
                mWorkConfig.setMax_distance_one(Float.parseFloat(mMax_distance.getText().toString()));
                mWorkConfig.setMin_money_one(Float.parseFloat(mMin_money.getText().toString()));
                mWorkConfig.setFrom_place_one(mFrom_words.getText().toString());
                mWorkConfig.setTo_place_one(mTo_words.getText().toString());
                if(mMax_time.getText().toString().isEmpty()){
                    mWorkConfig.setTime_minute_one(0);
                }else{
                    mWorkConfig.setTime_minute_one(Integer.parseInt(mMax_time.getText().toString()));
                }
                mWorkConfig.setType_one_state(true);
                mWorkConfig.setDanjia_one(mDanjia.getText().toString().isEmpty()?0:Float.parseFloat(mDanjia.getText().toString()));


            }else if(type==2){
                mWorkConfig.setMax_distance_two(Float.parseFloat(mMax_distance.getText().toString()));
                mWorkConfig.setMin_money_two(Float.parseFloat(mMin_money.getText().toString()));
                mWorkConfig.setFrom_place_two(mFrom_words.getText().toString());
                mWorkConfig.setTo_place_two(mTo_words.getText().toString());
                if(mMax_time.getText().toString().isEmpty()){
                    mWorkConfig.setTime_minute_two(0);
                }else{
                    mWorkConfig.setTime_minute_two(Integer.parseInt(mMax_time.getText().toString()));
                }
                mWorkConfig.setType_two_state(true);
                mWorkConfig.setDanjia_two(mDanjia.getText().toString().isEmpty()?0:Float.parseFloat(mDanjia.getText().toString()));

            }else if(type==3){
                mWorkConfig.setMax_distance_three(Float.parseFloat(mMax_distance.getText().toString()));
                mWorkConfig.setMin_money_three(Float.parseFloat(mMin_money.getText().toString()));
                mWorkConfig.setFrom_place_three(mFrom_words.getText().toString());
                mWorkConfig.setTo_place_three(mTo_words.getText().toString());
                if(mMax_time.getText().toString().isEmpty()){
                    mWorkConfig.setTime_minute_three(0);
                }else{
                    mWorkConfig.setTime_minute_three(Integer.parseInt(mMax_time.getText().toString()));
                }
                mWorkConfig.setType_three_state(true);
                mWorkConfig.setDanjia_three(mDanjia.getText().toString().isEmpty()?0:Float.parseFloat(mDanjia.getText().toString()));

            }else if(type==4){
                mWorkConfig.setMax_distance_four(Float.parseFloat(mMax_distance.getText().toString()));
                mWorkConfig.setMin_money_four(Float.parseFloat(mMin_money.getText().toString()));
                mWorkConfig.setFrom_place_four(mFrom_words.getText().toString());
                mWorkConfig.setTo_place_four(mTo_words.getText().toString());
                if(mMax_time.getText().toString().isEmpty()){
                    mWorkConfig.setTime_minute_four(0);
                }else{
                    mWorkConfig.setTime_minute_four(Integer.parseInt(mMax_time.getText().toString()));
                }
                mWorkConfig.setType_four_state(true);
                mWorkConfig.setDanjia_four(mDanjia.getText().toString().isEmpty()?0:Float.parseFloat(mDanjia.getText().toString()));
            }
        }else{
            if(type==1){
                mWorkConfig.setType_one_state(false);


            }else if(type==2){
                mWorkConfig.setType_two_state(false);

            }else if(type==3){
                mWorkConfig.setType_three_state(false);

            }else if(type==4){
                mWorkConfig.setType_four_state(false);
            }
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
                                mMax_time.getText().toString().isEmpty()
                        ){
                            aSwitch.setChecked(false);
                            ToastUtil.showShortToast("必填栏位丢失");
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
