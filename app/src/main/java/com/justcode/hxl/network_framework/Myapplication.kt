package com.justcode.hxl.network_framework

import android.app.Application
import com.justcode.hxl.networkframework.okhttp.header.CommonHeaderInterceptor
import com.justcode.hxl.networkframework.okhttp.httpcore.HttpAction
import com.justcode.hxl.networkframework.retrofit.ApiService
import com.justcode.hxl.networkframework.tcp.socket.sdk.OkSocket
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.OkSocketOptions

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.net.CookieManager
import java.util.concurrent.TimeUnit

class Myapplication : Application() {
    override fun onCreate() {
        super.onCreate()
        HttpAction.initOKHTTP()
        ApiService.init("http://test.fuwugongshe.com/", OkHttpClient.Builder()
                .addInterceptor(CommonHeaderInterceptor())
                .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
                .cookieJar(JavaNetCookieJar(CookieManager()))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES))
        OkSocket.initialize(this, true)


    }
}