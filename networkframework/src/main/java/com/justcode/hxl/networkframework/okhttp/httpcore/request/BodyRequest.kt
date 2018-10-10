package com.justcode.hxl.networkframework.okhttp.httpcore.request

import com.alibaba.fastjson.JSON
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

class BodyRequest {
    companion object {
        fun build(url0: String?, obj: Any?): Request {
            val json = JSON.toJSONString(obj)
            val bodyRequest = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            return Request.Builder().post(bodyRequest).url(url0).build()
        }
    }
}