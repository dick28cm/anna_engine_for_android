package com.baidu.engine.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.anna.engine.AnnaEngine;
import com.anna.engine.ProtoCallback;
import com.baidu.engine.R;
import com.baidu.engine.activity.MainActivity;

public class DownloadService extends Service implements Runnable, ProtoCallback {

    private static final String TAG = "AnnaEngine";
    private static final int NOTIFICATION_ID = 1989;
    private Thread taskThread;
    private boolean running;
    private Handler handler;
    private boolean taskObserve;

    public class Binder extends android.os.Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Log.i(TAG, "DownloadService.onBind");
        return new Binder();
    }

    @Override
    public void onCreate() {
        //Log.i(TAG, "DownloadService.onCreate");
        super.onCreate();
        // 获取服务通知
        Notification notification = createForegroundNotification();
        //将服务置于启动状态 ,NOTIFICATION_ID指的是创建的通知的ID
        startForeground(NOTIFICATION_ID, notification);

        //开始线程
        running = true;
        taskThread = new Thread(this);
        taskThread.start();
    }


    /**
     * 回调消息
     *
     * @param handler_
     */
    public void setCallback(Handler handler_) {
        this.handler = handler_;
    }

    /**
     * 是否观察
     *
     * @param ob
     */
    public void setTaskObserve(boolean ob) {
        this.taskObserve = ob;
    }

    @Override
    public void run() {
        while (running) {
            try {
                //判断是否观察
                if (this.taskObserve) {
                    //获取正在下载的任务,并且返回回调给activity
                    int retCode = AnnaEngine.me().getDownloadingTasks(this);
                    if (retCode <= 0) {
                        Message msg = handler.obtainMessage(2, retCode);
                        handler.sendMessage(msg);
                    }
                }
                //暂停1秒
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e(TAG, "DownloadService.run exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void callback(byte[] data) {
        if (this.handler != null) {
            Message msg = handler.obtainMessage(1, data);
            handler.sendMessage(msg);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Log.i(TAG, "DownloadService.onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "DownloadService.onDestroy");
        super.onDestroy();
        // 移除通知
        stopForeground(true);
        //
        try {
            running = false;
            taskThread.join(1000);
        } catch (InterruptedException e) {
            Log.e(TAG, "DownloadService.onDestroy exception: " + e.getMessage());
        }
    }

    /**
     * 创建服务通知
     */
    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 唯一的通知通道的id.
        String notificationChannelId = "anna_engine_channel_id_01";

        // Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            String channelName = "安娜引擎";
            //通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, channelName, importance);
            notificationChannel.setDescription("高速下载引擎运行中");
            //LED灯
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            //震动
            notificationChannel.setVibrationPattern(new long[]{200, 200, 200, 200});
            notificationChannel.enableVibration(true);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationChannelId);
        //通知小图标
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        //通知标题
        builder.setContentTitle("安娜引擎");
        //通知内容
        builder.setContentText("高速下载引擎运行中");
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        // 设置通知的优先级
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        //设定启动的内容
        Intent activityIntent = new Intent(this, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0); //PendingIntent.FLAG_UPDATE_CURRENT
        builder.setContentIntent(pendingIntent);

        //创建通知并返回
        return builder.build();
    }
}
