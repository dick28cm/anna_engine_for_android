package com.baidu.engine.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anna.engine.AnnaEngine;
import com.anna.engine.DLL;
import com.anna.engine.MagnetCallback;
import com.baidu.engine.R;
import com.baidu.engine.databinding.ActivityMainBinding;
import com.baidu.engine.service.DownloadService;
import com.efs.sdk.launch.LaunchManager;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialogx.dialogs.BottomDialog;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.umcrash.UMCrash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.VideoInfo;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "AnnaEngine";
    private ActivityMainBinding binding;
    private DownloadService downloadService;
    private long mExitTime;

    private RecyclerView downingRecyclerView;
    private RecyclerAdapter downingRecyclerAdapter;
    private EditText tvUrl;

    //显示anction菜单
    public void showActionSheet(int sta, String taskId_) {
        BottomMenu.show(new String[]{"下载", "暂停", "删除", "边下边播"})
                //.setMessage("操作")
                .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                    @Override
                    public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                        try {
                            if (index == 0) {
                                int ret = AnnaEngine.me().resumeDownload(taskId_);
                                AnnaEngine.me().validateRetCode(ret);
                                Toast.makeText(com.baidu.engine.activity.MainActivity.this, "开始任务", Toast.LENGTH_SHORT).show();
                            } else if (index == 1) {
                                int ret = AnnaEngine.me().pauseDownload(taskId_);
                                AnnaEngine.me().validateRetCode(ret);
                                Toast.makeText(com.baidu.engine.activity.MainActivity.this, "暂停任务", Toast.LENGTH_SHORT).show();
                            } else if (index == 2) {
                                int ret = AnnaEngine.me().removeDownload(taskId_, true);
                                AnnaEngine.me().validateRetCode(ret);
                                Toast.makeText(com.baidu.engine.activity.MainActivity.this, "删除任务", Toast.LENGTH_SHORT).show();
                            } else if (index == 3) {
                                String playUrl = AnnaEngine.me().getPlayUrl(taskId_);
                                //standalone player
                                //playUrl = "https://cn-videos.dji.net/video_trans/9cb475a3e4224d4895e7b46a1b2b073a/720.mp4";
                                VideoInfo videoInfo = new VideoInfo(playUrl)
                                        //.setTitle("test video") //config title
                                        .setAspectRatio(VideoInfo.AR_MATCH_PARENT) //aspectRatio
                                        .setShowTopBar(true) //show mediacontroller top bar
                                        //.setPortraitWhenFullScreen(false);//portrait when full screen
                                        .setFullScreenAnimation(false)
                                        .setFullScreenOnly(true);

                                GiraffePlayer.play(getApplicationContext(), videoInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(com.baidu.engine.activity.MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //初始化RecyclerView
        downingRecyclerView = findViewById(R.id.downloadingTasks);
        //线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        downingRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        downingRecyclerAdapter = new RecyclerAdapter(this);
        downingRecyclerView.setAdapter(downingRecyclerAdapter);

        //setTitle(AnnaEngine.me().version());
        requetPermission();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        //Log.i(TAG, "MainActivity.onServiceConnected");
        DownloadService.Binder binder = (DownloadService.Binder) service;
        downloadService = binder.getService();
        downloadService.setCallback(downingRecyclerAdapter.getHandler());
        downloadService.setTaskObserve(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //Log.i(TAG, "MainActivity.onServiceDisconnected");
        downloadService = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "MainActivity.onStart");
        //
        if (downloadService == null) {
            Intent mForegroundService = new Intent(com.baidu.engine.activity.MainActivity.this, DownloadService.class);
            bindService(mForegroundService, com.baidu.engine.activity.MainActivity.this, BIND_AUTO_CREATE);
        } else {
            downloadService.setTaskObserve(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "MainActivity.onStop");
        //
        //unbindService(MainActivity.this);
        //
        downloadService.setTaskObserve(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add://监听菜单按钮
            {
                BottomDialog.show(new OnBindView<BottomDialog>(R.layout.dialog_input_url) {
                    @Override
                    public void onBind(final BottomDialog dialog, View v) {
                        TextView btnAdd = v.findViewById(R.id.btnAdd);
                        tvUrl = v.findViewById(R.id.editUrl);
                        btnAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = tvUrl.getText().toString();
                                if ("".equals(url)) {
                                    PopTip.show("输入不能为空!");
                                    return;
                                }
                                if (!(url.startsWith("http:") || url.startsWith("https:") || url.startsWith("ed2k:") || url.startsWith("magnet:"))) {
                                    PopTip.show("输入正确的链接!");
                                    return;
                                }
                                //
                                if (url.startsWith("http:") || url.startsWith("https:")) {
                                    dialog.dismiss();
                                    Map<String, String> headers = new HashMap<>();
                                    try {
                                        AnnaEngine.me().addUri(url, headers);
                                        PopTip.show("添加http链接成功!");
                                    } catch (Exception e) {
                                        PopTip.show("添加http失败:" + e.getMessage());
                                        UMCrash.generateCustomLog(e, "UmengException");
                                    }
                                } else if (url.startsWith("ed2k:")) {
                                    dialog.dismiss();
                                    try {
                                        AnnaEngine.me().addEmule(url);
                                        PopTip.show("添加emule链接成功!");
                                    } catch (Exception e) {
                                        PopTip.show("添加emule失败:" + e.getMessage());
                                        UMCrash.generateCustomLog(e, "UmengException");
                                    }
                                } else if (url.startsWith("magnet:")) {
                                    dialog.dismiss();
                                    LaunchManager.onTraceBegin(com.baidu.engine.activity.MainActivity.this, "parseMagnet", System.currentTimeMillis());
                                    AnnaEngine.me().parseMagnet(url, new MagnetCallback() {
                                        @Override
                                        public void callback(int code, DLL.MagInfo data) {
                                            LaunchManager.onTraceEnd(com.baidu.engine.activity.MainActivity.this, "parseMagnet", System.currentTimeMillis());
                                            if (code <= 0) {
                                                PopTip.show("解析磁力失败");
                                                return;
                                            }
                                            //显示磁力文件
                                            showMagnetFile(data);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).setAllowInterceptTouch(false);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    //转换文件大小
    public String toFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    //偷懒用对话框显示算了.
    private void showMagnetFile(DLL.MagInfo data) {
        //解析文件
        String[] fileList = new String[data.getFilesCount()];
        for (int i = 0; i < data.getFilesCount(); i++) {
            DLL.MagFile f = data.getFiles(i);
            StringBuffer sb = new StringBuffer();
            sb.append(f.getPath());
            sb.append("(");
            sb.append(toFileSize(f.getLength()));
            sb.append(")");
            fileList[i] = sb.toString();
        }

        //关闭键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        //显示底部按钮
        BottomMenu.show(fileList)
                .setTitle(data.getName())
                .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                    @Override
                    public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                        try {
                            DLL.MagFile file = data.getFiles(index);
                            AnnaEngine.me().addMagnet(data.getInfohash(), (int) file.getIndex(), file.getPath());
                            PopTip.show("添加磁力任务成功");
                        } catch (Exception e) {
                            PopTip.show("添加磁力失败:" + e.getMessage());
                            UMCrash.generateCustomLog(e, "UmengException");
                        }
                        return false;
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出",
                        Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //请求权限
    private void requetPermission() {
        XXPermissions.with(this)
                // 申请多个权限
                .permission(Permission.Group.STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        //同时包含读写
                        if (permissions.contains(Permission.WRITE_EXTERNAL_STORAGE) &&
                                permissions.contains(Permission.READ_EXTERNAL_STORAGE)) {

                            //友盟初始化
                            UMConfigure.init(getApplicationContext(), null, null, UMConfigure.DEVICE_TYPE_PHONE, null);

                            //初始化
                            try {
                                //
                                LaunchManager.onTraceBegin(getApplicationContext(), "tryInitEngine", System.currentTimeMillis());
                                //
                                AnnaEngine.me().tryInit(getApplicationContext());
                                Toast.makeText(getApplicationContext(), "引擎初始化成功...",
                                        Toast.LENGTH_SHORT).show();
                                //
                                LaunchManager.onTraceEnd(getApplicationContext(), "tryInitEngine", System.currentTimeMillis());
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                UMCrash.generateCustomLog(e, "UmengException");
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请给予外部储存器读写权限,否则app无法正常运行",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(getApplicationContext(), "被永久拒绝授权，请手动授予外部储存器读写权限",
                                    Toast.LENGTH_SHORT).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(com.baidu.engine.activity.MainActivity.this, permissions);
                        } else {
                            Toast.makeText(getApplicationContext(), "获取外部储存器读写权限失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}