package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.AppHttpCodeEnum;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.exception.ResponseEnum;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.utils.common.BeanHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public ResponseResult load(ArticleHomeDto dto, int type) {
        this.check(dto);
        List<ApArticle> list = apArticleMapper.loadArticles(dto, type);
        ResponseEnum.OBJECT_NONNUL.assertNotNull(list);
        return ResponseResult.okResult(list);
    }

    @Override
    public ResponseResult<Long> saveArticle(ArticleDto dto) {
        //保存App文章（判断App文章添加还是修改），因为App文章可以通过自媒体后台进行修改
        /**
         * 保存三张表：
         *   ap_article
         *   ap_article_config
         *   ap_article_content
         */
        if (dto == null) {
            throw new LeadNewsException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //复制dto的属性
        ApArticle article = BeanHelper.copyProperties(dto, ApArticle.class);
        if (article == null) {
            throw new LeadNewsException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //如果没有app文章的id则表明，是正要发布的文章，也就是新增
        if (dto.getId() == null) {
            //1.保存文章
            this.save(article);

            //2.保存文章配置
            ApArticleConfig articleConfig = new ApArticleConfig();
            articleConfig.setArticleId(article.getId());
            articleConfig.setIsComment(true);
            articleConfig.setIsForward(true);
            articleConfig.setIsDown(false);
            articleConfig.setIsDelete(false);
            apArticleConfigMapper.insert(articleConfig);

            //3.异步保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(article.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        }

        //如果app文章的id不为空，则证明已经发布过文章，也就是要修改文章
        else {
            //修改不会改变文章配置表
            //1.修改文章
            this.saveOrUpdate(article);

            //2.修改文章内容
            QueryWrapper<ApArticleContent> qw = new QueryWrapper<>();
            qw.eq("article_id", article.getId());
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(qw);
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        }

        //生成静态页


        //返回app文章id，为雪花算法的id
        return ResponseResult.okResult(article.getId());
    }

    private void check(ArticleHomeDto dto) {
        ResponseEnum.OBJECT_NONNUL.assertNotNull(dto);

        if (dto.getSize() == null) {
            dto.setSize(10);
        }
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
    }
}