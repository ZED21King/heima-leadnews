package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;

import java.util.Map;

public interface ApUserService extends IService<ApUser> {

    ResponseResult<Map<String, Object>> login(LoginDto loginDto);
}
