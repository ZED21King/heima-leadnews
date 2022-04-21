package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.ResponseEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.config.Jwt;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.RsaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "ApUserServiceImpl")
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Autowired
    Jwt jwt;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult<Map<String, Object>> login( LoginDto loginDto) {

        ApUser loginUser;
        if (!StringUtils.isEmpty(loginDto.getPhone()) &&
                !StringUtils.isEmpty(loginDto.getPassword())) {

            QueryWrapper<ApUser> queryWrapper = new QueryWrapper<ApUser>();
            queryWrapper.eq("phone", loginDto.getPhone());
            loginUser = this.getOne(queryWrapper);

            //使用断言, 判断用户是否存在, 若存在, 密码是否错误
            ResponseEnum.LOGIN_USER_NOT_FOUND.assertNotNull(loginDto, loginUser);

            //载荷不放重要数据
            loginUser.setPassword(null);

        } else {
            //游客登录, id固定为0, 方便网关知道是否是游客登录
            log.debug("========访客登录========");
            loginUser = new ApUser();
            loginUser.setId(0);
        }

        try {
            //获取私钥
            PrivateKey privateKey = RsaUtils.getPrivateKey(jwt.getPrivateKeyPath());
            //利用Jwt生成Token
            String token = JwtUtils.generateTokenExpireInMinutes(loginUser, privateKey, 30);
            // 【往token中存储的应该是整个用户对象数据，所以这里需要把对象转换为json字符串存储】
            String tokenData = JSON.toJSONString(loginUser);
//            String tokenData = JsonUtils.toString(loginUser);
            redisTemplate.opsForValue().set(token, tokenData, 30, TimeUnit.MINUTES);

            //响应数据
            Map<String, Object> map = new HashMap<>(16);
            map.put("token", token);
            if (loginUser.getId() != 0) {
                map.put("user", loginUser);
            }
            return ResponseResult.okResult(map);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}