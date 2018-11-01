package com.justcode.hxl.networkframework.retrofit

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * 在指定线程和Android Main线程之间切换
 */
fun <T> Observable<T>.io2Main(): Observable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}