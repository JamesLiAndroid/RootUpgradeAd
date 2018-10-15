package cn.ninesecond.zbh.practice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by 9second on 2018/10/11.
 */

public class Utils {
    public static final String downloadApkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/com.9second.bottlerecyclenew/apk";

    /**
     * 延时启动Activity
     *
     */
    public static boolean sleepToStartApp(String packageName, String mainActivityName) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            Process process = Runtime.getRuntime().exec("su");//申请root权限
            dataOutputStream = new DataOutputStream(process.getOutputStream());

            String command = "am start -n " + packageName + "/" + packageName + "." + mainActivityName + "\n";
            LogCook.d("sleepToStartApp", "runShellCommand :" + command);
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String message = "";
            String line;
            while ((line = errorStream.readLine()) != null) {
                message += line;
            }
            LogCook.d("sleepToStartApp", "runShellCommand result : " + message.toString());
            Log.e("sleepToStartApp", message);
            if (!message.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 静默安装
     * @param apkPath apk文件路径
     */
    public static boolean install(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            Process process = Runtime.getRuntime().exec("su");//申请root权限
            dataOutputStream = new DataOutputStream(process.getOutputStream());

            String command = "pm install -r " + apkPath + "\n";//拼接 pm install 命令，执行。-r表示若存在则覆盖安装
            LogCook.d("wenfeng", "runShellCommand :" + command);
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();//安装过程是同步的，安装完成后再读取结果
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String message = "";
            String line;
            while ((line = errorStream.readLine()) != null) {
                message += line;
            }
            LogCook.d("wenfeng", "runShellCommand result : " + message.toString());
            Log.e("silentInstall", message);
            if (!message.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 启动设备app
     * @param packageName
     * @param mainActivityName
     */
    public static void execLinuxCommand(String packageName, String mainActivityName){
        String cmd = "sleep 120; am start -n " + packageName + "/" + packageName + "." + mainActivityName;
        //Runtime对象
        Runtime runtime = Runtime.getRuntime();
        try {
            Process localProcess = runtime.exec("su");
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();
            LogCook.e("AFDASFS", "设备准备重启");
        } catch (IOException e) {
            LogCook.i("TAG", "strLine:"+e.getMessage());
            e.printStackTrace();

        }
    }

    /**
     * 判断手机是否安装某个应用
     * @param context
     * @param appPackageName  应用包名
     * @return   true：安装，false：未安装
     */
    public static boolean isApplicationAvilible(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }
}
