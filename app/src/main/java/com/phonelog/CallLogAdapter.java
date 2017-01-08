package com.phonelog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * 通话记录Adapter
 * Created by fan on 2016/8/9.
 */
public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<CallInfoLog> callInfoLogs;
    private LayoutInflater mInflater;
    private CallLogItemClickListener callLogItemClickListener;
    private EditItemClickListener editClickListener;

    public CallLogAdapter(Context context, ArrayList<CallInfoLog> lists) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.callInfoLogs = lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View view = mInflater.inflate(R.layout.item_calllog_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CallInfoLog callInfoLog = callInfoLogs.get(position);
        //名字
        if (!TextUtils.isEmpty(callInfoLog.getName())) {
            holder.tvName.setText(callInfoLog.getName());
        } else if (!TextUtils.isEmpty(callInfoLog.getNumber())) {
            holder.tvName.setText(callInfoLog.getNumber());
        } else {
            holder.tvName.setText("未知");
        }
        //手机号
        if (!TextUtils.isEmpty(callInfoLog.getNumber())) {
            holder.tvNumber.setText(callInfoLog.getNumber());
        } else {
            holder.tvNumber.setText("未知");
        }
        //时间
        formatData(callInfoLog.getDate(), holder.tvTime);
        //通话时长
        setCallDura(callInfoLog.getDuration(), holder.tvDuration);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callLogItemClickListener) {
                    callLogItemClickListener.itemClick(position);
                }
            }
        });
        //数量
        if (callInfoLog.getCountType() > 1) {
            holder.tvCount.setText(callInfoLog.getCountType() + "");
        } else {
            holder.tvCount.setText("");
        }
        //通话类型
        if (callInfoLog.getType() == 1) {
            holder.tvType.setText("来电");
        } else if (callInfoLog.getType() == 2) {
            holder.tvType.setText("去电");

        } else if (callInfoLog.getType() == 3) {
            holder.tvType.setText("未接");
        }
    }


    @Override
    public int getItemCount() {
        return callInfoLogs.size();
    }

    /**
     * 设置条目点击事件
     */
    public void setOnItemClickListener(CallLogItemClickListener callLogItemClickListener) {
        this.callLogItemClickListener = callLogItemClickListener;
    }

    /**
     * 设置编辑按钮点击事件
     */
    public void setEditClickListener(EditItemClickListener editClickListener) {
        this.editClickListener = editClickListener;
    }

    public interface CallLogItemClickListener {
        void itemClick(int position);
    }

    public interface EditItemClickListener {
        void editClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;//名字
        private final TextView tvNumber;//来电号码
        private final TextView tvTime;//通话具体时间
        private final TextView tvDuration;//通话时长
        private final TextView tvCount;//通话数量
        private final TextView tvType;//通话类型

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvDuration = (TextView) itemView.findViewById(R.id.tv_duration);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }

    /**
     * 通话时长
     *
     * @param duration
     * @param tvDuration
     */
    private void setCallDura(String duration, TextView tvDuration) {
        if ("0".equals(duration)) {
            tvDuration.setText("");
        } else {
            int totalSecond = Integer.parseInt(duration);
//            totalSecond /= 1000;
            int minute = totalSecond / 60;
            int hour = minute / 60;
            int second = totalSecond % 60;
            minute = minute % 60;
            String callTime;
            if (hour > 0) {
                callTime = String.format("%02d:%02d:%02d", hour, minute, second);
            } else {
                callTime = String.format("%02d:%02d", minute, second);
            }
            if ("00:00".equals(callTime) || "00:00:00".equals(callTime)) {
                tvDuration.setText("");
            } else {
                tvDuration.setText("通话" + callTime + "秒");
            }
        }
    }

    /**
     * 通话具体时间
     *
     * @param time
     * @param tvTime
     */
    private void formatData(long time, TextView tvTime) {
        //获取拨打电话的日期
        String callDate = TransitionTime.getDate(time);
        //获取今天的日期
        String todayData = TransitionTime.getTodayData();
        //获取昨天的日期
        String yesData = TransitionTime.getYesData();

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < (60 * 1000)) {//一分钟
            //显示刚刚
            tvTime.setText("刚刚");
        } else if (currentTimeMillis - time < (60 * 1000 * 60)) {//一小时
            //显示一小时内
            long mint = (currentTimeMillis - time) / (60 * 1000);
            tvTime.setText(mint + "分钟前");
        } else if (todayData.equals(callDate)) {//一天
            // 月日 : 时分
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");
            tvTime.setText(f.format(time));
        } else if (yesData.equals(callDate)) {//昨天
            //显示昨天
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");
            tvTime.setText("昨天" + f.format(time));
        } else {
            // 月日 : 时分
            tvTime.setText(callDate);
        }
    }
}
