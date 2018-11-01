package com.justcode.hxl.networkframework.retrofit

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.util.concurrent.TimeUnit

object ApiService{

    lateinit var baseUrl: String
    lateinit var okHttpClient: OkHttpClient
    lateinit var retrofit: Retrofit
    val retrofitServiceCache = HashMap<String, Any>()

    fun init(baseUrl: String, okhttpBuilder: OkHttpClient.Builder? = null, retrofitBuilder: Retrofit.Builder? = null): ApiService {
        this.baseUrl = baseUrl
        okhttpBuilder?.let {
            okHttpClient = it.build()
        } ?: run {
            okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .cookieJar(JavaNetCookieJar(CookieManager()))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build()
        }
        retrofitBuilder?.let {
            retrofit = it.build()
        } ?: run {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .callFactory(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return this
    }

    inline fun <reified T : Any> api(): T {
        synchronized(retrofitServiceCache) {
            return if (retrofitServiceCache[T::class.java.name] == null) {
                val t = retrofit.create(T::class.java)
                retrofitServiceCache[T::class.java.name] = t
                t
            } else {
                retrofitServiceCache[T::class.java.name] as T
            }
        }
    }

}