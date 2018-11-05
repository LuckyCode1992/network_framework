package com.justcode.hxl.networkframework.tcp.socket.impl.iocore;

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;


import com.justcode.hxl.networkframework.tcp.interfacies.IIOCoreOptions;
import com.justcode.hxl.networkframework.tcp.interfacies.client.io.IReader;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;

import java.io.IOException;
import java.io.InputStream;


public abstract class AbsReader implements IReader<IIOCoreOptions> {

    protected IIOCoreOptions mOkOptions;

    protected IStateSender mStateSender;

    protected InputStream mInputStream;

    public AbsReader() {
    }

    @CallSuper
    @Override
    public void initialize(InputStream inputStream, IStateSender stateSender) {
        mStateSender = stateSender;
        mInputStream = inputStream;
    }

    @Override
    @MainThread
    public void setOption(IIOCoreOptions option) {
        mOkOptions = option;
    }


    @Override
    public void close() {
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }
}
