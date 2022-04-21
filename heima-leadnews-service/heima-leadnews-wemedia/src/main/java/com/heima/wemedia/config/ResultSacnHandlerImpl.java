package com.heima.wemedia.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/20 17:32
 */
@Component
public class ResultSacnHandlerImpl implements ResultSacnHandler{
    @Override
    public boolean resultScan(Map<String, String> result) {
        if (result != null) {
            String suggestion = result.get("suggestion");
            //成功
            if (StrUtil.equals("pass", suggestion)) {
                return true;
            }

            //机器审有点疑惑，交给人工审
            else if (StrUtil.equals("review", suggestion)) {

            }

            //驳回
            else if (StrUtil.equals("block", suggestion)) {

            }
        }
        return false;
    }
}
