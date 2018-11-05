package com.justcode.hxl.networkframework.tcp.interfacies.client.io;




import com.justcode.hxl.networkframework.tcp.interfacies.IIOCoreOptions;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;

import java.io.InputStream;



public interface IReader<T extends IIOCoreOptions> {

    void initialize(InputStream inputStream, IStateSender stateSender);

    void read() throws RuntimeException;

    void setOption(T option);

    void close();
}
