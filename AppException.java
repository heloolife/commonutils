package com.wdl.utils.test;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;

import com.wdl.utils.system.SystemTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 应用程序异常：用于捕获异常和提示错误信息
 */
public class AppException implements UncaughtExceptionHandler {
    private static String SAVE_PATH_FOLDER = "rrcx" + File.separator + "log";
    private static String SAVE_PATH_FILENAME = "crash.log";

    /**
     * 系统默认的UncaughtException处理类
     */
    private Context mContext;

    public static AppException instance;

    public static AppException getInstance() {
        if (instance == null) {
            instance = new AppException();
        }
        return instance;
    }

    public void init(Context ctx) {
        this.mContext = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex)) {
            System.exit(0);
        }
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     *
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null || mContext == null)
            return false;

        if (LogUtil.currentStage != LogUtil.RELEASE)
            saveToSDCard(ex);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        return true;
    }


    /**
     * 将异常信息保存到sd卡中
     *
     * @param ex
     * @return
     * @throws Exception
     */
    private void saveToSDCard(Throwable ex) {
        PrintWriter pw = null;
        try {
            boolean append = false;
            File saveSDFile = FileUtils.getSaveSDFile(SAVE_PATH_FOLDER, SAVE_PATH_FILENAME);
            if (System.currentTimeMillis() - saveSDFile.lastModified() > 5000)
                append = true;

            pw = new PrintWriter(new BufferedWriter(new FileWriter(
                    saveSDFile, append)));
            pw.println(getLogMsg(ex));
            pw.println();
        } catch (Exception e) {

        } finally {
            if (pw != null)
                pw.close();
        }
    }


    private String getLogMsg(Throwable ex) throws NameNotFoundException {
        String lineSeparator = "\r\n";
        StringBuilder sb = new StringBuilder();

        sb.append(SystemTool.getDataTime("yyyy-MM-dd-HH-mm-ss")).append(lineSeparator);

        // 应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        sb.append("App Version: ");
        sb.append(pi.versionName);
        sb.append('_');
        sb.append(pi.versionCode);
        sb.append(lineSeparator).append(lineSeparator);

        // android版本号
        sb.append("OS Version: ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("_");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(lineSeparator).append(lineSeparator);

        // 手机制造商
        sb.append("Vendor: ");
        sb.append(Build.MANUFACTURER);
        sb.append(lineSeparator).append(lineSeparator);

        // 手机型号
        sb.append("Model: ");
        sb.append(Build.MODEL);
        sb.append(lineSeparator).append(lineSeparator);

        // cpu架构
        sb.append("CPU ABI: ");
        sb.append(Build.CPU_ABI);
        sb.append(lineSeparator).append(lineSeparator);


        //exception的详细信息
        sb.append("Exception:");
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String dump = info.toString();
        sb.append(dump);
        sb.append(lineSeparator).append(lineSeparator);


        return sb.toString();
    }
}
