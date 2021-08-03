package com.baidu.engine.activity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anna.engine.DLL;
import com.baidu.engine.R;
import com.baidu.engine.activity.MainActivity;

import java.text.DecimalFormat;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AnnaEngine";

    private MainActivity mContext;

    private DLL.TaskList downingTasklist;

    //消息回调
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //反序列化pb
            if (msg.what == 1) {
                try {
                    byte[] data = (byte[]) msg.obj;
                    downingTasklist = DLL.TaskList.parseFrom(data);
                    RecyclerAdapter.this.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.i(TAG, "RecyclerAdapter.handleMessage parse protobuf exception: " + e.getMessage());
                }
            } else if (msg.what == 2) {
                int retCode = (int) msg.obj;
                if (retCode == 0) {
                    downingTasklist = null;
                    RecyclerAdapter.this.notifyDataSetChanged();
                }
            }
        }
    };

    public Handler getHandler() {
        return this.handler;
    }

    public RecyclerAdapter(MainActivity context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.task_item, parent, false);
        return new DowningTaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DowningTaskHolder normalHolder = (DowningTaskHolder) holder;

        if (downingTasklist == null) return;
        //填充数据
        DLL.Task taskInfo = downingTasklist.getTasks(position);
        normalHolder.tvTaskId.setText(taskInfo.getId());
        normalHolder.tvTaskName.setText(taskInfo.getName());
        normalHolder.tvTaskStatusValue.setText(taskInfo.getStatusText());
        normalHolder.tvTaskSta.setText(taskInfo.getTaskStatus().getNumber() + "");
        normalHolder.tvDownSizeValue.setText(taskInfo.getDownloadSize());
        int downProgress = (int) taskInfo.getDownloadPercentage();
        normalHolder.pgDownProgress.setProgress(downProgress);
        normalHolder.tvDownPlayValue.setText(taskInfo.getDownloadPlayInited() ? "已准备" : "未就绪");

        if (taskInfo.getTaskStatus() == DLL.TaskSta.DOWNLOAD) {
            normalHolder.llExtInfo.setVisibility(View.VISIBLE);
            normalHolder.tvSpeedValue.setText(taskInfo.getDownloadSpeed());
            normalHolder.tvReminTimeValue.setText(taskInfo.getDownloadRemainingTime());
            DecimalFormat df1 = new DecimalFormat("#.00");
            normalHolder.tvFileHealthValue.setText(String.format("%s%%", df1.format(taskInfo.getFileHealth())));
            normalHolder.tvPeerCountValue.setText(String.format("%d", taskInfo.getRunningPeerCount()));
        } else {
            normalHolder.llExtInfo.setVisibility(View.GONE);
            normalHolder.tvSpeedValue.setText("");
            normalHolder.tvReminTimeValue.setText("");
        }
    }

    @Override
    public int getItemCount() {
        if (downingTasklist != null)
            return downingTasklist.getTasksCount();
        return 0;
    }

    //任务条目
    public class DowningTaskHolder extends RecyclerView.ViewHolder {

        public TextView tvTaskId;
        public TextView tvTaskSta;
        public TextView tvTaskName;
        public TextView tvTaskStatusValue;
        public TextView tvDownSizeValue;
        public TextView tvSpeedValue;
        public TextView tvReminTimeValue;
        public TextView tvFileHealthValue;
        public TextView tvPeerCountValue;
        public TextView tvDownPlayValue;
        public ProgressBar pgDownProgress;

        public LinearLayout llTaskItem;
        public LinearLayout llExtInfo;

        public DowningTaskHolder(@NonNull View itemView) {
            super(itemView);

            llTaskItem = itemView.findViewById(R.id.llTaskItem);
            llTaskItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int sta = Integer.parseInt(tvTaskSta.getText().toString());
                    mContext.showActionSheet(sta, tvTaskId.getText().toString());
                }
            });
            llExtInfo = itemView.findViewById(R.id.llExtInfo);
            //
            tvTaskId = itemView.findViewById(R.id.tvTaskId);
            tvTaskSta = itemView.findViewById(R.id.tvTaskSta);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskStatusValue = itemView.findViewById(R.id.tvTaskStatusValue);
            tvDownSizeValue = itemView.findViewById(R.id.tvDownSizeValue);
            tvSpeedValue = itemView.findViewById(R.id.tvSpeedValue);
            tvReminTimeValue = itemView.findViewById(R.id.tvReminTimeValue);
            tvFileHealthValue = itemView.findViewById(R.id.tvFileHealthValue);
            tvPeerCountValue = itemView.findViewById(R.id.tvPeerCountValue);
            tvDownPlayValue = itemView.findViewById(R.id.tvDownPlayValue);
            pgDownProgress = itemView.findViewById(R.id.pgDownProgress);
        }
    }
}
