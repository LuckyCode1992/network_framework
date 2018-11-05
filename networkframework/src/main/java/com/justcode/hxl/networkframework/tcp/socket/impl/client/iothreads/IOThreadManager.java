package com.justcode.hxl.networkframework.tcp.socket.impl.client.iothreads;

import android.content.Context;
import android.support.annotation.NonNull;


import com.justcode.hxl.networkframework.tcp.basic.AbsLoopThread;
import com.justcode.hxl.networkframework.tcp.interfacies.IIOManager;
import com.justcode.hxl.networkframework.tcp.interfacies.IReaderProtocol;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IReader;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IWriter;
import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;
import com.justcode.hxl.networkframework.tcp.socket.impl.exceptions.ManuallyDisconnectException;
import com.justcode.hxl.networkframework.tcp.socket.impl.iocore.ReaderImpl;
import com.justcode.hxl.networkframework.tcp.socket.impl.iocore.WriterImpl;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.OkSocketOptions;
import com.justcode.hxl.networkframework.tcp.utils.SLog;

import java.io.InputStream;
import java.io.OutputStream;


public class IOThreadManager implements IIOManager<OkSocketOptions> {

    private Context mContext;

    private InputStream mInputStream;

    private OutputStream mOutputStream;

    private OkSocketOptions mOkOptions;

    private IStateSender mSender;

    private IReader mReader;

    private IWriter mWriter;

    private AbsLoopThread mSimplexThread;

    private DuplexReadThread mDuplexReadThread;

    private DuplexWriteThread mDuplexWriteThread;

    private OkSocketOptions.IOThreadMode mCurrentThreadMode;

    public IOThreadManager(@NonNull Context context,
                           @NonNull InputStream inputStream,
                           @NonNull OutputStream outputStream,
                           @NonNull OkSocketOptions okOptions,
                           @NonNull IStateSender stateSender) {
        mContext = context;
        mInputStream = inputStream;
        mOutputStream = outputStream;
        mOkOptions = okOptions;
        mSender = stateSender;
        initIO();
    }

    private void initIO() {
        assertHeaderProtocolNotEmpty();
        mReader = new ReaderImpl();
        mReader.initialize(mInputStream, mSender);
        mWriter = new WriterImpl();
        mWriter.initialize(mOutputStream, mSender);
    }

    @Override
    public void startEngine() {
        mCurrentThreadMode = mOkOptions.getIOThreadMode();
        //初始化读写工具类
        mReader.setOption(mOkOptions);
        mWriter.setOption(mOkOptions);
        switch (mOkOptions.getIOThreadMode()) {
            case DUPLEX:
                SLog.w("DUPLEX is processing");
                duplex();
                break;
            case SIMPLEX:
                SLog.w("SIMPLEX is processing");
                simplex();
                break;
            default:
                throw new RuntimeException("未定义的线程模式");
        }
    }

    private void duplex() {
        shutdownAllThread(null);
        mDuplexWriteThread = new DuplexWriteThread(mContext, mWriter, mSender);
        mDuplexReadThread = new DuplexReadThread(mContext, mReader, mSender);
        mDuplexWriteThread.start();
        mDuplexReadThread.start();
    }

    private void simplex() {
        shutdownAllThread(null);
        mSimplexThread = new SimplexIOThread(mContext, mReader, mWriter, mSender);
        mSimplexThread.start();
    }

    private void shutdownAllThread(Exception e) {
        if (mSimplexThread != null) {
            mSimplexThread.shutdown(e);
            mSimplexThread = null;
        }
        if (mDuplexReadThread != null) {
            mDuplexReadThread.shutdown(e);
            mDuplexReadThread = null;
        }
        if (mDuplexWriteThread != null) {
            mDuplexWriteThread.shutdown(e);
            mDuplexWriteThread = null;
        }
    }

    @Override
    public void setOkOptions(OkSocketOptions options) {
        mOkOptions = options;
        if (mCurrentThreadMode == null) {
            mCurrentThreadMode = mOkOptions.getIOThreadMode();
        }
        assertTheThreadModeNotChanged();
        assertHeaderProtocolNotEmpty();

        mWriter.setOption(mOkOptions);
        mReader.setOption(mOkOptions);
    }

    @Override
    public void send(ISendable sendable) {
        mWriter.offer(sendable);
    }

    @Override
    public void close() {
        close(new ManuallyDisconnectException());
    }

    @Override
    public void close(Exception e) {
        shutdownAllThread(e);
        mCurrentThreadMode = null;
    }

    private void assertHeaderProtocolNotEmpty() {
        IReaderProtocol protocol = mOkOptions.getReaderProtocol();
        if (protocol == null) {
            throw new IllegalArgumentException("The reader protocol can not be Null.");
        }

        if (protocol.getHeaderLength() == 0) {
            throw new IllegalArgumentException("The header length can not be zero.");
        }
    }

    private void assertTheThreadModeNotChanged() {
        if (mOkOptions.getIOThreadMode() != mCurrentThreadMode) {
            throw new IllegalArgumentException("can't hot change iothread mode from " + mCurrentThreadMode + " to "
                    + mOkOptions.getIOThreadMode() + " in blocking io manager");
        }
    }

}
