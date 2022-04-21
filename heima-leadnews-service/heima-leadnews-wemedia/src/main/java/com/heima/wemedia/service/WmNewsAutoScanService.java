package com.heima.wemedia.service;

public interface WmNewsAutoScanService {
    /**
     * 自媒体文章审核
     * @param newsId  自媒体文章id
     */
    void autoScanWmNews(Integer newsId);
}