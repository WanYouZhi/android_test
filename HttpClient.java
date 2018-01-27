package com.huawei.broser.http;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * The callback that gets run after the request is made.
 * <p>
 *
 * @author wWX531311
 * @date 2017/12/27
 */

public class HttpClient {
    //保存未返回的builder，留remove用
    private static final Set<HttpBuilder> builders = new HashSet<>();
    public static final Executor THREAD_POOL_EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
    public static final Executor SERIAL_EXECUTOR = AsyncTask.SERIAL_EXECUTOR;

    private HttpClient() {

    }

    public static Map<String, String> getDefaultHead() {
        return null;
    }

    public static void remove(Activity activity) {
        if (activity == null) {
            return;
        }

        //防止ConcurrentModification
        List<HttpBuilder> removeBuilders = new ArrayList<>();
        for (HttpBuilder builder : builders) {
            if (activity == builder.activity.get()) {
                removeBuilders.add(builder);
            }
        }
        for (HttpBuilder builder : removeBuilders) {
            remove(builder);
        }
    }

    public static boolean add(HttpBuilder builder){
       return builders.add(builder);
    }

    public static void remove(HttpBuilder builder) {
        if (builder == null) {
            return;
        }

        builder.cancel();
        builders.remove(builder);
    }

    public static Handler getMainHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
