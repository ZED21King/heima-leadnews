package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;

public interface ApArticleService extends IService<ApArticle> {
    ResponseResult load(ArticleHomeDto dto, int type);

    ResponseResult<Long> saveArticle(ArticleDto dto);
}