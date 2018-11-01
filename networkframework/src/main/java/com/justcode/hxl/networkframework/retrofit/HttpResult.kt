package com.justcode.hxl.networkframework.retrofit

interface HttpResult<out T>{
    fun obtainIsSuccess(): Boolean
    fun obtainData(): T?
    fun obtainMessage(): String?
}