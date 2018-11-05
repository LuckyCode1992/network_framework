package com.justcode.hxl.networkframework.tcp.interfacies;


import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable;

public interface IIOManager<E extends IIOCoreOptions> {
    void startEngine();

    void setOkOptions(E options);

    void send(ISendable sendable);

    void close();

    void close(Exception e);

}
