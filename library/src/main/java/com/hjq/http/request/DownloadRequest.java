package com.hjq.http.request;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.hjq.http.EasyLog;
import com.hjq.http.EasyUtils;
import com.hjq.http.callback.DownloadCallback;
import com.hjq.http.config.RequestApi;
import com.hjq.http.config.RequestServer;
import com.hjq.http.lifecycle.HttpLifecycleManager;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.listener.OnHttpListener;
import com.hjq.http.model.BodyType;
import com.hjq.http.model.CallProxy;
import com.hjq.http.model.FileContentResolver;
import com.hjq.http.model.HttpHeaders;
import com.hjq.http.model.HttpMethod;
import com.hjq.http.model.HttpParams;
import com.hjq.http.model.ResponseClass;

import java.io.File;

import okhttp3.Request;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/EasyHttp
 *    time   : 2019/07/20
 *    desc   : 下载请求
 */
public final class DownloadRequest extends HttpRequest<DownloadRequest> {

    /** 下载请求方式 */
    private HttpMethod mMethod = HttpMethod.GET;

    /** 保存的文件 */
    private File mFile;

    /** 校验的 md5 */
    private String mMd5;

    /** 下载监听回调 */
    private OnDownloadListener mListener;

    /** 请求执行对象 */
    private CallProxy mCallProxy;

    public DownloadRequest(LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
    }

    /**
     * 设置请求方式
     */
    public DownloadRequest method(HttpMethod method) {
        mMethod = method;
        return this;
    }

    /**
     * 设置下载地址
     */
    public DownloadRequest url(String url) {
        server(new RequestServer(url));
        api(new RequestApi(""));
        return this;
    }

    /**
     * 设置保存的路径
     */

    public DownloadRequest file(String filePath) {
        return file(new File(filePath));
    }

    public DownloadRequest file(File file) {
        mFile = file;
        return this;
    }

    public DownloadRequest file(ContentResolver resolver, Uri uri) {
        return file(new FileContentResolver(resolver, uri));
    }

    public DownloadRequest file(FileContentResolver file) {
        mFile = file;
        return this;
    }

    /**
     * 设置 MD5 值
     */
    public DownloadRequest md5(String md5) {
        mMd5 = md5;
        return this;
    }

    /**
     * 设置下载监听
     */
    public DownloadRequest listener(OnDownloadListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    protected Request createRequest(String url, String tag, HttpParams params, HttpHeaders headers, BodyType type) {
        switch (mMethod) {
            case GET:
                // 如果这个下载请求方式是 Get
                return new GetRequest(getLifecycleOwner()).api(getRequestApi()).createRequest(url, tag, params, headers, type);
            case POST:
                // 如果这个下载请求方式是 Post
                return new PostRequest(getLifecycleOwner()).api(getRequestApi()).createRequest(url, tag, params, headers, type);
            default:
                throw new IllegalStateException("method nonsupport");
        }
    }

    /**
     * 开始下载
     */
    public DownloadRequest start() {
        long delayMillis = getDelayMillis();
        if (delayMillis > 0) {
            // 打印请求延迟时间
            EasyLog.printKeyValue(this, "RequestDelay", String.valueOf(delayMillis));
        }

        StackTraceElement[] stackTrace = new Throwable().getStackTrace();

        EasyUtils.postDelayed(() -> {
            if (!HttpLifecycleManager.isLifecycleActive(getLifecycleOwner())) {
                // 宿主已被销毁，请求无法进行
                EasyLog.printLog(this, "LifecycleOwner has been destroyed and the request cannot be made");
                return;
            }
            EasyLog.printStackTrace(this, stackTrace);
            mCallProxy = new CallProxy(createCall());
            new DownloadCallback(this)
                    .setFile(mFile)
                    .setMd5(mMd5)
                    .setListener(mListener)
                    .setCall(mCallProxy)
                    .start();
        }, delayMillis);

        return this;
    }

    /**
     * 取消下载
     */
    public DownloadRequest stop() {
        if (mCallProxy != null) {
            mCallProxy.cancel();
        }
        return this;
    }

    @Override
    public void request(OnHttpListener<?> listener) {
        // 请调用 start 方法
        throw new IllegalStateException("Call the start method");
    }

    @Override
    public <Bean> Bean execute(ResponseClass<Bean> responseClass) {
        // 请调用 start 方法
        throw new IllegalStateException("Call the start method");
    }

    @Override
    public DownloadRequest cancel() {
        // 请调用 stop 方法
        throw new IllegalStateException("Call the start method");
    }

    @NonNull
    @Override
    public String getRequestMethod() {
        return String.valueOf(mMethod);
    }
}