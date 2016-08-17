package com.wdl.utils.test;

import android.util.Log;

import com.wdl.utils.data.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志记录
 */
public class LogUtil {
    private static String SAVE_PATH_FOLDER = FileUtils.getSDCardPath() + File.separator + "rrcx" + File.separator + "log";
    private static String SAVE_PATH_FILENAME= "log";
    private static String DEFULT_TAG = "LogUtil";

    /* 开发阶段*/
    public static final int DEVELOP = 0;
    /*内部测试阶段*/
    public static final int DEBUG = 1;
    /* 公开测试*/
    public static final int BATE = 2;
    /* 正式版*/
    public static final int RELEASE = 3;

    /* 当前阶段标示*/
    public static int currentStage = DEBUG;


    public static void log(String msg) {
        log(DEFULT_TAG, msg);
    }

    public static void log(Class clazz, String msg) {
        log(clazz.getSimpleName(), msg);
    }

    public static void log(String tag, String msg) {
        if (currentStage == DEVELOP) {
            Log.i(tag, msg);
        } else if (currentStage == DEBUG) {
            Log.i(tag, msg);
        } else if (currentStage == BATE) {

        } else if (currentStage == RELEASE) {

        }
    }

    public static void log_d(String msg) {
        log_d(DEFULT_TAG, msg);
    }

    public static void log_d(Class clazz, String msg) {
        log_d(clazz.getSimpleName(), msg);
    }

    public static void log_d(String tag, String msg) {
        if (currentStage == DEVELOP) {
            Log.d(tag, msg);
        } else if (currentStage == DEBUG) {
        } else if (currentStage == BATE) {

        } else if (currentStage == RELEASE) {

        }
    }


    public static void log_w(String msg) {
        log_w(DEFULT_TAG, msg);
    }

    public static void log_w(Class clazz, String msg) {
        log_w(clazz.getSimpleName(), msg);
    }

    public static void log_w(String tag, String msg) {
        if (currentStage == DEVELOP) {
            Log.w(tag, msg);
        } else if (currentStage == DEBUG) {
            Log.w(tag, msg);
        } else if (currentStage == BATE) {

        } else if (currentStage == RELEASE) {

        }
    }

    public static void log_v(String msg) {
        log_v(DEFULT_TAG, msg);
    }

    public static void log_v(Class clazz, String msg) {
        log_v(clazz.getSimpleName(), msg);
    }

    public static void log_v(String tag, String msg) {
        if (currentStage == DEVELOP) {
            Log.v(tag, msg);
        } else if (currentStage == DEBUG) {
        } else if (currentStage == BATE) {

        } else if (currentStage == RELEASE) {

        }
    }


    public static void log_e(String msg) {
        log_e(DEFULT_TAG, msg);
    }

    public static void log_e(Class clazz, String msg) {
        log_e(clazz.getSimpleName(), msg);
    }

    public static void log_e(String tag, String msg) {
        if (currentStage == DEVELOP) {
            Log.e(tag, msg);
        } else if (currentStage == DEBUG) {
            saveLogToSd(msg);
        } else if (currentStage == BATE) {

        } else if (currentStage == RELEASE) {

        }
    }

    public static void saveLogToSd(String log) {
        log = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\r\n" + log + "\r\n";
        FileUtils.saveFileCacheAppend(log.getBytes(), SAVE_PATH_FOLDER, SAVE_PATH_FILENAME);
    }
}
