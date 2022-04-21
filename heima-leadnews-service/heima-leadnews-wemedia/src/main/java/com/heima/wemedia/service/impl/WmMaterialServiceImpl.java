package com.heima.wemedia.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.AppHttpCodeEnum;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.exception.ResponseEnum;
import com.heima.common.minio.MinioTemplate;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.ThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import com.heima.wemedia.service.WmNewsMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private WmNewsMaterialService wmNewsMaterialService;

//    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Override
    public ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new LeadNewsException(AppHttpCodeEnum.PARAM_INVALID);
        }

        Integer userId = (Integer) ThreadLocalUtils.get();
//        Integer userId = WmMaterialServiceImpl.threadLocal.get();

        ResponseEnum.OBJECT_NONNUL.assertNotNull(userId);

        //把文件上传到Minio
        //因为文件名或许可能重复，所以用UUID来作为文件名
//        String uuid = UUIDUtils.generateUuid();
        String uuid = IdUtil.simpleUUID();

        //获取后缀名
        String getOriginalFilename = multipartFile.getOriginalFilename();
        ResponseEnum.OBJECT_NONNUL.assertNotNull(getOriginalFilename);
        String extName = getOriginalFilename.substring(getOriginalFilename.lastIndexOf("."));
        String fileName = uuid + extName;

        try {
            String url = minioTemplate.uploadImgFile("wemedia",fileName, multipartFile.getInputStream());

            WmMaterial wmMaterial = new WmMaterial();
            wmMaterial.setUserId(userId);
            wmMaterial.setUrl(url);
//            wmMaterial.setType(false);
//            wmMaterial.setCollection(false);
            //保存素材
            this.save(wmMaterial);

            return ResponseResult.okResult(wmMaterial);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            WmMaterialServiceImpl.threadLocal.remove();
            ThreadLocalUtils.remove();
        }

        throw new LeadNewsException(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult<List<WmMaterial>> findList(WmMaterialDto dto) {
        try {
            //断言不能为null
            ResponseEnum.OBJECT_NONNUL.assertNotNull(dto);

            //校验参数合法性
            dto.checkParam();

            Integer userId = (Integer) ThreadLocalUtils.get();
            ResponseEnum.OBJECT_NONNUL.assertNotNull(userId);

            QueryWrapper<WmMaterial> qw = new QueryWrapper<>();
            qw.eq("user_id", userId); //当前用户的素材

            if (dto.getIsCollection() == 1) {
                qw.eq("is_collection", dto.getIsCollection()); //是否收藏
            }
            qw.orderByDesc("created_time");

            IPage<WmMaterial> page = new Page<>(dto.getPage(), dto.getSize());
            return new PageResponseResult<>(this.page(page, qw));
        } catch (Exception e) {
            ThreadLocalUtils.remove();
        }

        throw new LeadNewsException(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult<Object> deletePicture(Integer id) {
        ResponseEnum.PARAM_INVALID.assertNotNull(id);

        //1.先查看wm_news_material表, 该素材有没有被文章所使用
        QueryWrapper<WmNewsMaterial> qw1 = new QueryWrapper<>();
        qw1.eq("material_id", id);
        WmNewsMaterial wmNewsMaterial = wmNewsMaterialService.getOne(qw1);

        //2.若无, 则删除该素材
        if (wmNewsMaterial == null) {
            QueryWrapper<WmMaterial> qw2 = new QueryWrapper<>();
            qw2.eq("id", id);
            //先查询素材表有无该素材
            WmMaterial wmMaterial = this.getOne(qw2);
            if (wmMaterial != null) {
                minioTemplate.delete(wmMaterial.getUrl());
                this.remove(qw2);
            }
        }

        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<Object> collect(Integer id) {
        ResponseEnum.PARAM_INVALID.assertNotNull(id);

        WmMaterial wmMaterial = this.getById(id);
        wmMaterial.setIsCollection((short) 1);
        this.updateById(wmMaterial);

        return new ResponseResult<>();
    }

    @Override
    public ResponseResult<Object> cancelCollect(Integer id) {
        ResponseEnum.PARAM_INVALID.assertNotNull(id);

        WmMaterial wmMaterial = this.getById(id);
        wmMaterial.setIsCollection((short) 0);
        this.updateById(wmMaterial);

        return new ResponseResult<>();
    }
}