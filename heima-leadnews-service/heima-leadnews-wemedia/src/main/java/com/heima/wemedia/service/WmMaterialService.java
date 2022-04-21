package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WmMaterialService extends IService<WmMaterial> {

    ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile);

    ResponseResult<List<WmMaterial>> findList(WmMaterialDto dto);

    ResponseResult<Object> deletePicture(Integer id);

    ResponseResult<Object> collect(Integer id);

    ResponseResult<Object> cancelCollect(Integer id);
}