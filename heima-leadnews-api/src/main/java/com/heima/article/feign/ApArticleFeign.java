package com.heima.article.feign;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * App文章Feign接口
 */
@FeignClient(value = "leadnews-article", path = "/api/v1/article")
public interface ApArticleFeign {

    /**
     * 保存App文章
     */
    @PostMapping("/save")
    ResponseResult<Long> saveArticle(@RequestBody ArticleDto dto);
}