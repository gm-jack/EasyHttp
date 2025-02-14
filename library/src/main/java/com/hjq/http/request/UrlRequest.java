package com.hjq.http.request;

import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CacheMode;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpParams;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2020/10/07
 *    desc   : 不带 RequestBody 的请求
 */
public abstract class UrlRequest<T extends UrlRequest<?>> extends HttpRequest<T> {

    public UrlRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    @Override
    protected Request createRequest(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        Request.Builder requestBuilder = new Request.Builder();

        if (tag != null) {
            requestBuilder.tag(tag);
        }

        // 如果设置了不缓存数据
        if (getRequestCache().getCacheMode() == CacheMode.NO_CACHE) {
            requestBuilder.cacheControl(new CacheControl.Builder().noCache().build());
        }

        // 添加请求头
        if (!headers.isEmpty()) {
            for (String key : headers.getKeys()) {
                requestBuilder.addHeader(key, headers.get(key));
            }
        }

        HttpUrl.Builder urlBuilder = HttpUrl.get(url).newBuilder();
        // 添加参数
        if (!params.isEmpty()) {
            for (String key : params.getKeys()) {
                urlBuilder.addQueryParameter(key, String.valueOf(params.get(key)));
            }
        }
        HttpUrl link = urlBuilder.build();
        requestBuilder.url(link);
        requestBuilder.method(getRequestMethod(), null);

        EasyLog.printKeyValue(this, "RequestUrl", String.valueOf(link));
        EasyLog.printKeyValue(this, "RequestMethod", getRequestMethod());

        // 打印请求头和参数的日志
        if (EasyConfig.getInstance().isLogEnabled()) {

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.printLine(this);
            }

            for (String key : headers.getKeys()) {
                EasyLog.printKeyValue(this, key, headers.get(key));
            }

            if (!headers.isEmpty() && !params.isEmpty()) {
                EasyLog.printLine(this);
            }

            for (String key : params.getKeys()) {
                EasyLog.printKeyValue(this, key, String.valueOf(params.get(key)));
            }

            if (!headers.isEmpty() || !params.isEmpty()) {
                EasyLog.printLine(this);
            }
        }

        return requestBuilder.build();
    }
}