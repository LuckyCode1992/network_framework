package com.justcode.hxl.network_framework.tcpdemo

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SwitchCompat
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.justcode.hxl.network_framework.R
import com.justcode.hxl.network_framework.tcpdemo.adapter.LogAdapter
import com.justcode.hxl.network_framework.tcpdemo.data.*
import com.justcode.hxl.networkframework.tcp.basic.bean.OriginalData
import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable
import com.justcode.hxl.networkframework.tcp.socket.sdk.OkSocket
import com.justcode.hxl.networkframework.tcp.socket.sdk.OkSocket.open
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.ConnectionInfo
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.OkSocketOptions
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.SocketActionAdapter
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.bean.IPulseSendable
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.connection.IConnectionManager
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.connection.NoneReconnect
import kotlinx.android.synthetic.main.activity_tcp.*
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

class TCPActivity : AppCompatActivity() {
    private var mManager: IConnectionManager? = null

    private val mSendLogAdapter = LogAdapter()
    private val mReceLogAdapter = LogAdapter()


    private var mInfo: ConnectionInfo? = null
    private val socketActionAdapter by lazy {
        val adapter = object : SocketActionAdapter() {
            /**
             * 连接成功
             */
            override fun onSocketConnectionSuccess(context: Context?, info: ConnectionInfo?, action: String?) {
                super.onSocketConnectionSuccess(context, info, action)
                logRece("连接成功")
                mManager?.send(HandShake())
                connect.text = "DisConnect"
                initSwitch()
            }

            /**
             * 断开连接
             */
            override fun onSocketDisconnection(context: Context?, info: ConnectionInfo?, action: String?, e: Exception?) {
                super.onSocketDisconnection(context, info, action, e)
                if (e != null) {
                    if (e is RedirectException) {
                        logSend("正在重定向连接...")
                        mManager?.switchConnectionInfo(e.redirectInfo)
                        mManager?.connect()
                    } else {
                        logSend("异常断开:" + e.message)
                    }
                } else {
                    logSend("正常断开")
                }
                connect.text = "Connect"
            }

            /**
             * 连接失败
             */
            override fun onSocketConnectionFailed(context: Context?, info: ConnectionInfo?, action: String?, e: Exception?) {
                super.onSocketConnectionFailed(context, info, action, e)
                Toast.makeText(context, "连接失败" + e?.message, LENGTH_SHORT).show()
                logSend("连接失败")
                connect.text = "Connect"
            }

            /**
             * 读入操作（接收数据）  返回的相应数据，这里是最关键的地方，数据已经传输完毕，这里就是解析地方，这里需要和后台商量数据格式
             * 以下cmd 都是自己写死的，具体项目，自行参照后台规则
             */
            override fun onSocketReadResponse(context: Context?, info: ConnectionInfo?, action: String?, data: OriginalData?) {
                super.onSocketReadResponse(context, info, action, data)
                if (data == null)
                    return
                val str = String(data!!.getBodyBytes(), Charset.forName("utf-8"))
                val jsonObject = JsonParser().parse(str).asJsonObject
                val cmd = jsonObject.get("cmd").asInt
                if (cmd == 54) {//登陆成功
                    val handshake = jsonObject.get("handshake").asString
                    logRece("握手成功! 握手信息:$handshake. 开始心跳..")
                    mManager?.getPulseManager()?.setPulseSendable(PulseBean())?.pulse()
                } else if (cmd == 57) {//切换,重定向  其实，就是重新连接新的ip和端口号，这个看需要实现，主要是依赖后台返回数据中，带有新的IP和端口号
                    val ip = jsonObject.get("data").asString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val port = Integer.parseInt(jsonObject.get("data").asString.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
                    val redirectInfo = ConnectionInfo(ip, port)
                    redirectInfo.backupInfo = mInfo?.getBackupInfo()
                    mManager?.getReconnectionManager()?.addIgnoreException(RedirectException::class.java)
                    mManager?.disconnect(RedirectException(redirectInfo))
                } else if (cmd == 14) {//心跳
                    logRece("收到心跳,心跳成功")
                    mManager?.getPulseManager()?.feed()
                } else {
                    logRece(str)
                }

            }

            /**
             * 写出数据（发送数据），参照上一个方法理解
             */
            override fun onSocketWriteResponse(context: Context?, info: ConnectionInfo?, action: String?, data: ISendable?) {
                super.onSocketWriteResponse(context, info, action, data)
                if (data == null)
                    return
                var bytes = data.parse()
                bytes = Arrays.copyOfRange(bytes, 4, bytes.size)
                val str = String(bytes, Charset.forName("utf-8"))
                val jsonObject = JsonParser().parse(str).asJsonObject
                val cmd = jsonObject.get("cmd").asInt
                when (cmd) {
                    54 -> {
                        val handshake = jsonObject.get("handshake").asString
                        logSend("发送握手数据:$handshake")
                    }
                    else -> logSend(str)
                }
            }

            /**
             * 继续参照上面的方法理解
             */
            override fun onPulseSend(context: Context?, info: ConnectionInfo?, data: IPulseSendable?) {
                super.onPulseSend(context, info, data)
                if (data == null)
                    return
                var bytes = data.parse()
                bytes = Arrays.copyOfRange(bytes, 4, bytes.size)
                val str = String(bytes, Charset.forName("utf-8"))
                val jsonObject = JsonParser().parse(str).asJsonObject
                val cmd = jsonObject.get("cmd").asInt
                if (cmd == 14) {
                    logSend("发送心跳包")
                }
            }
        }
        adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tcp)

        initData()
        setListener()
    }

    private fun setListener() {
        mManager?.registerReceiver(socketActionAdapter)

        is_live_in_bg.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked == isChecked) {
                return@OnCheckedChangeListener
            }
            var value: Long = -1
            if (isChecked) {
                value = OkSocket.getBackgroundSurvivalTime()
            } else {
                value = -1
            }
            OkSocket.setBackgroundSurvivalTime(value)
            bg_live_minute.setText("")
            bg_live_minute.setHint(value.toString() + "")
        })

        switch_reconnect.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (mManager != null && !mManager!!.isConnect()) {
                buttonView.isChecked = !isChecked
                return@OnCheckedChangeListener
            }
            if (buttonView.isChecked == isChecked) {
                return@OnCheckedChangeListener
            }
            if (!isChecked) {
                mManager?.option(OkSocketOptions.Builder(mManager?.getOption()).setReconnectionManager(NoneReconnect()).build())
            } else {
                mManager?.option(OkSocketOptions.Builder(mManager?.getOption()).setReconnectionManager(OkSocketOptions.getDefault().reconnectionManager).build())
            }
        })

        connect.setOnClickListener(View.OnClickListener {
            if (mManager == null) {
                return@OnClickListener
            }
            if (!mManager!!.isConnect()) {
                mManager!!.connect()
            } else {
                connect.setText("DisConnecting")
                mManager!!.disconnect()
            }
        })

        disconnect.setOnClickListener(View.OnClickListener {
            if (mManager == null) {
                return@OnClickListener
            }
            mManager?.disconnect()
        })

        clear_log.setOnClickListener(View.OnClickListener {
            mReceLogAdapter.dataList.clear()
            mSendLogAdapter.dataList.clear()
            mReceLogAdapter.notifyDataSetChanged()
            mSendLogAdapter.notifyDataSetChanged()
        })

        bg_live_minute_btn.setOnClickListener(View.OnClickListener {
            val timeStr = bg_live_minute.getText().toString()
            var time: Long = 0
            try {
                time = java.lang.Long.parseLong(timeStr)
                OkSocket.setBackgroundSurvivalTime(time)
            } catch (e: Exception) {
            }
        })

        redirect.setOnClickListener(View.OnClickListener {
            if (mManager == null) {
                return@OnClickListener
            }
            val ip = ip.getText().toString()
            val portStr = port.getText().toString()
            val jsonObject = JsonObject()
            jsonObject.addProperty("cmd", 57)
            jsonObject.addProperty("data", ip + ":" + portStr)
            val bean = DefaultSendBean()
            bean.setContent(Gson().toJson(jsonObject))
            mManager?.send(bean)
        })

        set_pulse_frequency.setOnClickListener(View.OnClickListener {
            if (mManager == null) {
                return@OnClickListener
            }
            val frequencyStr = pulse_frequency.getText().toString()
            var frequency: Long = 0
            try {
                frequency = java.lang.Long.parseLong(frequencyStr)
                val okOptions = OkSocketOptions.Builder(mManager!!.getOption())
                        .setPulseFrequency(frequency)
                        .build()
                mManager!!.option(okOptions)
            } catch (e: NumberFormatException) {
            }
        })

        manual_pulse.setOnClickListener(View.OnClickListener {
            if (mManager == null) {
                return@OnClickListener
            }
            mManager?.getPulseManager()?.trigger()
        })

        send_btn.setOnClickListener(View.OnClickListener {
            if (mManager == null) {
                return@OnClickListener
            }
            if (!mManager!!.isConnect()) {
                Toast.makeText(applicationContext, "未连接,请先连接", LENGTH_SHORT).show()
            } else {
                val msg = send_et.getText().toString()
                if (TextUtils.isEmpty(msg.trim({ it <= ' ' }))) {
                    return@OnClickListener
                }
                val msgDataBean = MsgDataBean(msg)
                mManager?.send(msgDataBean)
                send_et.setText("")
            }
        })
    }


    private fun initData() {
        val manager1 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        send_list.setLayoutManager(manager1)
        send_list.setAdapter(mSendLogAdapter)

        val manager2 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rece_list.setLayoutManager(manager2)
        rece_list.setAdapter(mReceLogAdapter)

        mInfo = ConnectionInfo("104.238.184.237", 8080)
        val builder = OkSocketOptions.Builder()
        mManager = open(mInfo).option(builder.build())
    }

    private fun initSwitch() {
        val okSocketOptions = mManager?.option
        val minute = OkSocket.getBackgroundSurvivalTime()
        is_live_in_bg?.isChecked = minute != -1L
        switch_reconnect?.isChecked = okSocketOptions?.reconnectionManager !is NoneReconnect
    }

    private fun logSend(log: String) {
        val logBean = LogBean(System.currentTimeMillis(), log)
        mSendLogAdapter.dataList.add(0, logBean)
        mSendLogAdapter.notifyDataSetChanged()
    }

    private fun logRece(log: String) {
        val logBean = LogBean(System.currentTimeMillis(), log)
        mReceLogAdapter.dataList.add(0, logBean)
        mReceLogAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mManager?.disconnect()
        mManager?.unRegisterReceiver(socketActionAdapter)

    }
}
