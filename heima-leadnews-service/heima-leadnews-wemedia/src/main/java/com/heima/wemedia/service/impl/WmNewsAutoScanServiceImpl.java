package com.heima.wemedia.service.impl;

import cn.hutool.core.util.StrUtil;
import com.heima.common.aliyun.AliyunGreenContentScan;
import com.heima.common.minio.MinioTemplate;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private AliyunGreenContentScan aliyunGreenContentScan;

    @Override
    public void autoScanWmNews(Integer newsId) {
        //根据id查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(newsId);

        //从文章中提取出文本和图片
        List<String> textList = this.getTextFromNews(wmNews);
        List<byte[]> imageList = this.getImageFromNews(wmNews);

        //提交文本给阿里云内容检测
        //根据阿里云的反馈结果进行处理
        if (!Collections.isEmpty(textList)) {
            try {
                Map<String, String> result = aliyunGreenContentScan.greeTextScan(textList);
                if (!this.resultScan(result, wmNews)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //提交图片给阿里云内容检测
        //根据阿里云的反馈结果进行处理
        if (!Collections.isEmpty(imageList)) {
            try {
                Map<String, String> result = aliyunGreenContentScan.imageListScan(imageList);
                if (!this.resultScan(result, wmNews)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //判断发布时间是否大于当前时间，状态改为8（待发布）
        Date publishTime = wmNews.getPublishTime();


        //保存App端文章，同时更新自媒体状态为9

    }

    public boolean resultScan(Map<String, String> result, WmNews wmNews) {
        if (result != null) {
            String suggestion = result.get("suggestion");
            //成功
            if (StrUtil.equals("pass", suggestion)) {
                return true;
            }

            //机器审有点疑惑，交给人工审
            else if (StrUtil.equals("review", suggestion)) {

            }

            //驳回
            else if (StrUtil.equals("block", suggestion)) {

            }
        }
        return false;
    }

    private List<byte[]> getImageFromNews(WmNews wmNews) {
        return null;
    }

    private List<String> getTextFromNews(WmNews wmNews) {
        return null;
    }
}