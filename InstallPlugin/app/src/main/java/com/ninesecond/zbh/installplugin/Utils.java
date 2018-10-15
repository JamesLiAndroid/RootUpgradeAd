package com.ninesecond.zbh.installplugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by 9second on 2018/10/11.
 */

public class Utils {
    public static final String downloadApkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/com.9second.bottlerecyclenew/apk";

    /**
     * 执行Shell命令，用在静默安装app上
     * @param command "pm install -r  /sdcard/haha.apk"
     */
    public static void runShellCommand(String command) {
        Process process = null;
        BufferedReader bufferedReader = null;
        StringBuilder mShellCommandSB =new StringBuilder();
        LogCook.d("wenfeng", "runShellCommand :" + command);
        mShellCommandSB.delete(0, mShellCommandSB.length());
        String[] cmd = new String[] { "su", command }; //调用bin文件
        try {
            byte b[] = new byte[1024];
            process = Runtime.getRuntime().exec(cmd);
          //   String cmder = "/system/bin/sh" + "-c" + command;
            // process.getOutputStream().write(cmder.getBytes());
            bufferedReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                mShellCommandSB.append(line);
            }
            LogCook.d("wenfeng", "runShellCommand result : " + mShellCommandSB.toString());
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    //  handle exception
                    e.printStackTrace();
                }
            }

            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * 获取当前时间戳日期
     * @return
     */
    public static String getCurrTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
        dateFormat.applyPattern("yyyy-MM-dd_HH-mm-ss");//
        String date = dateFormat.format(System.currentTimeMillis());
        return date;
    }

    /**
     * 判断手机是否拥有Root权限。
     * @return 有root权限返回true，否则返回false。
     */
    public static boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

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
            if (!message.contains("Error")) {
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

    /*
　　@pararm apkPath 等待安装的app全路径，如：/sdcard/app/app.apk
**/
    public static boolean clientInstall(String apkPath) {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 " + apkPath);
            PrintWriter
                    .println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r " + apkPath);
            // PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            LogCook.e("sadfsadfasdf", "静默安装返回值："+value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogCook.e("sadfsadfasdf","安装apk出现异常");
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
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
