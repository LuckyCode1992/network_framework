package com.justcode.hxl.networkframework.okhttp.interceptor

import android.content.Context
import android.os.Handler
import android.util.Log
import com.justcode.hxl.networkframework.LogHttp
import com.justcode.hxl.networkframework.okhttp.HttpResult
import com.justcode.hxl.networkframework.okhttp.action.Action1
import com.justcode.hxl.networkframework.okhttp.function.Function1
import java.util.ArrayList

/**
 * action 拦截器，用于处理何时开始，何时结束
 */
abstract class InterceptorHttpAction<T, S> {

    companion object {
        val mHandler: Handler = Handler()
    }

    var startAction: MutableList<Runnable>? = null                   //请求网络开始前调用本任务列表
    var completAction: MutableList<Runnable>? = null                 //请求网络结束后调用本任务列表(json解析前)
    var resultAction: MutableList<Action1<HttpResult<*>>>? = null    //请求网络结束后调用本任务列表(json解析后)

    /**
     * 拦截器，一个事件，开始和结束的时候，添加方法（请求开始，请求结束，中间过程加载动画）
     * 这里是面向接口（抽象）编程的思想
     *
     * 从这里开始，就可能有些难懂了，多看几遍
     *
     * 这里其实是把start和complete的回掉注入到一个pair中，
     * 这个pair中，是存放Runnable，这个Runnable其实是用来启动一个线程的
     */
    fun addInterceptor(action: InterceptorInterface): InterceptorHttpAction<T, S> {
        addAction(object : Function1<Pair<Runnable, Runnable>> {
            override fun call(): Pair<Runnable, Runnable> {
                return Pair(Runnable {
                    action.runOnStart()
                }, Runnable {
                    action.runOnComplete()
                })
            }
        })

        return this
    }

    /**
     * 这以下其实，是将start和complete分别放入两个集合中，方便后续调用
     * 之所以，这里要用这么麻烦，其实是因为要用Callable，实现线程的管理
     */
    fun addAction(actionBuilder: Function1<Pair<Runnable, Runnable>>): InterceptorHttpAction<T, S> {
        var actions: Pair<Runnable, Runnable>? = null

        try {
            actions = actionBuilder.call()
        } catch (e: Exception) {
            Log.e("错误日志", "日志：${Log.getStackTraceString(e)}")
        }
        return addAction(actions?.first, actions?.second)
    }

    fun addAction(start: Runnable?, complet: Runnable?): InterceptorHttpAction<T, S> {
        return addActionOnStart(start).addActionOnComplet(complet)
    }

    /**
     * 添加完成任务
     */
    fun addActionOnComplet(action: Runnable?): InterceptorHttpAction<T, S> {
        if (action == null) return this
        if (completAction == null) completAction = ArrayList()
        completAction?.add(action)
        return this

    }

    /**
     * 添加开始任务
     */
    fun addActionOnStart(action: Runnable?): InterceptorHttpAction<T, S> {
        if (action == null) return this
        if (startAction == null) startAction = ArrayList()
        startAction?.add(action)
        return this
    }

    /**
     * 添加结果任务
     */
    fun addActionOnResult(action: Action1<HttpResult<*>>): InterceptorHttpAction<T, S> {
        if (action == null) return this
        if (resultAction == null) resultAction = ArrayList()
        resultAction?.add(action)
        return this
    }

    /**
     * 执行 结果任务列表 中的所有任务
     */
    fun runOnResult(httpResult: HttpResult<*>) {
        resultAction?.let {
            mHandler.post {
                for (action in it) {
                    action.call(httpResult)
                }
                LogHttp.log("这里可以做一些统一操作:" + httpResult.error_code)
            }
        }
    }

    /**
     * 执行 结束任务列表 中的所有任务
     */
    fun runOnComplet() {
        completAction?.let {
            for (action in it) {
                mHandler.post(action)
            }
        }
    }

    /**
     * 执行 开始任务列表 中的所有任务
     */
    fun runOnStart() {
        startAction?.let {
            for (action in it) {
                mHandler.post(action)
            }
        }
    }

    /**
     * 设置请求成功时的回调任务, 将之添加到结果处理任务列表中
     *
     *  if (t.code == 0) 这个判断条件根据自己后台返回结果自行更改
     */
    fun onSuccess(action: Action1<HttpResult<*>>): InterceptorHttpAction<T, S> {
        addActionOnResult(object : Action1<HttpResult<*>> {
            override fun call(t: HttpResult<*>) {

                if (t.error_code == 0) action.call(t)
            }
        })
        return this
    }

    /**
     * 设置请求失败时的回调任务, 将之添加到结果处理任务列表中
     */
    fun onFail(action: Action1<HttpResult<*>>): InterceptorHttpAction<T, S> {
        addActionOnResult(object : Action1<HttpResult<*>> {
            override fun call(result: HttpResult<*>) {

                if (result.error_code != 0) action.call(result)
            }
        })
        return this
    }

    /**
     * 请求失败时, 弹Toast的任务
     */
    fun onFailToast(context: Context): InterceptorHttpAction<T, S> {
        return onFail(InterceptorUtil.buildToast2Action1(context))
    }

    /**
     * 执行请求
     */
    abstract fun execute()
}