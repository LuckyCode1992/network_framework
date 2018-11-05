package com.justcode.hxl.networkframework.tcp.socket.impl.client.iothreads;

import android.content.Context;


import com.justcode.hxl.networkframework.tcp.basic.AbsLoopThread;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IReader;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IWriter;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;
import com.justcode.hxl.networkframework.tcp.socket.impl.exceptions.ManuallyDisconnectException;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction;
import com.justcode.hxl.networkframework.tcp.utils.SLog;

import java.io.IOException;



public class SimplexIOThread extends AbsLoopThread {
    private IStateSender mStateSender;

    private IReader mReader;

    private IWriter mWriter;

    private boolean isWrite = false;


    public SimplexIOThread(Context context, IReader reader,
                           IWriter writer, IStateSender stateSender) {
        super(context, "simplex_io_thread");
        this.mStateSender = stateSender;
        this.mReader = reader;
        this.mWriter = writer;
    }

    @Override
    protected void beforeLoop() throws IOException {
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_START);
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_START);
    }

    @Override
    protected void runInLoopThread() throws IOException {
        isWrite = mWriter.write();
        if (isWrite) {
            isWrite = false;
            mReader.read();
        }
    }

    @Override
    public synchronized void shutdown(Exception e) {
        mReader.close();
        mWriter.close();
        super.shutdown(e);
    }

    @Override
    protected void loopFinish(Exception e) {
        e = e instanceof ManuallyDisconnectException ? null : e;
        if (e != null) {
            SLog.e("simplex error,thread is dead with exception:" + e.getMessage());
        }
        mStateSender.sendBroadcast(IAction.ACTION_WRITE_THREAD_SHUTDOWN, e);
        mStateSender.sendBroadcast(IAction.ACTION_READ_THREAD_SHUTDOWN, e);
    }
}
