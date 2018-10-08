package com.justcode.hxl.networkframework.okhttp.action

/**
 * 定义一个回掉接口，返回两个参数
 */
interface Action2<T, S> {
    fun call(t: T, s: S)
}