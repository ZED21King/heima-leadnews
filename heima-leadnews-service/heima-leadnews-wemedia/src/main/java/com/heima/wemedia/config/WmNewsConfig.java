package com.heima.wemedia.config;

import com.heima.model.config.TableFieldMetaObjectHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({"com.heima.common.minio","com.heima.common.exception","com.heima.common.aliyun"})
@Import({TableFieldMetaObjectHandler.class})
public class WmNewsConfig {
}