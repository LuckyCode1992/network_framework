package com.justcode.hxl.networkframework.okhttp.action

/**
 * 定义一个回掉接口 ，返回一个参数的
 * action 用于界面调用者使用
 * 后续根据自己的情况，自行添加多个参数的
 */
interface Action1<T> {
    fun call(t: T)
}