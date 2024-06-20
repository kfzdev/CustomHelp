package com.customapi.fmkpl.okhttp;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.customapi.fmkpl.R;
import com.customapi.fmkpl.customviews.MyApplication;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpClientManager {
    private static final String TAG = "OkHttpClientManager";

    private static OkHttpClientManager mInstance;
    private static OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    //默认的请求回调类
    private final OkHttpCallback<String> DEFAULT_RESULT_CALLBACK = new OkHttpCallback<String>() {

        @Override
        public void onSuccess(String response, Headers headers) {

        }

        @Override
        public void onFailure(BaseEntity<String> entity, String message, int responseCode) {

        }
    };

    private OkHttpClientManager() {
        mOkHttpClient = new OkHttpClient().newBuilder()
                .callTimeout(20,TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)// 失败后是否重试
                .cookieJar(CookieJar.NO_COOKIES)
                .build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        if (mOkHttpClient == null) return;
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        if (tag == null || mOkHttpClient == null) return;
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param callback
     * @param tag
     */
    public void getAsyn(String url, final OkHttpCallback callback, Object tag) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .tag(tag)
                .get()
                .build();
        deliveryResult(callback, request);
    }



    /**
     * 请求参数表单创建(请求参数转为json字符串，字段名为data)
     *
     * @param url
     * @param params
     * @param tag
     * @return
     */
    private Request buildPostRequest(String url, Map<String, String> params, String headKey, String headValue, Object tag) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                builder.add(key, params.get(key));
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url)
                .post(builder.build())
                .tag(tag);
        // 添加token
        requestBuilder
                .addHeader("Connection", "close");
        // 添加header
//        if (!StringUtils.getInstance().isEmpty(headKey) && !StringUtils.getInstance().isEmpty(headValue)) {
//            requestBuilder.addHeader(headKey, headValue);
//        }
        return requestBuilder.build();
    }


    /**
     * 传参下载文件
     *
     * @param url
     * @param params
     * @param fileDir
     * @param fileName
     * @param callback
     * @param tag
     */
    public void postAsynDownload(String url, Map<String, String> params, String fileDir, String fileName, final OkHttpDownLoadCallback callback, Object tag) {
        Request request = buildPostRequest(url, params, null, null, tag);
        downloadResult(fileDir, fileName, callback, request);
    }

    /**
     * 不传参get下载文件
     *
     * @param url
     * @param fileDir
     * @param fileName
     * @param callback
     * @param tag
     */
    public void getAsynDownload(String url, String fileDir, String fileName, final OkHttpDownLoadCallback callback, Object tag) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        downloadResult(fileDir, fileName, callback, request);
    }

    /**
     * 请求参数表单创建(请求参数转为json字符串，字段名为data)
     *
     * @param url
     * @param params
     * @param tag
     * @return
     */
    private Request buildPostFormRequest(String url, Map<String, Object> params, String headKey, String headValue, Object tag) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params == null) {
            builder.add("data", "");
        } else {
            String data = mGson.toJson(params);
            builder.add("data", data);
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url)
                .post(builder.build())
                .tag(tag);
        return requestBuilder.build();
    }

    /**
     * 通用基础的异步的post请求
     *
     * @param url
     * @param callback
     * @param tag
     */
    public void postAsyn(String url, Map<String, String> params, final OkHttpCallback callback, Object tag) {
        Request request = buildPostRequest(url, params, null, null, tag);
        deliveryResult(callback, request);
    }


    public void postJsonAsyn(String url, LinkedHashMap<String, String> param, final OkHttpCallback callback) {
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : param.keySet()) {
            Object obj = param.get(key);
            if (obj != null) {
                builder.addEncoded(key, param.get(key).toString());
            } else {
                builder.addEncoded(key, "");
            }
        }
        FormBody  requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        deliveryResult(callback, request);
    }

    public void putJsonAsyn(String url, String jsonParams, final OkHttpCallback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .put(body)
                .build();
        deliveryResult(callback, request);
    }

    /**
     * 上传文件
     *
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public void uploadFile(String url, String pathName, String fileName, OkHttpCallback callback) {
        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        //创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MEDIA_TYPE.type(), fileName, RequestBody.create(MEDIA_TYPE, new File(pathName)));
        //发出请求参数
        Request request = new Request.Builder()
//                .header("Authorization", "Client-ID " + "9199fdef135c122")
                .url(url)
                .post(builder.build())
                .build();
        deliveryResult(callback, request);
    }

    private static String judgeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public void downloadResult(final String destFileDir, final String destFileName, final OkHttpDownLoadCallback listener, final Request request) {

        //异步请求
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
//                    if (AppMgrUtils.getInstance().isDev()) {
//                        LogUtils.i("file size:" + (total * 1.0f) / 1024 + " KB");
//                    }
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onDownloadSuccess(file);
                } catch (Exception e) {
                    listener.onDownloadFailed(e);
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });
    }

    public void delete(String url, final OkHttpCallback callback){
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .delete()
                .build();
        deliveryResult(callback, request);

    }


    /**
     * 请求回调处理方法并传递返回值
     *
     * @param callback Map类型请求参数
     * @param request  Request请求
     */
    private void deliveryResult(OkHttpCallback callback, final Request request) {
        if (callback == null)
            callback = DEFAULT_RESULT_CALLBACK;
        final OkHttpCallback resCallBack = callback;
        //UI thread
        callback.onBefore(request);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedStringCallback(request, "e",500, resCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String responseMessage = response.message();
                    final String responseBody = response.body().string();
                    final int responseCode = response.code();
                    if(responseCode<=300&&responseCode>=200){
//                        response.headers().get("x-page");
                        if (resCallBack.mType == String.class) {
                            sendSuccessResultCallback(responseBody,response.headers(), resCallBack);
                        }else if(resCallBack.mType == JSONObject.class){
                            try {
                                JSONObject result = new JSONObject(responseBody);
                                sendSuccessResultCallback(result,response.headers(), resCallBack);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Object o = mGson.fromJson(responseBody, resCallBack.mType);
                            sendSuccessResultCallback(o,response.headers(), resCallBack);
                        }
                    }else{

                        if(responseCode==401||responseCode==403){
//                            DiaLog.NormalDiaLog("認證信息過期，請重新登錄");
//                            startActivity(new Intent(MyApplication.getContext(), LoginActivity.class));
                            UnauthorizedCallback(resCallBack);
                            cancelAll();
                        }else if(responseCode==422){
                            sendFailedStringCallback(request, responseBody,responseCode, resCallBack);
                        }else{
                            sendFailedStringCallback(request, "e",responseCode, resCallBack);
                        }
                    }

                } catch (IOException | com.google.gson.JsonParseException e) {
//                    sendFailedStringCallback(response.request(), BaseApplication.getContext().getString(R.string.error_json_parse), resCallBack);
                }
            }
        });
    }



    /**
     * 处理请求成功的回调信息方法
     *
     * @param object   服务器响应信息
     * @param callback 回调类
     */
    private void sendSuccessResultCallback(final Object object, final Headers headers, final OkHttpCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(object,headers);
                callback.onAfter();
             }
        });
    }

    private void sendSuccessResultCallback(final JSONObject object, final Headers headers, final OkHttpCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(object,headers);
                callback.onAfter();
            }
        });
    }

    private void sendFailedStringCallback(final Request request, final String message, int responseCode, final OkHttpCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
//                callback.onFailure(null, e.getMessage());
                callback.onFailure(null, message,responseCode);
            }
        });
    }

    private void UnauthorizedCallback(final OkHttpCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
//                callback.onFailure(null, e.getMessage());
                callback.onFailure(null, "unauthorized",401);
            }
        });
    }



//    public class TokenInterceptor implements Interceptor {
//        private static final String TAG = "TokenInterceptor";
//
//        @Override
//
//        public Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//
//            Response response = chain.proceed(request);
//
//            Log.d(TAG, "response.code=" + response.code());
//
////根据和服务端的约定判断token过期
//
//            if (isTokenExpired(response)) {
//                Log.d(TAG, "自动刷新Token,然后重新请求数据");
//
////同步请求方式，获取最新的Token
//
//                String newToken = getNewToken();
//
////使用新的Token，创建新的请求
//
//                Request newRequest = chain.request()
//
//                        .newBuilder()
//
//                        .header("Authorization", "Basic " + newToken)
//
//                        .build();
//
////重新请求
//
//                return chain.proceed(newRequest);
//
//            }
//
//            return response;
//
//        }
//    }
}
