package com.justcode.hxl.networkframework.tcp.interfacies.server;



import com.justcode.hxl.networkframework.tcp.interfacies.IReaderProtocol;
import com.justcode.hxl.networkframework.tcp.interfacies.client.IDisConnectable;
import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISender;

import java.io.Serializable;

public interface IClient extends IDisConnectable, ISender<IClient>, Serializable {

    String getHostIp();

    String getHostName();

    void setUniqueTag(String uniqueTag);

    String getUniqueTag();

    void setReaderProtocol(IReaderProtocol protocol);

    void addIOCallback(IClientIOCallback clientIOCallback);

    void removeIOCallback(IClientIOCallback clientIOCallback);

    void removeAllIOCallback();

}
