package com.justcode.hxl.network_framework.tcpdemo.data;




import com.justcode.hxl.networkframework.tcp.socket.sdk.client.bean.IPulseSendable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class PulseBean implements IPulseSendable {
    private String str = "";

    public PulseBean() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 14);
            str = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] parse() {
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }
}