package com.qinggan.app.arielapp.minor.main.navigation;

import okhttp3.OkHttpClient;

public class HttpClient {
    public static final OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getOkHttpClient() {
        return client;
    }
}
