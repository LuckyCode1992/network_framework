package com.justcode.hxl.networkframework.okhttp.header

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 自己决定需要什么要的header
 */
class CommonHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain?): Response {
        val original = chain?.request()
        val newRequest = original?.newBuilder()
                ?.addHeader("User-Agent", "userinfo")
                ?.addHeader("Content-Type", "application/json;charset=UTF-8")
        val request = newRequest?.build()
        return chain!!.proceed(request)

    }

}