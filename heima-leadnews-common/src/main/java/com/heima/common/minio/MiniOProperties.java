package com.heima.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO配置属性类
 */
@Data
@ConfigurationProperties(
    prefix = "leadnews.minio" //加载yml文件中的对应配置，注意：属性类的每个属性名和yml文件的属性名保持一致
)
public class MiniOProperties {
    private String accessKey;//账户名称
    private String secretKey;//账户密码
    private String bucket;//桶名称
    private String endpoint;//MinIO连接地址
    private String readPath;//访问文件的地址
}