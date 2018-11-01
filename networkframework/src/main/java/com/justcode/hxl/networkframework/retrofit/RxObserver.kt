package com.justcode.hxl.networkframework.retrofit

import android.app.Activity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


open class RxObserver<T>(var activity: Activity? = null) : Observer<T> {
    var disposable: Disposable? = null
    override fun onComplete() {
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }

        if (activity == null || activity!!.isFinishing) {
            return
        }

        //todo 加载动画结束
    }

    override fun onSubscribe(d: Disposable) {
        disposable = d
        if (activity == null || activity!!.isFinishing) {
            return
        }
        //todo 加载动画开始
    }

    override fun onNext(t: T) {
        if (activity == null || activity!!.isFinishing) {
            return
        }
    }

    override fun onError(e: Throwable) {
        disposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
        if (activity == null || activity!!.isFinishing) {
            return
        }
        //todo 加载动画结束
    }


}