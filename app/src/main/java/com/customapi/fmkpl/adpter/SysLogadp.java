package com.customapi.fmkpl.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.customapi.fmkpl.R;
import com.customapi.fmkpl.tool.MyTool;

import java.util.ArrayList;
import java.util.List;

public class SysLogadp extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private List<String> mSysLog = new ArrayList<>();
    public SysLogadp(Context context , List<String> qrcode_datas){
        mContext = context;
        mSysLog = qrcode_datas;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mSysLog.size();
    }

    @Override
    public Object getItem(int position) {
        return mSysLog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //返回一个视图

        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_syslog,null);
            viewHolder = new ViewHolder();
            viewHolder.itemTextView = (TextView) convertView.findViewById(R.id.syslogtext);
            viewHolder.numberTextView = (TextView) convertView.findViewById(R.id.number);
            viewHolder.datetimeTextView = (TextView) convertView.findViewById(R.id.datetime);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.numberTextView.setText((position+1)+".");
        viewHolder.itemTextView.setText(mSysLog.get(position));
        viewHolder.datetimeTextView.setText(MyTool.getNowDate());
        return convertView;
    }

    class ViewHolder{
        TextView itemTextView;
        TextView numberTextView;
        TextView datetimeTextView;

    }

    public void refreshData(List<String> qrcode_datas){
        mSysLog = qrcode_datas;
        notifyDataSetChanged();
    }
}
