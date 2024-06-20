package com.customapi.fmkpl.okhttp;

import java.io.File;

public interface OkHttpDownLoadCallback {
    /**
     * 下载成功之后的文件
     */
    void onDownloadSuccess(File file);

    /**
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载异常信息
     */

    void onDownloadFailed(Exception e);
}
