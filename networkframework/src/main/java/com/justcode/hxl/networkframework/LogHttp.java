package com.justcode.hxl.networkframework;


import android.text.TextUtils;
import android.util.Log;

public class LogHttp {

    public static void log(String msg) {
        if (TextUtils.isEmpty(msg))
            msg = "没有获取到日志信息";
        Log.d("网络请求结果", msg);
    }
}
