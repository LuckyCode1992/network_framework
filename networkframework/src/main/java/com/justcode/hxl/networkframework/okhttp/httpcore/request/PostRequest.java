package com.justcode.hxl.networkframework.okhttp.httpcore.request;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * post请求构建
 */
public class PostRequest {

    /**
     * 普通post请求
     *
     * @param url
     * @param map
     */
    public static Request Build(String url, Map<String, String> map) {

       FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体


       // RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), JSON.toJSONString(map));

        if (map != null && map.size() > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .post(formBody.build())//传递请求体
                .build();
        return request;
    }

    /**
     * 带文件post请求
     */

    public static Request Build(String url, HashMap<String, Object> map) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        for (String key : map.keySet()) {
            Object object = map.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
            }
        }
        //创建RequestBody
        RequestBody body = builder.build();
        Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .post(body)//传递请求体
                .build();
        return request;
    }


}
