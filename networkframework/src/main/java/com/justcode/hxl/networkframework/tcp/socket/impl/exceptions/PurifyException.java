package com.justcode.hxl.networkframework.tcp.socket.impl.exceptions;


import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class PurifyException extends RuntimeException {
    public PurifyException() {
        super();
    }

    public PurifyException(String message) {
        super(message);
    }

    public PurifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PurifyException(Throwable cause) {
        super(cause);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected PurifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
