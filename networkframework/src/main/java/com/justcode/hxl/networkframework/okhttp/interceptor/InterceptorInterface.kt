package com.justcode.hxl.networkframework.okhttp.interceptor

/**
 * 抽象拦截器，定义开始和结束，具体实现，实现类完成
 * 设计思想：任何一个请求都是由开始，请求中，结束组成
 * 不过，只需要知道开始和结束，中间即是请求中
 */
interface InterceptorInterface {
    fun runOnStart()
    fun runOnComplete()
}