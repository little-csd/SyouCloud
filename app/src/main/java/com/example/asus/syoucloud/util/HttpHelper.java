package com.example.asus.syoucloud.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpHelper {

    public static void sendLyricDownloadRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
