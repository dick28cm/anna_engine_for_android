package com.baidu.engine;

import android.app.Application;

import com.anna.engine.AnnaEngine;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.style.MaterialStyle;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.umcrash.UMCrash;
import com.umeng.umcrash.UMCrashCallback;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //预初始化下载引擎
        AnnaEngine.me().preInit(this, "AnnaEngine");
        //AnnaEngine.me().logEnable(true);

        // SDK预初始化函数
        // preInit预初始化函数耗时极少，不会影响App首次冷启动用户体验
        UMConfigure.preInit(this, null, null);

        /**
         *设置组件化的Log开关
         *参数: boolean 默认为false，如需查看LOG设置为true
         */
        //UMConfigure.setLogEnabled(true);

        //
        UMCrash.registerUMCrashCallback(new UMCrashCallback() {
            @Override
            public String onCallback() {
                return "AnnaEgine ";
            }
        });

        XXPermissions.setScopedStorage(true);

        DialogX.init(this);
        DialogX.implIMPLMode = DialogX.IMPL_MODE.VIEW;
        DialogX.useHaptic = true;
        DialogX.globalStyle = MaterialStyle.style();

        DialogX.globalTheme = DialogX.THEME.AUTO;
        DialogX.onlyOnePopTip = false;
    }
}
