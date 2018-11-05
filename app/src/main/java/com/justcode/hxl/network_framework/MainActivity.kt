package com.justcode.hxl.network_framework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.justcode.hxl.network_framework.okdemo.WeatherAction
import com.justcode.hxl.network_framework.okdemo.WetherData
import com.justcode.hxl.network_framework.retrofitdemo.API
import com.justcode.hxl.network_framework.retrofitdemo.LoginRequest
import com.justcode.hxl.network_framework.retrofitdemo.LoginResult
import com.justcode.hxl.networkframework.loading.NormalLoading
import com.justcode.hxl.networkframework.okhttp.HttpResult
import com.justcode.hxl.networkframework.okhttp.action.Action1
import com.justcode.hxl.networkframework.okhttp.interceptor.InterceptorUtil
import com.justcode.hxl.networkframework.retrofit.*
import com.ptyh.smartyc.corelib.http.FileApi
import com.ptyh.smartyc.corelib.http.FileApiService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.RxThreadFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_okhttp.setOnClickListener {

            WeatherAction()
                    .addParam("重庆")
                    .addInterceptor(InterceptorUtil.buildLoading(NormalLoading(this)))
                    .onSuccess(object : Action1<HttpResult<*>> {
                        override fun call(t: HttpResult<*>) {
                            t?.let {
                                val wetherData: WetherData = t.result as WetherData
                                tv_okhttp.text = JSON.toJSONString(wetherData)
                            }
                        }
                    })
                    //可以不实现
                    .onFail(object : Action1<HttpResult<*>> {
                        override fun call(t: HttpResult<*>) {
                        }

                    })
                    .onFailToast(this)
                    .execute()


        }

        btn_retrofit.setOnClickListener {

            ApiService.api<API>()
                    .userLogin(loginRequest = LoginRequest("13508320770", "12345678"))
                    .io2Main()
                    .compose(HttpResultHandle.handle())
                    .subscribe(object : RxObserver<LoginResult>() {
                        override fun onNext(t: LoginResult) {
                            super.onNext(t)
                        }

                        override fun onError(e: Throwable) {
                            super.onError(e)
                        }

                        override fun onComplete() {
                            super.onComplete()
                        }
                    }
                    )


        }

    }

    fun uploadFile() {
        var fileList: MutableList<File> = ArrayList()
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型
        fileList.forEach {
            val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), it)
            builder.addFormDataPart(it.name, it.name, requestBody)
        }
        FileApiService.api<FileApi>()
                .upload("forum-note", builder.build().parts())
                .io2Main()
                .compose(HttpResultHandle.handle())
                .subscribe({

                }, {


                })
    }
}

