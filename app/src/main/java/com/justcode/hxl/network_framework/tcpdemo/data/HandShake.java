package com.justcode.hxl.network_framework.tcpdemo.data;

import org.json.JSONException;
import org.json.JSONObject;



public class HandShake extends DefaultSendBean {

    public HandShake() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", 54);
            jsonObject.put("handshake", "Hello I'm a OkSocket demo");
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
