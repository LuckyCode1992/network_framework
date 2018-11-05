package com.justcode.hxl.networkframework.tcp.interfacies.server;


import com.justcode.hxl.networkframework.tcp.basic.bean.OriginalData;
import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable;

public interface IClientIOCallback {

    void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool);

    void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool);

}
