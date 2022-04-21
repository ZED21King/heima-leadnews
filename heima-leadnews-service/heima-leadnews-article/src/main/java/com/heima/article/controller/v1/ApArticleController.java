package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * App文章
 */
@RestController
@RequestMapping("/api/v1/article")
public class ApArticleController {
    @Autowired
    private ApArticleService apArticleService;
    /**
     * 保存App文章
     */
    @PostMapping("/save")
    public ResponseResult<Long> saveArticle(@RequestBody ArticleDto dto){
        return apArticleService.saveArticle(dto);
    }
}