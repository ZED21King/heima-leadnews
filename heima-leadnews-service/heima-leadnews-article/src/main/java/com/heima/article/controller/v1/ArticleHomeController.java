package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/13 16:01
 */
@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {
    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/load")
    public ResponseResult<List<ApArticle>> load(@RequestBody ArticleHomeDto dto) {
        System.out.println("dto:" + dto);
        return apArticleService.load(dto, 1);
    }

    @PostMapping("/loadmore")
    public ResponseResult<List<ApArticle>> loadmore(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(dto, 1);
    }

    @PostMapping("/loadnew")
    public ResponseResult<List<ApArticle>> loadnew(@RequestBody ArticleHomeDto dto) {
        return apArticleService.load(dto, 2);
    }
}
