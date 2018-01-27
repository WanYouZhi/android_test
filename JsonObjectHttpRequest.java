package com.huawei.broser.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * get and post Method HttpReQuest,return a object from jsonConvert.
 * <p>
 *
 * @author wWX531311
 * @date 2017/12/27
 */

public class JsonObjectHttpRequest<T> extends HttpRequest<T> {
    private final String mPostParams;

    public JsonObjectHttpRequest(String url, String postParams, HttpResult<T> callback) throws MalformedURLException {
        this(url, postParams, null, callback);
    }

    /**
     * Construct a Request object.
     *
     * @param url        The URL to make an HTTP request to.
     * @param postParams if is null do get ,else do post
     * @param callback   The callback run when the HTTP response is received.
     *                   The callback will be run on the main thread.
     * @throws MalformedURLException on invalid url
     */
    public JsonObjectHttpRequest(String url, String postParams, Map<String, String> headMap, HttpResult<T> callback) throws MalformedURLException {
        super(url, headMap, callback);
        mPostParams = postParams;
    }

    /**
     * Helper method to make an HTTP request.
     *
     * @param urlConnection The HTTP connection.
     */
    @Override
    public void writeToUrlConnection(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        if (mPostParams == null) {
            return;
        }
        urlConnection.setRequestMethod("POST");
        if (!mPostParams.isEmpty()) {
            OutputStream os = urlConnection.getOutputStream();
            os.write(mPostParams.getBytes("UTF-8"));
            os.close();
        }
    }

    /**
     * Helper method to read an HTTP response.
     *
     * @param is The InputStream.
     * @return An object representing the HTTP response.
     */
    @Override
    protected T readInputStream(InputStream is) throws IOException {
        String jsonString = readStreamToString(is);
        try {
            return (T) JsonUtil.fromGenericJson(jsonString, this.getClass());
        } catch (Exception error) {
            throw new IOException(error.toString());
        }
    }

    private static String readStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        return os.toString("UTF-8");
    }
}
