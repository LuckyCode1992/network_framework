package com.justcode.hxl.networkframework.tcp.socket.impl.client.action;

import android.content.Context;

import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IRegister;
import com.justcode.hxl.networkframework.tcp.socket.impl.exceptions.ManuallyDisconnectException;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.ConnectionInfo;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.OkSocketOptions;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.ISocketActionListener;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.SocketActionAdapter;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.connection.IConnectionManager;


public class SocketActionHandler extends SocketActionAdapter {
    private IConnectionManager mManager;

    private OkSocketOptions.IOThreadMode mCurrentThreadMode;

    private boolean iOThreadIsCalledDisconnect = false;

    public SocketActionHandler() {

    }

    public void attach(IConnectionManager manager, IRegister<ISocketActionListener,IConnectionManager> register) {
        this.mManager = manager;
        register.registerReceiver(this);
    }

    public void detach(IRegister register) {
        register.unRegisterReceiver(this);
    }

    @Override
    public void onSocketIOThreadStart(Context context, String action) {
        if (mManager.getOption().getIOThreadMode() != mCurrentThreadMode) {
            mCurrentThreadMode = mManager.getOption().getIOThreadMode();
        }
        iOThreadIsCalledDisconnect = false;
    }

    @Override
    public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
        if (mCurrentThreadMode != mManager.getOption().getIOThreadMode()) {//切换线程模式,不需要断开连接
            //do nothing
        } else {//多工模式
            if (!iOThreadIsCalledDisconnect) {//保证只调用一次,多工多线程,会调用两次
                iOThreadIsCalledDisconnect = true;
                if (!(e instanceof ManuallyDisconnectException)) {
                    mManager.disconnect(e);
                }
            }
        }
    }

    @Override
    public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
        mManager.disconnect(e);
    }
}
