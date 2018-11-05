package com.justcode.hxl.networkframework.tcp.utils;

import android.util.Log;



public class SLog {

    private static boolean isDebug;

    public static void setIsDebug(boolean isDebug) {
        SLog.isDebug = isDebug;
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e("OkSocket", msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i("OkSocket", msg);
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            Log.w("OkSocket", msg);
        }
    }
}
