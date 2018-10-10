package com.justcode.hxl.network_framework

import android.app.Application
import com.justcode.hxl.networkframework.okhttp.httpcore.HttpAction

class Myapplication:Application(){
    override fun onCreate() {
        super.onCreate()
        HttpAction.initOKHTTP()
    }
}