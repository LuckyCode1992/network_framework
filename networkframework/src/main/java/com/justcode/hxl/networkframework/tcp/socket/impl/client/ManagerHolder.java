package com.justcode.hxl.networkframework.tcp.socket.impl.client;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.util.SparseArray;


import com.justcode.hxl.networkframework.tcp.interfacies.server.IServerManager;
import com.justcode.hxl.networkframework.tcp.interfacies.server.IServerManagerPrivate;
import com.justcode.hxl.networkframework.tcp.socket.impl.client.abilities.IConnectionSwitchListener;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.ConnectionInfo;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.OkSocketOptions;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.connection.IConnectionManager;
import com.justcode.hxl.networkframework.tcp.utils.SLog;
import com.justcode.hxl.networkframework.tcp.utils.SPIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Keep
public class ManagerHolder {

    private Map<ConnectionInfo, IConnectionManager> mConnectionManagerMap = new HashMap<>();

    private SparseArray<IServerManagerPrivate> mServerManagerMap = new SparseArray<>();

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();
    }

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        mConnectionManagerMap.clear();
    }

    public IServerManager getServer(int localPort, Context context) {
        IServerManagerPrivate manager = mServerManagerMap.get(localPort);
        if (manager == null) {
            manager = (IServerManagerPrivate) SPIUtils.load(IServerManager.class);
            if (manager == null) {
                SLog.e("Server load error. Server plug-in are required!" +
                        " For details link to https://github.com/xuuhaoo/OkSocket");
            } else {
                synchronized (mServerManagerMap) {
                    mServerManagerMap.append(localPort, manager);
                }
                manager.initServerPrivate(context, localPort);
                return manager;
            }
        }
        return manager;
    }

    public IConnectionManager getConnection(ConnectionInfo info, Context context) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager == null) {
            return getConnection(info, context, OkSocketOptions.getDefault());
        } else {
            return getConnection(info, context, manager.getOption());
        }
    }

    public IConnectionManager getConnection(ConnectionInfo info, Context context, OkSocketOptions okOptions) {
        IConnectionManager manager = mConnectionManagerMap.get(info);
        if (manager != null) {
            if (!okOptions.isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(info);
                }
                return createNewManagerAndCache(info, context, okOptions);
            } else {
                manager.option(okOptions);
            }
            return manager;
        } else {
            return createNewManagerAndCache(info, context, okOptions);
        }
    }

    @NonNull
    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, Context context, OkSocketOptions okOptions) {
        AbsConnectionManager manager = new ConnectionManagerImpl(context, info);
        manager.option(okOptions);
        manager.setOnConnectionSwitchListener(new IConnectionSwitchListener() {
            @Override
            public void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo,
                                               ConnectionInfo newInfo) {
                synchronized (mConnectionManagerMap) {
                    mConnectionManagerMap.remove(oldInfo);
                    mConnectionManagerMap.put(newInfo, manager);
                }
            }
        });
        synchronized (mConnectionManagerMap) {
            mConnectionManagerMap.put(info, manager);
        }
        return manager;
    }

    protected List<IConnectionManager> getList() {
        List<IConnectionManager> list = new ArrayList<>();
        Iterator<ConnectionInfo> it = mConnectionManagerMap.keySet().iterator();
        while (it.hasNext()) {
            ConnectionInfo info = it.next();
            IConnectionManager manager = mConnectionManagerMap.get(info);
            if (!manager.getOption().isConnectionHolden()) {
                synchronized (mConnectionManagerMap) {
                    it.remove();
                }
                continue;
            }
            list.add(manager);
        }
        return list;
    }


}
