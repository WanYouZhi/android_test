package com.huawei.broser.http;

/**
 * The callback that gets run after the request is made.
 *
 * @author wWX531311
 * @date 2017/12/29
 */

public interface HttpResult<T> {
    /**
     * The callback run on a valid response.
     *
     * @param result The result object.
     */
    void onSuccess(T result);

    /**
     * The callback run on an Exception.
     *
     * @param msg The errorMsg
     */
    void onFailed(String msg);
}
