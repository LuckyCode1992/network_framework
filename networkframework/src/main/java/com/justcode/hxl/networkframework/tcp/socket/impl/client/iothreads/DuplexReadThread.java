package com.justcode.hxl.networkframework.tcp.socket.impl.client.iothreads;

import android.content.Context;


import com.justcode.hxl.networkframework.tcp.basic.AbsLoopThread;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IReader;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;
import com.justcode.hxl.networkframework.tcp.socket.impl.exceptions.ManuallyDisconnectException;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction;
import com.justcode.hxl.networkframework.tcp.utils.SLog;

import java.io.IOException;



public class DuplexReadThread extends AbsLoopThread {
    private IStateSender mStateSender;

    private IReader mReader;

    public DuplexReadThread(Context context, IReader reader, IStateSender stateSender) {
        super(context, "duplex_read_thread");
        this.mStateSender = stateSender;
        this.mReader = reader;
    }

    @Override
    protected void beforeLoop() {
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mReader.read();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mReader.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex read error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
