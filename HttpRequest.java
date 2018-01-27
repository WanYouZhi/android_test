// Copyright 2015 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
package com.huawei.broser.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;


/**
 * This class represents an HTTP request.
 * This is to be used as a base class for more specific request classes.
 *
 * @param <T> The type representing the request payload.
 */
abstract class HttpRequest<T> implements Runnable {
    private final URL mUrl;
    private final HttpResult<T> mCallback;
    private final Map<String, String> mHeadMap;

    public HttpRequest(String url, HttpResult<T> callback)
            throws MalformedURLException {
        this(url, null, callback);
    }

    /**
     * Construct a Request object.
     *
     * @param url      The URL to make an HTTP request to.
     * @param headMap  The headers to make an HTTP request to.
     * @param callback The callback run when the HTTP response is received.
     *                 The callback will be run on the main thread.
     * @throws MalformedURLException on invalid url
     */
    public HttpRequest(String url, Map<String, String> headMap, HttpResult<T> callback)
            throws MalformedURLException {
        mUrl = new URL(url);
        if (!mUrl.getProtocol().equals("http")
                && !mUrl.getProtocol().equals("https")) {
            throw new MalformedURLException("This is not a http or https URL: " + url);
        }
        mCallback = callback;
        mHeadMap = headMap == null ? HttpClient.getDefaultHead() : headMap;
    }

    /**
     * Make the HTTP request and parse the HTTP response.
     */
    @Override
    public void run() {
        // Setup some values
        HttpURLConnection urlConnection = null;
        T result = null;
        InputStream inputStream = null;
        int responseCode = 0;
        IOException ioException = null;

        // Make the request
        try {
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            setHeadMap(urlConnection);
            writeToUrlConnection(urlConnection);
            responseCode = urlConnection.getResponseCode();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            result = readInputStream(inputStream);
        } catch (IOException e) {
            ioException = e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        HttpClient.getMainHandler().post(new Response<>(responseCode, ioException, result, mCallback));
    }

    private void setHeadMap(HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> entry : mHeadMap.entrySet()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Helper method to make an HTTP request.
     *
     * @param urlConnection The HTTP connection.
     * @throws IOException on error
     */
    protected abstract void writeToUrlConnection(HttpURLConnection urlConnection)
            throws IOException;

    /**
     * Helper method to read an HTTP response.
     *
     * @param is The InputStream.
     * @return An object representing the HTTP response.
     * @throws IOException on error
     */
    protected abstract T readInputStream(InputStream is) throws IOException;
}
