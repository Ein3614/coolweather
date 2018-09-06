package com.whut.ein3614.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 类描述：（请求网络数据）
 * 创建人：Created by Administrator on 2018/9/6.
 * 修改人：
 * 修改时间：
 */
public class HttpUtil {
    public static void sendOkHttpRequestByGet(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
