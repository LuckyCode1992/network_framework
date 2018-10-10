package com.justcode.hxl.networkframework.loading

import android.app.Activity

/**
 *抽象loading 这里是用到了模板模式
 * 就是，把一些基本操作套路化，模板化，
 * 就是，显示和隐藏
 */
abstract class BaseLoading(var activity: Activity?) {


    companion object {
        var isShowing = false
    }

    open fun show() {
        isShowing = true
        onShow()
    }

   open fun hide() {
        isShowing = false
        onHide()
    }

    abstract fun onShow()
    abstract fun onHide()
}