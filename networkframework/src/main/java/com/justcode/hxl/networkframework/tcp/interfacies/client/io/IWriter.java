package com.justcode.hxl.networkframework.tcp.interfacies.client.io;


import com.justcode.hxl.networkframework.tcp.interfacies.IIOCoreOptions;
import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;

import java.io.OutputStream;



public interface IWriter<T extends IIOCoreOptions> {

    void initialize(OutputStream outputStream, IStateSender stateSender);

    boolean write() throws RuntimeException;

    void setOption(T option);

    void offer(ISendable sendable);

    void close();

}
