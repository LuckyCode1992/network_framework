package com.justcode.hxl.network_framework.okdemo

import android.util.Log
import com.alibaba.fastjson.JSON
import com.justcode.hxl.networkframework.okhttp.HttpResult
import com.justcode.hxl.networkframework.okhttp.httpcore.HttpAction
import java.net.URLEncoder
import java.nio.charset.Charset

class WeatherAction(childurl: String? = null) : HttpAction<WetherData>("http://op.juhe.cn/onebox/weather/query") {


    fun addParam(cityname: String): WeatherAction {



        Log.d("WeatherAction",cityname)
        add("cityname", cityname)
        add("key", "33c5d3d8f81aa1e8dcd573b951e4eba6")
        add("dtype","json")
        return this
    }

    override fun decodeModel(json: String?, httpResult: HttpResult<WetherData>): WetherData? {
        return JSON.parseObject(json, WetherData::class.java)
    }
}