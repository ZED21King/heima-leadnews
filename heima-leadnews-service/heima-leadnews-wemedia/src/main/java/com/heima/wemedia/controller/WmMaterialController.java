package com.heima.wemedia.controller;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 素材
 */
@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {
    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传素材
     */
    @PostMapping("/upload_picture")
    public ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile) {
        return wmMaterialService.uploadPicture(multipartFile);
    }

    /**
     * 查询素材列表
     */
    @PostMapping("/list")
    public ResponseResult<List<WmMaterial>> findList(@RequestBody WmMaterialDto dto) {
        return wmMaterialService.findList(dto);
    }

    @GetMapping("/del_picture/{id}")
    public ResponseResult<Object> deletePicture(@PathVariable("id") Integer id) {
        return wmMaterialService.deletePicture(id);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult<Object> collect(@PathVariable("id") Integer id) {
        return wmMaterialService.collect(id);
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult<Object> cancelCollect(@PathVariable("id") Integer id) {
        return wmMaterialService.cancelCollect(id);
    }
}