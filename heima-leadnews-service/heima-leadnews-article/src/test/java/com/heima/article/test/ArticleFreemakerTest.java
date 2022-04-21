package com.heima.article.test;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.common.minio.MinioTemplate;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.utils.common.JsonUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 演示生成文章详情静态页
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArticleApplication.class)
public class ArticleFreemakerTest {
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private Configuration configuration;
    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private ApArticleMapper apArticleMapper;


    /**
     * 读取文章内容，生成静态页，并存入MinIO
     */
    @Test
    public void createArticlePage() throws Exception {
        Long articleId = 1383827787629252610L;

        //读取指定文章内容
        QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);

        //如果文章内容不为空，则利用Freemarker生成该文章的静态页面
        if(apArticleContent!=null && StringUtils.isNotEmpty(apArticleContent.getContent())){

            //读取静态模板文件
            Template template = configuration.getTemplate("article.ftl");

            //准备文章内容数据, 文章内容是一个Json集合, 包含一个Map
            //[{"text",""},{"content",""}]
            Map<String,Object> data = new HashMap<>();
            List<Map> content = JsonUtils.toList(apArticleContent.getContent(),Map.class);
            data.put("content",content);

            //把静态文件内容写入临时字符流中
            StringWriter writer = new StringWriter();
            template.process(data, writer);

            //把文章静态页文件上传MinIO
            InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());
            String url = minioTemplate.uploadHtmlFile("article",articleId.toString(), inputStream);

            //更新文章表的static_url字段
            ApArticle apArticle = new ApArticle();
            apArticle.setId(articleId);
            apArticle.setStaticUrl(url);
            apArticleMapper.updateById(apArticle);
        }

    }
}