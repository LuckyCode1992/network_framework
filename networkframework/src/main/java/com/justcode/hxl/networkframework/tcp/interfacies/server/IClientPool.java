package com.justcode.hxl.networkframework.tcp.interfacies.server;


import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable;

public interface IClientPool<T, K> {

    void cache(T t);

    T findByUniqueTag(K key);

    int size();

    void sendToAll(ISendable sendable);
}
