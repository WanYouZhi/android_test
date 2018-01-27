package com.huawei.broser.http;

import android.util.Log;

import java.io.IOException;

/**
 *
 * @author wWX531311
 * @date 2018/1/2
 */

public class Response<T> implements Runnable {
    private static final String TAG = "Response";
    private int code;
    private IOException ioException;
    private T result;
    private HttpResult<T> httpResult;

    public Response(int code, IOException ioException, T result,HttpResult<T> httpResult) {
        this.code = code;
        this.ioException = ioException;
        this.result = result;
        this.httpResult =httpResult;
    }

    @Override
    public void run() {
        if (ioException == null) {
            httpResult.onSuccess(result);
        } else {
            Log.e(TAG, "onFailed: code=" + code);
            httpResult.onFailed(ioException.getMessage());
        }
    }
}
