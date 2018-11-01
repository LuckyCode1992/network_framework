package com.justcode.hxl.network_framework.retrofitdemo

import com.justcode.hxl.networkframework.retrofit.BussinessResult
import io.reactivex.Observable
import retrofit2.http.*

interface API{
    /**
     * 获取省市区，区域
     * 替换方式
     */
    @GET("api/area/{areaId}")
    fun getAreal(@Path("areaId") areaId: String): Observable<BussinessResult<*>>//*这里是占位符，实际项目，就是请求返回的body的实体类见login接口

    /**
     * eID登录获取token
     * 重新定义 完整url方式
     */
    @GET
    fun eIDgetToken(@Url url: String = "http://uam.eidop.com:8088/uiam/oauth2/access_token",
                    @Query("client_id") client_id: String,
                    @Query("client_secret") client_secret: String,
                    @Query("grant_type") grant_type: String,
                    @Query("code") code: String): Observable<*>//*这里是占位符，实际项目，就是请求返回的body的实体类见login接口
    /**
     * 搜索
     * 普通 get请求
     */
    @GET("api/home/searchByKeyWords")
    fun searchByKeyWords(@Query("keyWords") keyWords: String): Observable<BussinessResult<*>>//*这里是占位符，实际项目，就是请求返回的body的实体类见login接口

    /**
     * POST 重新定义url 传入kv键值对方式
     */
    @POST
    fun orderpay(@Url url: String = "http://214072e1v8.iok.la:21836/alipay/createOrder",
                 @Query("body") body: String,
                 @Query("total_fee") total_fee: String): Observable<BussinessResult<String>>//*这里是占位符，实际项目，就是请求返回的body的实体类见login接口

    /**
     * 用户登录
     * post 传入body方式
     */
    @POST("api/gz/user/login.json")
    fun userLogin(@Body loginRequest: LoginRequest): Observable<BussinessResult<LoginResult>>
}