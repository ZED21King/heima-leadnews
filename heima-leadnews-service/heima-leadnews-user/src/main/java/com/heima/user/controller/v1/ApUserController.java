package com.heima.user.controller.v1;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/9 21:50
 */
@RestController
@RequestMapping("/api/v1/login")
public class ApUserController {
    @Autowired
    ApUserService apUserService;

    @PostMapping("/login_auth")
    public ResponseResult<Map<String, Object>> login(@RequestBody LoginDto loginDto) {
        return apUserService.login(loginDto);
    }
}
