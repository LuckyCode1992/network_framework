package com.justcode.hxl.networkframework.okhttp.httpcore.request;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import okhttp3.Request;

/**
 * get请求
 */
public class GetRequest {

    public static Request build(String url0, Map<String, String> map) {
        StringBuffer stringBuffer = new StringBuffer();
        if (map != null && map.size() > 0) {
            stringBuffer.append("?");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (!stringBuffer.toString().equals("?"))
                    stringBuffer.append("&");
                stringBuffer.append(entry.getKey());
                stringBuffer.append("=");
                stringBuffer.append(entry.getValue());
            }
        }
        String url = url0.concat(stringBuffer.toString());
        try {
            url = new String(url.getBytes("UTF-8"));//避免出现乱码，中文
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .get()//写不写均可，这里写，表示一下
                .build();
        return request;

    }
}
