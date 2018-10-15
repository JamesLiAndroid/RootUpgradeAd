package cn.ninesecond.zbh.practice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Process;

import java.io.File;

/**
 * 覆盖安装后重启的代码
 */
public class ReInstallAutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            LogCook.d("TAF-------------------------", "已经重新安装。。。。。。");
            //PackageManager manager = context.getPackageManager();
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogCook.d("TAF-------------------------", "ACTION_PACKAGE_ADDED。。。。。。" + packageName
                        + ":::::::" + intent.getDataString());
                File apk = new File(context.getFilesDir() + "/" + packageName + ".apk");
                if (apk.exists()) {
                    apk.delete();
                }
            }
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogCook.d("TAF-------------------------", "ACTION_PACKAGE_REMOVED。。。。。。" + packageName
                        + ":::::::" + intent.getDataString());
                Utils.sleepToStartApp("com.ninesecond.zbh.installplugin", "MainActivity");
                Process.killProcess(Process.myPid());
            }
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogCook.d("TAF-------------------------", "ACTION_PACKAGE_REPLACED包名。。。。。。" + packageName
                        + ":::::::" + intent.getDataString());
                try {
                    if (intent.getDataString().contains(context.getPackageName())) {
                        Intent myIntent = new Intent();
                        LogCook.d("TAF-------------------------", "包名。。。。。。" + intent.getDataString());
                        PackageManager pm = context.getPackageManager();
                        try {
                            myIntent = pm.getLaunchIntentForPackage(intent.getDataString().substring(8));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(myIntent);
                        LogCook.d("TAF-------------------------", "启动appp。。。。。。");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
