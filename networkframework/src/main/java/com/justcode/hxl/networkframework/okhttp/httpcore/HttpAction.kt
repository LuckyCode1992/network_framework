package com.justcode.hxl.networkframework.okhttp.httpcore

import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.justcode.hxl.networkframework.LogHttp
import com.justcode.hxl.networkframework.okhttp.HttpResult
import com.justcode.hxl.networkframework.okhttp.MycookieJar
import com.justcode.hxl.networkframework.okhttp.httpcore.request.BodyRequest
import com.justcode.hxl.networkframework.okhttp.httpcore.request.GetRequest
import com.justcode.hxl.networkframework.okhttp.httpcore.request.PostRequest
import com.justcode.hxl.networkframework.okhttp.interceptor.InterceptorHttpAction
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.HashMap
import java.util.concurrent.TimeUnit

abstract class HttpAction<T> : InterceptorHttpAction<T, String> {
    val map = HashMap<String, String>()          //参数集合(String 参数)
    val fileMap = HashMap<String, File>()       //参数集合(文件 参数)
    var body: Any? = null
    var connTimeout = 0
    var readTimeout = 0
    var writeTimeout = 0

    var baseurl: String? = null
    var childurl: String? = null


    val GET = "GET"
    val POST = "POST"
    val BODY = "BODY"
    var methodTag = POST

    companion object {
        /**
         * 在application中初始化
         */
        val builder = OkHttpClient().newBuilder()

        fun initOKHTTP() {
            builder.connectTimeout(10, TimeUnit.SECONDS)
                    //.addInterceptor(CommonHeaderInterceptor())  //需要自定义header就打开
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    //  .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())//配置  这句话，忽略https证书验证
                    //  .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置  这句话，忽略host验证
                    .cookieJar(MycookieJar())

        }
    }

    constructor(baseurl: String?, childurl: String?) : super() {
        this.baseurl = baseurl
        this.childurl = childurl
    }

    constructor(childurl: String?) : super() {
        this.childurl = childurl
    }


    fun getRequest(): HttpAction<*> {
        methodTag = GET
        return this
    }

    fun postRequest(): HttpAction<*> {
        methodTag = POST
        return this
    }

    fun bodyRequest(): HttpAction<*> {
        methodTag = BODY
        return this
    }


    /**
     * 为单次请求配置连接超时时间
     *
     * @param timeout 单位s
     */
    fun connTimeOut(timeout: Int): HttpAction<*> {
        this.connTimeout = timeout
        return this
    }

    /**
     * 为单次请求配置读取超时时间
     *
     * @param timeout 单位s
     */
    fun readTimeOut(timeout: Int): HttpAction<*> {
        this.readTimeout = timeout
        return this
    }

    /**
     * 为单次请求配置写入超时时间
     *
     * @param timeout 单位s
     */
    fun writeTimeOut(timeout: Int): HttpAction<*> {
        this.writeTimeout = timeout
        return this
    }

    /**
     * 添加普通参数
     */
    fun add(key: String, value: String): HttpAction<*> {
        map[key] = value
        return this
    }

    /**
     * 添加文件参数
     */
    fun addFile(key: String, file: File): HttpAction<*> {
        fileMap[key] = file
        return this
    }

    /**
     * 添加请求体
     */
    fun addBody(body: Any): HttpAction<*> {
        this.body = body
        return this
    }

    override fun execute() {
        var url: String? = null
        baseurl?.let {
            url += it
        }
        childurl.let {
            url += it
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        runOnStart()//开始请求   回掉

        var request: Request? = null
        val tempMap = HashMap<String, Any>()
        tempMap.putAll(map)

        when (methodTag) {
            GET -> {
                request = GetRequest.build(url, map)
            }
            POST -> {
                if (fileMap != null && fileMap.size > 0) {
                    tempMap.putAll(fileMap)
                }
                request = PostRequest.Build(url, tempMap)
            }
            BODY -> {
                request = BodyRequest.build(url, body)
            }
        }

        if (connTimeout > 0) HttpAction.builder.connectTimeout(connTimeout.toLong(), TimeUnit.SECONDS)
        if (readTimeout > 0) builder.readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        if (writeTimeout > 0) builder.writeTimeout(writeTimeout.toLong(), TimeUnit.SECONDS)

        var questString: String = ""
        if (methodTag == BODY) {
            questString = JSON.toJSONString(body)
        } else {
            questString = JSON.toJSONString(tempMap)
        }
        LogHttp.log("请求:\n" + (url + "\n" + questString))
        val client = builder.build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                LogHttp.log("响应:\n" + (url + "\t" + "onError" + "\t" + e?.message))
                //请求结束后的回调(Gson解析前)
                runOnComplet()
                runOnResult(HttpResult.defaultErrorResult)

            }

            override fun onResponse(call: Call?, response: Response?) {
                val json = response?.body()?.string()
                LogHttp.log("响应:\n$url\tonResponse\n$json")
                //请求结束后的回调(Gson解析前)
                runOnComplet()
                try {
                    //Gson解析响应
                    val httpResult = HttpResult<T>()
                    httpResult.entity = decodeAndProcessModel(json, httpResult)
                    runOnResult(httpResult)
                } catch (e: Exception) {
                    Log.e("错误日志", "日志：${Log.getStackTraceString(e)}")
                    runOnResult(HttpResult.paseErrorResult)
                }

            }

        })

    }

    fun decodeAndProcessModel(json: String?, httpResult: HttpResult<T>): T? {
        //默认解析公共的数据
        val resultObject = JSONObject(json)
        httpResult.code = (resultObject.getInt("code"))
        if (resultObject.has("message"))
            httpResult.message = (resultObject.getString("message"))
        //解析非公共的数据
        val e = decodeModel(json, httpResult)
        httpResult.entity = e
        return e
    }

    fun decodeModel(json: String?, httpResult: HttpResult<T>): T? {
        return null
    }


}