package com.heima.schedule.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/30 13:52
 */
@Slf4j
@Component
public class ArticleXxlJob {

    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");
        System.out.println("开始执行定时任务...");
    }
}
