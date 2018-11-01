package com.justcode.hxl.network_framework

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.fastjson.JSON
import com.justcode.hxl.network_framework.okdemo.WeatherAction
import com.justcode.hxl.network_framework.okdemo.WetherData
import com.justcode.hxl.networkframework.loading.NormalLoading
import com.justcode.hxl.networkframework.okhttp.HttpResult
import com.justcode.hxl.networkframework.okhttp.action.Action1
import com.justcode.hxl.networkframework.okhttp.interceptor.InterceptorUtil
import kotlinx.android.synthetic.main.activity_main.*

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
    }
}

