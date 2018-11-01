package com.justcode.hxl.networkframework.retrofit

import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 * 处理接口返回实体类，获取data
 */
object HttpResultHandle {

    fun <T> handle(): ObservableTransformer<HttpResult<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.flatMap {
                if (it.obtainIsSuccess()) {
                    it.obtainData()?.let { it1 ->
                        return@flatMap Observable.just(it1)
                    } ?: run {
                        return@flatMap Observable.empty<T>()
                    }
                } else {
                    return@flatMap Observable.error<T>(HttpResultException(it.obtainMessage()
                            ?: "未知错误"))
                }
            }
        }
    }

}