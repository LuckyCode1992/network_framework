package com.justcode.hxl.networkframework.tcp.socket.impl.client.abilities;


import com.justcode.hxl.networkframework.tcp.socket.sdk.client.ConnectionInfo;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.connection.IConnectionManager;

public interface IConnectionSwitchListener {
    void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo, ConnectionInfo newInfo);
}
