package com.idrv.coach.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * time: 15/7/10
 * description: 日志管理类
 *
 * @author sunjianfei
 */
public final class Logger {
    private static final String DEFAULT_TAG = "Zjb";
    public static final int TYPE_V = 1;
    public static final int TYPE_D = TYPE_V << 1;
    public static final int TYPE_I = TYPE_D << 1;
    public static final int TYPE_W = TYPE_I << 1;
    public static final int TYPE_E = TYPE_W << 1;
    private static final String LOG_NAME = "log.txt";
    private static final int LOG_SIZE = 204800;
    private static boolean isLog = true;
    private static String sLogPath;
    private static SimpleDateFormat sFormat;

    private Logger() {
    }

    public static void initLog(boolean enable, String path) {
        isLog = enable;
        sLogPath = path;
    }

    public static boolean isDebug() {
        return isLog;
    }

    public static void e(String msg) {
        log(DEFAULT_TAG, msg, TYPE_E, false);
    }

    public static void e(Exception e) {
        log(DEFAULT_TAG, Log.getStackTraceString(e), TYPE_E, false);
    }

    public static void e(Throwable tr) {
        log(DEFAULT_TAG, Log.getStackTraceString(tr), TYPE_E, false);
    }

    public static void e(String tag, String msg) {
        log(tag, msg, TYPE_E, false);
    }

    public static void e(String tag, Exception e) {
        log(tag, Log.getStackTraceString(e), TYPE_E, false);
    }

    public static void e(String tag, Throwable tr) {
        log(tag, Log.getStackTraceString(tr), TYPE_E, false);
    }

    public static void e(Object tag, String msg) {
        log(tag.getClass().getSimpleName(), msg, TYPE_E, false);
    }

    public static void e(Object tag, Exception e) {
        log(tag.getClass().getSimpleName(), Log.getStackTraceString(e), TYPE_E, false);
    }

    public static void e(Object tag, Throwable tr) {
        log(tag.getClass().getSimpleName(), Log.getStackTraceString(tr), TYPE_E, false);
    }

    public static void e(String tag, String msg, Throwable tr) {
        log(tag, msg + '\n' + Log.getStackTraceString(tr), TYPE_E, false);
    }

    public static void w(String msg) {
        log(DEFAULT_TAG, msg, TYPE_W, false);
    }

    public static void w(Exception e) {
        log(DEFAULT_TAG, Log.getStackTraceString(e), TYPE_W, false);
    }

    public static void w(Throwable tr) {
        log(DEFAULT_TAG, Log.getStackTraceString(tr), TYPE_W, false);
    }

    public static void w(String tag, String msg) {
        log(tag, msg, TYPE_W, false);
    }

    public static void w(String tag, Exception e) {
        log(tag, Log.getStackTraceString(e), TYPE_W, false);
    }

    public static void w(String tag, Throwable tr) {
        log(tag, Log.getStackTraceString(tr), TYPE_W, false);
    }

    public static void w(Object tag, String msg) {
        log(tag.getClass().getSimpleName(), msg, TYPE_W, false);
    }

    public static void w(Object tag, Exception e) {
        log(tag.getClass().getSimpleName(), Log.getStackTraceString(e), TYPE_W, false);
    }

    public static void w(Object tag, Throwable tr) {
        log(tag.getClass().getSimpleName(), Log.getStackTraceString(tr), TYPE_W, false);
    }

    public static void w(String tag, String msg, Throwable tr) {
        log(tag, msg + '\n' + Log.getStackTraceString(tr), TYPE_W, false);
    }

    public static void i(String msg) {
        log(DEFAULT_TAG, msg, TYPE_I, false);
    }

    public static void i(boolean msg) {
        log(DEFAULT_TAG, msg+"", TYPE_I, false);
    }

    public static void i(String tag, String msg) {
        log(tag, msg, TYPE_I, false);
    }

    public static void i(Object tag, String msg) {
        log(tag.getClass().getSimpleName(), msg, TYPE_I, false);
    }

    public static void d(String msg) {
        log(DEFAULT_TAG, msg, TYPE_D, false);
    }

    public static void d(String tag, String msg) {
        log(tag, msg, TYPE_D, false);
    }

    public static void d(Object tag, String msg) {
        log(tag.getClass().getSimpleName(), msg, TYPE_D, false);
    }

    public static void v(String msg) {
        log(DEFAULT_TAG, msg, TYPE_V, false);
    }

    public static void v(String tag, String msg) {
        log(tag, msg, TYPE_V, false);
    }

    public static void v(Object tag, String msg) {
        log(tag.getClass().getSimpleName(), msg, TYPE_V, false);
    }

    /*将log写入文件*/
    public static void f(String msg) {
        log(DEFAULT_TAG, msg, TYPE_I, true);
    }

    /*将log写入文件*/
    public static void f(String msg, boolean force) {
        if (force) {
            StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
            String fileInfo = "[" + stackTrace.getFileName() + "(" + stackTrace.getLineNumber() + ") " + stackTrace.getMethodName() + "] ";
            msg = fileInfo + msg;
            if (!TextUtils.isEmpty(sLogPath)) {
                String message = sFormat.format(new Date());
                message = message + "  ";
                message = message + DEFAULT_TAG;
                message = message + "  ";
                message = message + msg;
                message = message + "\n";
                write(LOG_NAME, LOG_SIZE, message);
            }
        } else {
            f(msg);
        }
    }

    public static void f(Exception e) {
        log(DEFAULT_TAG, Log.getStackTraceString(e), TYPE_E, true);
    }

    public static void f(Throwable tr) {
        log(DEFAULT_TAG, Log.getStackTraceString(tr), TYPE_E, true);
    }

    public static void f(String tag, String msg) {
        log(tag, msg, TYPE_I, true);
    }

    public static void f(String tag, Exception e) {
        log(tag, Log.getStackTraceString(e), TYPE_E, true);
    }

    public static void f(String tag, Throwable tr) {
        log(tag, Log.getStackTraceString(tr), TYPE_E, true);
    }

    public static void f(Object tag, String msg) {
        log(tag.getClass().getSimpleName(), msg, TYPE_I, true);
    }

    public static void f(Object tag, Exception e) {
        log(tag.getClass().getSimpleName(), Log.getStackTraceString(e), TYPE_E, true);
    }

    public static void f(Object tag, Throwable tr) {
        log(tag.getClass().getSimpleName(), Log.getStackTraceString(tr), TYPE_E, true);
    }

    public static void f(String tag, String msg, Throwable tr) {
        log(tag, msg + '\n' + Log.getStackTraceString(tr), TYPE_E, true);
    }


    private static void log(String tag, String msg, int logType, boolean toFile) {
        if (isLog) {
            StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
            String fileInfo = "[" + stackTrace.getFileName() + "(" + stackTrace.getLineNumber() + ") " + stackTrace.getMethodName() + "] ";
            msg = fileInfo + msg;
            switch (logType) {
                case TYPE_V:
                    Log.v(tag, msg);
                    break;
                case TYPE_D:
                    Log.d(tag, msg);
                    break;
                case TYPE_I:
                    Log.i(tag, msg);
                    break;
                case TYPE_W:
                    Log.w(tag, msg);
                    break;
                case TYPE_E:
                    Log.e(tag, msg);
            }

            if (toFile && !TextUtils.isEmpty(sLogPath)) {
                String message = sFormat.format(new Date());
                message = message + "  ";
                message = message + tag;
                message = message + "  ";
                message = message + msg;
                message = message + "\n";
                write(LOG_NAME, LOG_SIZE, message);
            }

        }
    }

    private static void write(String filePath, int logFileLength, final String logInfo) {
        try {
            if (FileUtil.isSDCardExist()) {
                final File file = new File(sLogPath, filePath);
                if (file.exists() && file.length() > (long) logFileLength) {
                    file.delete();
                }
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                (new Thread() {
                    public void run() {
                        FileWriter fw = null;

                        try {
                            fw = new FileWriter(file, true);
                            fw.write(logInfo);
                            fw.flush();
                        } catch (IOException e1) {
                            Logger.e(DEFAULT_TAG, "Write log to file failed.", e1);
                        } finally {
                            if (fw != null) {
                                try {
                                    fw.close();
                                } catch (IOException e2) {
                                    Logger.e(DEFAULT_TAG, "Write log to file failed.", e2);
                                }
                            }

                        }

                    }
                }).start();
            }
        } catch (Exception e) {
            e(DEFAULT_TAG, "Write log to file failed.", e);
        }

    }

    static {
        sFormat = new SimpleDateFormat("[MM-dd hh:mm:ss]", Locale.CHINA);
    }
}
