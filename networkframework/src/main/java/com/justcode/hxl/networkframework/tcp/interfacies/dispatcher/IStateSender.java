package com.justcode.hxl.networkframework.tcp.interfacies.dispatcher;

import java.io.Serializable;



public interface IStateSender {

    void sendBroadcast(String action, Serializable serializable);

    void sendBroadcast(String action);
}
