package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.AppHttpCodeEnum;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.exception.ResponseEnum;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.RsaUtils;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/17 17:38
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    @Value("${leadnews.jwt.privateKeyPath}")
    private String privateKeyPath;
    @Value("${leadnews.jwt.expire}")
    private Integer expire;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult login(WmLoginDto dto) {
        if (dto == null || StringUtils.isEmpty(dto.getName()) && StringUtils.isEmpty(dto.getPassword())) {
            throw new LeadNewsException(AppHttpCodeEnum.PARAM_INVALID);
        }

        QueryWrapper<WmUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", dto.getName());
        WmUser wmUser = this.getOne(queryWrapper);
        ResponseEnum.LOGIN_USER_NOT_FOUND.assertNotNull(dto, wmUser);

        wmUser.setPassword(null);

        try {
            PrivateKey privateKey = RsaUtils.getPrivateKey(this.privateKeyPath);
            String token = JwtUtils.generateTokenExpireInMinutes(wmUser, privateKey, expire);
            redisTemplate.opsForValue().set(token, wmUser.getId().toString(), 30, TimeUnit.MINUTES);

            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", wmUser);
            return ResponseResult.okResult(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}