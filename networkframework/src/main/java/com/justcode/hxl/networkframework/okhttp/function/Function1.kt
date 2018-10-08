package com.justcode.hxl.networkframework.okhttp.function

import java.util.concurrent.Callable

/**
 * java.util.concurrent 包含许多线程安全、测试良好、高性能的并发构建块。创建 java.util.concurrent 的目的就是要实现 Collection 框架对数据结构所执行的并发操作
 */
interface Function1<T>:Callable<T>{
    override fun call(): T
}