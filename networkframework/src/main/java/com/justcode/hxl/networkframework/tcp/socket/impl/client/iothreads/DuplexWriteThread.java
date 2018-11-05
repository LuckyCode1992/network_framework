package com.justcode.hxl.networkframework.tcp.socket.impl.client.iothreads;

import android.content.Context;


import com.justcode.hxl.networkframework.tcp.basic.AbsLoopThread;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IWriter;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;
import com.justcode.hxl.networkframework.tcp.socket.impl.exceptions.ManuallyDisconnectException;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction;
import com.justcode.hxl.networkframework.tcp.utils.SLog;

import java.io.IOException;



public class DuplexWriteThread extends AbsLoopThread {
    private IStateSender mStateSender;

    private IWriter mWriter;

    public DuplexWriteThread(Context context, IWriter writer,
                             IStateSender stateSender) {
        super(context, "duplex_write_thread");
        this.mStateSender = stateSender;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() {
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        mWriter.write();
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mWriter.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("duplex write error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_SHUTDOWN, e);
    }
}
