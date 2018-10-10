package com.justcode.hxl.networkframework.okhttp

/**
 * http请求结果返回实体
 * 具体数据结构根据后台返回进行修改
 */
 data class HttpResult<T>(var entity: T? = null, var code: Int? = null, var message: String? = null) {

    /**
     * 自行定义错误类型和提示语，也可以不用
     */
    companion object {
        val defaultErrorResult = HttpResult(null, -1, "服务器开小差")
        val timeOutErrorResult = HttpResult(null, -2, "没有连接上服务器")
        val paseErrorResult  = HttpResult(null, -3, "Json解析错误")
    }

}