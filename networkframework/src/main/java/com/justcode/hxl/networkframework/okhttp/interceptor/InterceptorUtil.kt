package com.justcode.hxl.networkframework.okhttp.interceptor

import android.content.Context
import android.widget.Toast
import com.justcode.hxl.networkframework.loading.BaseLoading
import com.justcode.hxl.networkframework.okhttp.HttpResult
import com.justcode.hxl.networkframework.okhttp.action.Action1

class InterceptorUtil {
    companion object {
        //toast提示语
        fun buildToast2Action1(context: Context): Action1<HttpResult<*>> {
            return object : Action1<HttpResult<*>> {
                override fun call(t: HttpResult<*>) {
                    Toast.makeText(context, t.reason, Toast.LENGTH_SHORT).show()
                }

            }
        }

        //过渡效果
        fun buildLoading(loading: BaseLoading?): InterceptorInterface {
            return object : InterceptorInterface {
                override fun runOnStart() {
                    loading?.show()
                }

                override fun runOnComplete() {
                    loading?.hide()
                }

            }
        }
    }
}