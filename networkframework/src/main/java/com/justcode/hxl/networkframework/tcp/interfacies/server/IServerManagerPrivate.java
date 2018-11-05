package com.justcode.hxl.networkframework.tcp.interfacies.server;

import android.content.Context;

import com.justcode.hxl.networkframework.tcp.interfacies.IIOCoreOptions;


public interface IServerManagerPrivate<E extends IIOCoreOptions> extends IServerManager<E> {
    void initServerPrivate(Context context, int serverPort);
}
