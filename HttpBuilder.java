package com.huawei.broser.http;

import android.app.Activity;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 *
 * @author wWX531311
 * @date 2017/12/27
 */

public class HttpBuilder {
    WeakReference<Activity> activity;
    private String url;
    private Map<String, String> params = new HashMap<>();
    private Executor executor;

    private HttpBuilder(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public HttpBuilder url(String url) {
        this.url = url;
        return this;
    }

    public HttpBuilder addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public HttpBuilder addParams(Map<String, String> map) {
        params.putAll(map);
        return this;
    }

    /**
     * 默认用HttpClient.THREAD_POOL_EXECUTOR
     * @param executor
     * @return
     */
    public HttpBuilder changeExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }

    private Map<String, String> head;

    /**
     * @param head http协议头
     * @return
     */
    public HttpBuilder setHeads(Map<String, String> head) {
        this.head = head;
        return this;
    }

    public <T> HttpBuilder get(HttpResult<T> callBack) {
        getGetUrl();
        doCall(null, callBack);
        return this;
    }

    private void getGetUrl() {
        url = getUrl(url, params);
    }

    public static String getUrl(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> item : params.entrySet()) {
            builder.append("&").append(urlEncode(item.getKey()))
                    .append("=").append(urlEncode(item.getValue()));
        }
        return url.contains("?") ? url + builder.toString()
                : url + builder.replace(0, 1, "?").toString();
    }

    public static String getPostString(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> item : params.entrySet()) {
            builder.append("&").append(item.getKey())
                    .append("=").append(item.getValue());
        }
        return builder.replace(0, 1, "").toString();
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    public <T> HttpBuilder post(HttpResult<T> callBack) {
        doCall(getPostString(params), callBack);
        return this;
    }

    private <T> void doCall(String postString, HttpResult<T> callBack) {
        if (executor == null) {
            executor = HttpClient.THREAD_POOL_EXECUTOR;
        }
        try {
            executor.execute(new JsonObjectHttpRequest<T>(url, postString, callBack));
            HttpClient.add(this);
        } catch (MalformedURLException e) {
            callBack.onFailed(e.getMessage());
        }
    }

    public void cancel() {

    }
}
