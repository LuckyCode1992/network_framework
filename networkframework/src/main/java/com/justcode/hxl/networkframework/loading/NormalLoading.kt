package com.justcode.hxl.networkframework.loading

import android.app.Activity

class NormalLoading(activity: Activity?) : BaseLoading(activity) {

    val loading: Dialog_loading by lazy {
        val loading = Dialog_loading(activity)
        loading
    }

    override fun show() {
        super.show()
        if (!loading.isShowing) {
            loading.show()
        }
    }

    override fun hide() {
        super.hide()
        if (loading.isShowing) {
            loading.dismiss()
        }

    }

    override fun onShow() {
        //todo 显示的回掉
    }

    override fun onHide() {

        //todo 隐藏的回掉
    }

}


