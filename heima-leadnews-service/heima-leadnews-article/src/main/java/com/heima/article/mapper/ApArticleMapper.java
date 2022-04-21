package com.heima.article.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/13 15:53
 */
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    List<ApArticle> loadArticles(@Param("dto") ArticleHomeDto dto, @Param("type") int type);
}