package com.justcode.hxl.networkframework.retrofit

import com.google.gson.Gson

/**
 * 参数根据自己的接口定义
 */
open class BussinessResult<T>(var err_code: String? = null,
                              var err_code_des: String? = "",

                              var body: T? = null) : HttpResult<T> {
    override fun obtainIsSuccess(): Boolean = "0" == err_code

    override fun obtainData(): T? = body

    override fun obtainMessage(): String {
        return "$err_code&&$err_code_des"

    }

}