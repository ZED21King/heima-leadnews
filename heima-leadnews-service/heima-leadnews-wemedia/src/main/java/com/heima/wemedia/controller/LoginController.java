package com.heima.wemedia.controller;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/17 17:39
 */

import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 自媒体登录
 */
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private WmUserService wmUserService;

    /**
     * 登录
     */
    @PostMapping("/in")
    public ResponseResult<Map<String,Object>> login(@RequestBody WmLoginDto dto){
        return wmUserService.login(dto);
    }

}
