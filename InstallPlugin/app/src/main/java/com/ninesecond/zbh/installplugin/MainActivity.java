package com.ninesecond.zbh.installplugin;

import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    // 初始化日志信息
    public static final String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/com.a9second.zbh/log";

    public static final String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/com.9second.bottlerecyclenew/apk";
    public static final String targetApkName = "app-debug.apk";


//    ReInstallAutoStartReceiver receiver;
//    ReinstallReceiver receiver2;
//    ReinstallGETReceiver receiver3;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogCook.getInstance() // 单例获取LogCook实例
                .setLogPath(logPath) //设置日志保存路径
                //              .setErrorLogPath(errorLogPath) // 设置错误日志路径
                .setLogName("main.log") //设置日志文件名，如果将来定时开关机，需要更改为按日期存储的
                .isOpen(true)  //是否开启输出日志
                .isSave(true)  //是否保存日志
                .initialize(); //完成初始化Crash监听

        // 注册广播接收者
//        receiver = new ReInstallAutoStartReceiver();
//        intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
//        registerReceiver(receiver, intentFilter);
//
//        receiver3 = new ReinstallGETReceiver();
//        intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REPLACED);
//        registerReceiver(receiver3, intentFilter);
//
//        receiver2 = new ReinstallReceiver();
//        intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
//        registerReceiver(receiver2, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 模拟下载流程，将apk拷入sd卡
        Toast.makeText(MainActivity.this, "开始下载。。。", Toast.LENGTH_LONG).show();
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;

        try {
            in = assetManager.open("app-debug.apk");
            File filePath = new File(apkPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            File file = new File(apkPath, targetApkName);
            out = new FileOutputStream(file);

            byte[] buffer = new byte[1024];

            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
            LogCook.d("TAG", "拷贝：：：" + e.getMessage());
        }

        // 开始静默安装
        // 静默安装
        Toast.makeText(MainActivity.this, "开始静默安装！", Toast.LENGTH_LONG).show();
        boolean isInstalled = Utils.install(apkPath + "/" + targetApkName);
        if (isInstalled) {
            LogCook.d("TAG", "开始启动。。。");
            // 安装完成，启动
            Utils.sleepToStartApp("cn.ninesecond.zbh", "MainActivity");
            Process.killProcess(Process.myPid());
           /// finish();
        } else {
            // apk未安装
            Toast.makeText(MainActivity.this, "apk未安装！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(receiver);
//        unregisterReceiver(receiver2);
//        unregisterReceiver(receiver3);
    }

//    private class ReInstallAutoStartReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //  if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
//            LogCook.d("TAF", "已经重新安装。。。。。。");
//            //PackageManager manager = context.getPackageManager();
//            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
//                String packageName = intent.getData().getSchemeSpecificPart();
//                LogCook.d("TAF", "ACTION_PACKAGE_ADDED。。。。。。" + packageName
//                        + ":::::::" + intent.getDataString());
//                Toast.makeText(MainActivity.this, "ACTION_PACKAGE_ADDED:" + packageName, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private class ReinstallReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            LogCook.d("TAF", "已经重新安装。。。。。。");
//            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
//                String packageName = intent.getData().getSchemeSpecificPart();
//                LogCook.d("TAF", "ACTION_PACKAGE_REMOVED。。。。。。" + packageName
//                        + ":::::::" + intent.getDataString());
//                Toast.makeText(MainActivity.this, "ACTION_PACKAGE_REMOVED:" + packageName, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private class ReinstallGETReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            LogCook.d("TAF", "已经重新安装。。。。。。");
//            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
//                String packageName = intent.getData().getSchemeSpecificPart();
//                LogCook.d("TAF", "ACTION_PACKAGE_REPLACED包名。。。。。。" + packageName
//                        + ":::::::" + intent.getDataString());
//                Toast.makeText(MainActivity.this, "ACTION_PACKAGE_REPLACED:" + packageName, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}
