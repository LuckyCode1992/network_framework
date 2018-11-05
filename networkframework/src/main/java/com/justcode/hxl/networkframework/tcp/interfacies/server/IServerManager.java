package com.justcode.hxl.networkframework.tcp.interfacies.server;


import com.justcode.hxl.networkframework.tcp.interfacies.IIOCoreOptions;

public interface IServerManager<E extends IIOCoreOptions> extends IServerShutdown {

    void listen();

    void listen(E options);

    boolean isLive();

}
