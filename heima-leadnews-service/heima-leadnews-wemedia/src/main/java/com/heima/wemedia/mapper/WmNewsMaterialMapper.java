package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zed21@aliyun.com
 * @date 2022/3/18 10:50
 */
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    void saveNewsMaterial(@Param("newsId") Integer id, @Param("materialIds") List<Integer> materialIds, @Param("type") int type);
}
