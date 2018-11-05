package com.justcode.hxl.network_framework.tcpdemo;


import com.justcode.hxl.networkframework.tcp.socket.sdk.client.ConnectionInfo;

public class RedirectException extends RuntimeException {
    public ConnectionInfo redirectInfo;

    public RedirectException(ConnectionInfo redirectInfo) {
        this.redirectInfo = redirectInfo;
    }
}
