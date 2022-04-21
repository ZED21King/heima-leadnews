package com.heima.article.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

@Slf4j
//@Component
public class TableFieldMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill......");
        LocalDateTime dateTime = LocalDateTime.now(); //线程安全的日期类
        this.setFieldValByName("createTime", dateTime, metaObject);
        this.setFieldValByName("updateTime", dateTime, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill......");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}