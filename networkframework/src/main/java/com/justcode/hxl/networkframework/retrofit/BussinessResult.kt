package com.justcode.hxl.networkframework.retrofit

/**
 * 参数根据自己的接口定义
 */
open class BussinessResult<T>(var err_code: String? = null,
                              var err_code_des: String? = "",
                              var body: T? = null) : HttpResult<T> {
    override fun obtainIsSuccess(): Boolean = "0" == err_code

    override fun obtainData(): T? = body

    override fun obtainMessage(): String? = err_code_des

}