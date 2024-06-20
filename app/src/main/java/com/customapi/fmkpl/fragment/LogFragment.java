package com.customapi.fmkpl.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.customapi.fmkpl.R;
import com.customapi.fmkpl.adpter.LogAdapter;
import com.customapi.fmkpl.bean.LogDetail;
import com.customapi.fmkpl.tool.MyTool;

import java.util.ArrayList;

public class LogFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LogAdapter mLogAdapter;
//    private ListView mSyslogs;
    private ArrayList<LogDetail> log_list = new ArrayList<>();
//    private SysLogadp mSysLogadp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container,false);

        mRecyclerView = view.findViewById(R.id.syslog);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mLogAdapter = new LogAdapter(getContext(),log_list);
        mRecyclerView.setAdapter(mLogAdapter);
        return view;
    }
    public void celanList(){

        log_list.clear();
        if(mLogAdapter!=null){
            mLogAdapter.refreshData(log_list);
        }
    }
    public void updateView(String text){
        log_list.add(new LogDetail(log_list.size()+1, MyTool.getNowDate(),text));
        if(mLogAdapter!=null){
            mLogAdapter.refreshData(log_list);
            mRecyclerView.scrollToPosition(log_list.size()-1);
        }
    }
}
