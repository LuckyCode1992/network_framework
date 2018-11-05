package com.justcode.hxl.networkframework.tcp.socket.sdk.client;

import java.net.Socket;

public abstract class OkSocketFactory {

    public abstract Socket createSocket(ConnectionInfo info, OkSocketOptions options) throws Exception;

}
