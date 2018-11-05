package com.justcode.hxl.networkframework.tcp.socket.impl.client.action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;


import com.justcode.hxl.networkframework.tcp.basic.bean.OriginalData;
import com.justcode.hxl.networkframework.tcp.interfacies.client.msg.ISendable;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IRegister;
import com.justcode.hxl.networkframework.tcp.interfacies.dispatcher.IStateSender;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.ConnectionInfo;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.OkSocketOptions;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.ISocketActionListener;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.bean.IPulseSendable;
import com.justcode.hxl.networkframework.tcp.socket.sdk.client.connection.IConnectionManager;
import com.justcode.hxl.networkframework.tcp.utils.SocketBroadcastManager;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_CONNECTION_FAILED;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_CONNECTION_SUCCESS;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_DATA;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_DISCONNECTION;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_PULSE_REQUEST;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_READ_COMPLETE;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_READ_THREAD_SHUTDOWN;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_READ_THREAD_START;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_WRITE_COMPLETE;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_WRITE_THREAD_SHUTDOWN;
import static com.justcode.hxl.networkframework.tcp.socket.sdk.client.action.IAction.ACTION_WRITE_THREAD_START;


/**
 * 状态机
 */
public class ActionDispatcher implements IRegister<ISocketActionListener, IConnectionManager>, IStateSender, Handler.Callback {
    /**
     * 线程回调管理Handler
     */
    private static final HandlerThread HANDLE_THREAD = new HandlerThread("dispatch_thread", Process.THREAD_PRIORITY_BACKGROUND);

    static {
        //启动分发线程
        HANDLE_THREAD.start();
    }

    /**
     * 每个连接一个广播管理器不会串
     */
    private SocketBroadcastManager mSocketBroadcastManager;
    /**
     * 除了广播还支持回调
     */
    private ConcurrentHashMap<ISocketActionListener, BroadcastReceiver> mResponseHandlerMap = new ConcurrentHashMap<>();
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 连接信息
     */
    private ConnectionInfo mConnectionInfo;
    /**
     * 连接管理器
     */
    private IConnectionManager mManager;
    /**
     * 处理线程回调
     */
    private Handler mHandleFromThread;


    public ActionDispatcher(Context context, ConnectionInfo info, IConnectionManager manager) {
        mContext = context.getApplicationContext();
        mManager = manager;
        mConnectionInfo = info;
        mSocketBroadcastManager = new SocketBroadcastManager(mContext);
        mHandleFromThread = new Handler(HANDLE_THREAD.getLooper(), this);
    }

    private IConnectionManager registerReceiver(BroadcastReceiver broadcastReceiver, String... action) {
        IntentFilter intentFilter = new IntentFilter();
        if (action != null) {
            for (int i = 0; i < action.length; i++) {
                intentFilter.addAction(action[i]);
            }
        }
        mSocketBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
        return mManager;
    }

    @Override
    public IConnectionManager registerReceiver(final ISocketActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            if (!mResponseHandlerMap.containsKey(socketResponseHandler)) {
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        dispatchActionToListener(context, intent, socketResponseHandler);
                    }
                };
                registerReceiver(broadcastReceiver,
                        ACTION_CONNECTION_FAILED,
                        ACTION_CONNECTION_SUCCESS,
                        ACTION_DISCONNECTION,
                        ACTION_READ_COMPLETE,
                        ACTION_READ_THREAD_SHUTDOWN,
                        ACTION_READ_THREAD_START,
                        ACTION_WRITE_COMPLETE,
                        ACTION_WRITE_THREAD_SHUTDOWN,
                        ACTION_WRITE_THREAD_START,
                        ACTION_PULSE_REQUEST);
                mResponseHandlerMap.put(socketResponseHandler, broadcastReceiver);
            }
        }
        return mManager;
    }

    private IConnectionManager unRegisterReceiver(BroadcastReceiver broadcastReceiver) {
        mSocketBroadcastManager.unregisterReceiver(broadcastReceiver);
        return mManager;
    }

    @Override
    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        BroadcastReceiver broadcastReceiver = mResponseHandlerMap.get(socketResponseHandler);
        mResponseHandlerMap.remove(socketResponseHandler);
        unRegisterReceiver(broadcastReceiver);
        return mManager;
    }

    /**
     * 分发收到的响应
     *
     * @param context
     * @param intent
     * @param responseHandler
     */
    private void dispatchActionToListener(Context context, Intent intent, ISocketActionListener responseHandler) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_CONNECTION_SUCCESS: {
                try {
                    responseHandler.onSocketConnectionSuccess(context, mConnectionInfo, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CONNECTION_FAILED: {
                try {
                    Exception exception = (Exception) intent.getSerializableExtra(ACTION_DATA);
                    responseHandler.onSocketConnectionFailed(context, mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_DISCONNECTION: {
                try {
                    Exception exception = (Exception) intent.getSerializableExtra(ACTION_DATA);
                    responseHandler.onSocketDisconnection(context, mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_COMPLETE: {
                try {
                    OriginalData data = (OriginalData) intent.getSerializableExtra(ACTION_DATA);
                    responseHandler.onSocketReadResponse(context, mConnectionInfo, action, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_THREAD_START:
            case ACTION_WRITE_THREAD_START: {
                try {
                    responseHandler.onSocketIOThreadStart(context, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_COMPLETE: {
                try {
                    ISendable sendable = (ISendable) intent.getSerializableExtra(ACTION_DATA);
                    responseHandler.onSocketWriteResponse(context, mConnectionInfo, action, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_THREAD_SHUTDOWN:
            case ACTION_READ_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) intent.getSerializableExtra(ACTION_DATA);
                    responseHandler.onSocketIOThreadShutdown(context, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_PULSE_REQUEST: {
                try {
                    IPulseSendable sendable = (IPulseSendable) intent.getSerializableExtra(ACTION_DATA);
                    responseHandler.onPulseSend(context, mConnectionInfo, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        OkSocketOptions option = mManager.getOption();
        if (option != null && option.isCallbackInThread()) {
            Message message = new Message();
            message.getData().putSerializable("serializable", serializable);
            message.getData().putString("action", action);
            mHandleFromThread.sendMessage(message);
        } else {
            Intent intent = new Intent(action);
            intent.putExtra(ACTION_DATA, serializable);
            mSocketBroadcastManager.sendBroadcast(intent);
        }
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        mConnectionInfo = connectionInfo;
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg == null) {
            return false;
        }
        String action = msg.getData().getString("action", "");
        if (TextUtils.isEmpty(action)) {
            return false;
        }
        Serializable serializable = msg.getData().getSerializable("serializable");
        Set<ISocketActionListener> listeners = mResponseHandlerMap.keySet();
        for (ISocketActionListener listener : listeners) {
            Intent intent = new Intent(action);
            intent.putExtra(ACTION_DATA, serializable);
            dispatchActionToListener(mContext, intent, listener);
        }
        return true;
    }

}
