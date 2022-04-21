package com.heima.article.test;

import com.heima.article.ArticleApplication;
import com.heima.common.minio.MinioTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest(classes = ArticleApplication.class)
public class MinIOTest {
    @Autowired
    private MinioTemplate minioTemplate;

    @Test
    public void testUploadFile() throws Exception {
        InputStream inputStream = new FileInputStream("d:/list.html");
        String url = minioTemplate.uploadHtmlFile("", "list.html", inputStream);
        System.out.println(url);
    }
}