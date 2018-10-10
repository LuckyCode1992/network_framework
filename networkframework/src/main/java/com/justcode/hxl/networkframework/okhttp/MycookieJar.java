package com.justcode.hxl.networkframework.okhttp;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * cookie接口实现
 */
public class MycookieJar implements CookieJar {
    private ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        //这里可以做cookie传递，保存等操作
        cookieStore.put(url.host(), cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        //加载新的cookies
        List<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
