package cn.ninesecond.zbh;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import cn.ninesecond.zbh.practice.LogCook;
import cn.ninesecond.zbh.practice.Utils;


public class MainActivity extends AppCompatActivity {

    public static final String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/com.9second.bottlerecyclenew/log";
    public static final String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/com.9second.bottlerecyclenew/apk";
    public static final String apkName = "reinstall_plugin.apk";

    public static final String apkPackageName = "com.ninesecond.zbh.installplugin";
    // public static final String targetPackageName = "com.ninesecond.zbh.installplugin";

    ProgressDialog progressDialog;

    ReInstallAutoStartReceiver receiver;
    ReinstallReceiver receiver2;
    ReinstallGETReceiver receiver3;
    IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_slient_install_plugin_apk = findViewById(R.id.btn_slient_install_apk);
        btn_slient_install_plugin_apk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // app是否存在，存在即不再进行安装
                if (!Utils.isApplicationAvilible(MainActivity.this, apkPackageName)) {

                    AssetManager assetManager = getAssets();

                    InputStream in = null;
                    OutputStream out = null;

                    try {
                        in = assetManager.open("reinstall_plugin.apk");
                        File filePath = new File(apkPath);
                        if (!filePath.exists()) {
                            filePath.mkdirs();
                        }
                        File file = new File(apkPath, apkName);
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
                        LogCook.d("TAG", "写入异常。。。。。");
                    }

                    // 静默安装
                    Utils.install(apkPath + "/" + apkName);
                    // 启动apk
                    Utils.sleepToStartApp("com.ninesecond.zbh.installplugin", "MainActivity");
                } else {
                    // 启动apk
                    Utils.sleepToStartApp("com.ninesecond.zbh.installplugin", "MainActivity");
                }
            }
        });

        LogCook.getInstance() // 单例获取LogCook实例
                .setLogPath(logPath) //设置日志保存路径
                //              .setErrorLogPath(errorLogPath) // 设置错误日志路径
                .setLogName("main.log") //设置日志文件名，如果将来定时开关机，需要更改为按日期存储的
                .isOpen(true)  //是否开启输出日志
                .isSave(true)  //是否保存日志
                .initialize(); //完成初始化Crash监听
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播接收者
        receiver = new ReInstallAutoStartReceiver();
        intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        registerReceiver(receiver, intentFilter);

        receiver3 = new ReinstallGETReceiver();
        intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REPLACED);
        registerReceiver(receiver3, intentFilter);

        receiver2 = new ReinstallReceiver();
        intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        registerReceiver(receiver2, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(receiver2);
        unregisterReceiver(receiver3);
    }

    private class ReInstallAutoStartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //  if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            LogCook.d("TAF", "已经重新安装。。。。。。");
            //PackageManager manager = context.getPackageManager();
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogCook.d("TAF", "ACTION_PACKAGE_ADDED。。。。。。" + packageName
                        + ":::::::" + intent.getDataString());
                Toast.makeText(MainActivity.this, "ACTION_PACKAGE_ADDED:" + packageName, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ReinstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogCook.d("TAF", "已经重新安装。。。。。。");
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogCook.d("TAF", "ACTION_PACKAGE_REMOVED。。。。。。" + packageName
                        + ":::::::" + intent.getDataString());
                Toast.makeText(MainActivity.this, "ACTION_PACKAGE_REMOVED:" + packageName, Toast.LENGTH_LONG).show();
                Utils.execLinuxCommand("com.ninesecond.zbh.installplugin", "MainActivity");
            }
        }
    }

    private class ReinstallGETReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogCook.d("TAF", "已经重新安装。。。。。。");
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogCook.d("TAF", "ACTION_PACKAGE_REPLACED包名。。。。。。" + packageName
                        + ":::::::" + intent.getDataString());
                Toast.makeText(MainActivity.this, "ACTION_PACKAGE_REPLACED:" + packageName, Toast.LENGTH_LONG).show();
            }
        }
    }
}
