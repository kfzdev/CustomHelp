package com.customapi.fmkpl.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.customapi.fmkpl.R;
import com.customapi.fmkpl.bean.LogDetail;

import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private Context context;
    private List<LogDetail> mSysLog = new ArrayList<>();

    public LogAdapter(Context context,List<LogDetail> logs) {
        this.context = context;
        mSysLog = logs;
    }
    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogViewHolder(LayoutInflater.from(context).inflate(R.layout.item_syslog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
//        holder.numberTextView.setText((position+1)+".");
        holder.itemTextView.setText(mSysLog.get(position).getText());
        holder.datetimeTextView.setText(mSysLog.get(position).getDatetime());
    }

    @Override
    public int getItemCount() {
        return mSysLog.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        TextView numberTextView;
        TextView datetimeTextView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = (TextView) itemView.findViewById(R.id.syslogtext);
            numberTextView = (TextView) itemView.findViewById(R.id.number);
            datetimeTextView = (TextView) itemView.findViewById(R.id.datetime);
        }
    }
    public void refreshData(List<LogDetail> logs){
        mSysLog = logs;
        notifyDataSetChanged();
    }
}
