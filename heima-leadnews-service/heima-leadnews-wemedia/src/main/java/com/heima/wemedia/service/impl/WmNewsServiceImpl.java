package com.heima.wemedia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.feign.ApArticleFeign;
import com.heima.common.aliyun.AliyunGreenContentScan;
import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.AppHttpCodeEnum;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.exception.ResponseEnum;
import com.heima.common.minio.MinioTemplate;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.*;
import com.heima.utils.common.BeanHelper;
import com.heima.utils.common.JsonUtils;
import com.heima.utils.common.ThreadLocalUtils;
import com.heima.wemedia.mapper.*;
import com.heima.wemedia.service.WmNewsMaterialService;
import com.heima.wemedia.service.WmNewsService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    private WmNewsMaterialService wmNewsMaterialService;
    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private AliyunGreenContentScan aliyunGreenContentScan;
    @Autowired
    private ApArticleFeign apArticleFeign;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public ResponseResult findAll(WmNewsPageReqDto dto) {
        try {
            ResponseEnum.OBJECT_NONNUL.assertNotNull(dto);

            //检查page和size, 若没有传, 则设置默认值
            dto.checkParam();

            QueryWrapper<WmNews> qw = new QueryWrapper<>();
            //当前登录用户
            Integer userId = ThreadLocalUtils.get(Integer.class);
            ResponseEnum.OBJECT_NONNUL.assertNotNull(userId);
            qw.eq("user_id", userId);

            //状态
            if (dto.getStatus() != null) {
                qw.eq("status", dto.getStatus());
            }

            //关键字
            if (StrUtil.isNotEmpty(dto.getKeyword())) {
                qw.like("title", dto.getKeyword());
            }

            //频道
            if (dto.getChannelId() != null) {
                qw.eq("channel_id", dto.getChannelId());
            }

            //发布日期
            if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
                qw.between("publish_time", dto.getBeginPubDate(), dto.getEndPubDate());
            }

            //降序
            qw.orderByDesc("created_time");

            //分页
            IPage<WmNews> page = new Page<>(dto.getPage(), dto.getSize());
            page = this.page(page, qw);
            return new PageResponseResult<>(page);
        } catch (Exception e) {
            ThreadLocalUtils.remove();
        }

        throw new LeadNewsException(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult submit(WmNewsDto dto) {
        try {
            this.assertNotNull(dto);

            //转换成WmNews对象, 因为最后保存的是WmNews对象
            WmNews news = BeanHelper.copyProperties(dto, WmNews.class);
            if (news == null) {
                throw new LeadNewsException(AppHttpCodeEnum.NEED_LOGIN);
            }

            //设置当前用户为自媒体用户ID
            news.setUserId(this.getUserId());

            //转换为String, 因为自媒体文章保存的是文章封面图片, 是String类型
            List<String> images = dto.getImages();
            if (!Collections.isEmpty(images)) {
                //流式操作
                //news.setImages(String.join(",", images));
                String imageStr = StrUtil.join(",", images);
                news.setImages(imageStr);
            }

            //获取内容
            String content = news.getContent();
            List<String> contentImages = this.getContentImages(content);
            if (contentImages == null) {
                contentImages = new ArrayList<>();
            }

            //判断是否为【自动】选取封面
            if (news.getType() == -1) {
                //判断内容是否包含图片
                //等于0则是无图
                if (contentImages.size() == 0) {
                    news.setType((short) 0);
                    news.setImages(null);
                }
                //小于3张则是单图, 取第一张图片即可
                else if (contentImages.size() <= 2) {
                    news.setType((short) 1);
                    news.setImages(contentImages.get(0));
                }
                //大于2张则是多图, 取前3张图片即可
                else {
                    news.setType((short) 2);
                    //截取前3张图片
                    news.setImages(String.join(",", contentImages.subList(0, 3)));
                }
            }

            //2）发布文章既有新增文章，也有修改文章（判断是否存在文章ID）
            if (news.getId() == null) {
                //2.1如果没有, 则是新增文章，添加wm_news表数据
                this.save(news);

            } else {
                //2.2如果为修改文章，修改wm_news表数据，删除该文章的所有素材关联数据
                //更新
                this.updateById(news);
                //删除该文章与素材的联系
                QueryWrapper<WmNewsMaterial> qw = new QueryWrapper<>();
                qw.eq("news_id", news.getId());
                wmNewsMaterialMapper.delete(qw);
            }

            //3）提交审核时，才关联建立文章和素材的关系（新增wm_news_material表数据）
            if (news.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
                //建立联系需要知道type引用类型, 到底是内容引用还是标题引用, 且无论是文章素材还是内容素材都需要建立联系

                //1表示主图引用, 建立文章和封面素材的关系
                if (images != null) {
                    final List<Integer> materialIds = this.getMaterialIdFromUrl(images);
                    this.saveBatch(news.getId(), materialIds, true);
                }

                //0表示内容引用, 建立文章和内容素材的关系
                //首先将图片URL转换为ID
                //批量插入（1条SQL搞定）, 因为MybatisPlus没有, 要自己手动编写mapper
                //wmNewsMaterialMapper.saveNewsMaterial(news.getId(), materialIds, 1);
                final List<Integer> materialIds = this.getMaterialIdFromUrl(contentImages);
                this.saveBatch(news.getId(), materialIds, false);

                //审核文章
                this.checkWmNews(news);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new LeadNewsException(500, e.getMessage());
        } finally {
            this.removeUserId();
        }

        return ResponseResult.okResult(null);
    }

    private void saveBatch(Integer newsId, List<Integer> materialIds, boolean type) {
        if (materialIds != null) {
            List<WmNewsMaterial> newsMaterialList = this.generateNewsMaterialList(newsId, materialIds, type);
            wmNewsMaterialService.saveBatch(newsMaterialList);
        }
    }

    private Integer getUserId() {
        Integer userId = ThreadLocalUtils.get(Integer.class);
        if (userId == null) {
            throw new LeadNewsException(AppHttpCodeEnum.NEED_LOGIN);
        }
        return userId;
    }

    @Override
    public ResponseResult delNews(Integer newsId) {
        if (newsId == null) {
            throw new LeadNewsException(AppHttpCodeEnum.PARAM_INVALID);
        }
        //1.判断是否为审核表
        WmNews news = this.getById(newsId);
        this.assertNotNull(news);
        if (news.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            List<Integer> materialIds = new ArrayList<>();

            WmNews wmNews = this.getById(newsId);
            String images = wmNews.getImages();
            if (images != null && images.contains(",")) {
                List<String> urlList = StrUtil.split(images, ",");
                if (!Collections.isEmpty(urlList)) {
                    for (String url : urlList) {
                        QueryWrapper<WmMaterial> qw = new QueryWrapper<>();
                        qw.eq("url", url);
                        WmMaterial wmMaterial = wmMaterialMapper.selectOne(qw);
                        materialIds.add(wmMaterial.getId());
                    }
                }
            }

            for (Integer materialId : materialIds) {
                QueryWrapper<WmNewsMaterial> qw = new QueryWrapper<>();
                qw.eq("news_id", newsId);
                qw.eq("material_id", materialId);
                wmNewsMaterialMapper.delete(qw);
            }

        }

        this.removeById(newsId);

        return ResponseResult.okResult(null);
    }

    @Override
    public ResponseResult one(Integer newsId) {
        return ResponseResult.okResult(this.getById(newsId));
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new LeadNewsException(AppHttpCodeEnum.NEED_LOGIN);
        }
    }

    private void removeUserId() {
        ThreadLocalUtils.remove();
    }

    private List<WmNewsMaterial> generateNewsMaterialList(Integer newsId, List<Integer> materialIds, boolean type) {
        if (materialIds == null || Collections.isEmpty(materialIds)) {
            return null;
        }

        List<WmNewsMaterial> list = new ArrayList<>();
        for (Integer materialId : materialIds) {
            WmNewsMaterial wmNewsMaterial = new WmNewsMaterial();
            wmNewsMaterial.setNewsId(newsId);
            wmNewsMaterial.setMaterialId(materialId);
            wmNewsMaterial.setType(type);
            list.add(wmNewsMaterial);
        }
        return list;
    }

    private List<Integer> getMaterialIdFromUrl(List<String> urlList) {
        if (urlList == null || Collections.isEmpty(urlList)) {
            return null;
        }

        List<Integer> materialIds = new ArrayList<>();
        QueryWrapper<WmMaterial> qw = new QueryWrapper<>();
        if (urlList.size() == 1) {
            qw.eq("url", urlList.get(0));
            WmMaterial wmMaterial = wmMaterialMapper.selectOne(qw);
            materialIds.add(wmMaterial.getId());
        } else {
            qw.in("url", urlList);
            List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(qw);
            materialIds = wmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
        }

        return materialIds;
    }

    private List<String> getContentImages(String content) {
        if (StrUtil.isEmpty(content)) {
            throw new LeadNewsException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //因为content是一个Json
        List<Map> mapList = JsonUtils.toList(content, Map.class);
        if (Collections.isEmpty(mapList)) {
            return null;
        }
        List<Object> collect = mapList.stream().map(map -> {
            if ("image".equals(map.get("type"))) {
                return map.get("value");
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());


        //通过糊涂工具包将List<Object>转为List<String>
        return Convert.convert(new TypeReference<List<String>>() {
        }, collect);
    }

    //审查自媒体文章是否过审
//    @Async
    public void checkWmNews(WmNews wmNews) {
        //根据id查询自媒体文章
        this.assertNotNull(wmNews);

        //从文章中提取出文本和图片
        List<String> textList = this.getTextFromNews(wmNews);
        List<String> imageList = null;
        if (wmNews.getImages() != null) {
            imageList = StrUtil.split(wmNews.getImages(), ",");
        }
//        List<byte[]> imageList = this.getImageFromNews(wmNews);

        //自定义内容检测

        //提交文本给阿里云内容检测
        //根据阿里云的反馈结果进行处理
        if (!Collections.isEmpty(textList)) {
            try {
                Map<String, String> result = aliyunGreenContentScan.greeTextScan(textList);

                //如果不过审，直接返回false，不往下执行
                if (!resultScan(result, wmNews)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        CompletableFuture<Boolean> task1 = CompletableFuture.supplyAsync(() -> {
//
//            return true;
//        }, taskExecutor);

        //提交图片给阿里云内容检测
        //根据阿里云的反馈结果进行处理
        final List<String> imageUrlList = imageList;
        if (!Collections.isEmpty(imageUrlList)) {
            try {
                Map<String, String> result = aliyunGreenContentScan.imageUrlListScan(imageUrlList);
                if (!this.resultScan(result, wmNews)) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        CompletableFuture<Boolean> task2 = CompletableFuture.supplyAsync(() -> {
//
//            return true;
//        }, taskExecutor);

        //判断发布时间是否大于当前时间，状态改为8（待发布）
        Date publishTime = wmNews.getPublishTime();
        if (publishTime != null && publishTime.after(new Date())) {
            wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
            wmNews.setReason("审核成功，待发布");
            wmNewsMapper.updateById(wmNews);
            //注意要退出
            return;
        }

        //异步保存App端文章，同时更新自媒体状态为9
        CompletableFuture.runAsync(() -> {
            this.saveApArticle(wmNews);
        }, taskExecutor);
    }

    /**
     * 保存App文章
     *
     * @param wmNews
     */
//    @Async
    public void saveApArticle(WmNews wmNews) {
        if (wmNews == null) {
            throw new LeadNewsException(AppHttpCodeEnum.PARAM_INVALID);
        }

        //因为要远程调用Article服务，需要ArticleDto
        ArticleDto dto = BeanHelper.copyProperties(wmNews, ArticleDto.class);
        if (dto == null) {
            dto = new ArticleDto();
        }

        //设置作者id和作者用户名
        QueryWrapper<WmUser> qw = new QueryWrapper<>();
        qw.eq("id", wmNews.getUserId());
        WmUser wmUser = wmUserMapper.selectOne(qw);
        if (wmUser != null) {
            if (wmUser.getApAuthorId() != null) {
                dto.setAuthorId(wmUser.getApAuthorId().longValue());
            }
            if (wmUser.getName() != null) {
                dto.setAuthorName(wmUser.getName());
            }
        }

        //设置频道
        if (wmNews.getChannelId() != null) {
            WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
            dto.setChannelId(wmChannel.getId());
            if (wmChannel.getName() != null) {
                dto.setChannelName(wmChannel.getName());
            }
        }

        //设置文章布局，是否无图，单图，3图
        if (wmNews.getType() != null) {
            dto.setLayout(wmNews.getType());
        }

        //设置文章类型、点赞数、收藏数、评论数、阅读数
        dto.setFlag((byte) 0);
        dto.setLikes(0);
        dto.setCollection(0);
        dto.setComment(0);
        dto.setViews(0);

        //Feign远程调用【保存文章】服务
        ResponseResult<Long> responseResult = apArticleFeign.saveArticle(dto);
        if (responseResult.getCode() == 200) {
            Long articleId = responseResult.getData();
            wmNews.setArticleId(articleId);
            wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
            wmNews.setReason("已发布");

            //更新该自媒体文章
            this.updateById(wmNews);
        }
    }

    public boolean resultScan(Map<String, String> result, WmNews wmNews) {
        if (result != null && wmNews != null) {
            String suggestion = result.get("suggestion");
            //成功
            if (StrUtil.equals("pass", suggestion)) {
                return true;
            }

            //机器审有点疑惑，交给人工审
            else if (StrUtil.equals("review", suggestion)) {
                //修改文章状态为3, 转人工审
                wmNews.setStatus(WmNews.Status.ADMIN_AUTH.getCode());
                wmNews.setReason("待人工审核");
            }

            //驳回
            else if (StrUtil.equals("block", suggestion)) {
                //修改文章状态为2，审核失败
                wmNews.setStatus(WmNews.Status.FAIL.getCode());
                wmNews.setReason("内容含敏感内容，请修改");
            }

            //更新文章
            this.updateById(wmNews);
        }
        return false;
    }

    //    private List<String> getImageFromNews(String wmNews) {
//
//    }
    private List<byte[]> getImageFromNews(WmNews wmNews) {
        if (wmNews == null) {
            return null;
        }

        //从内容提取图片
        String content = wmNews.getContent();
//        ArrayList<byte[]> imageByteArrayList = (ArrayList<byte[]>) this.extractContent(content, "image");
        List<byte[]> imageByteArrayList = new ArrayList<>();
        if (content != null) {
            List<Map> contentMapList = JsonUtils.toList(content, Map.class);
            if (contentMapList != null) {
                for (Map map : contentMapList) {
                    if ("image".equals(map.get("type"))) {
                        String imageUrl = (String) map.get("value");
                        //从minio下载图片内容
                        byte[] imageByteArray = minioTemplate.downLoadFile(imageUrl);
                        if (imageByteArray != null) {
                            imageByteArrayList.add(imageByteArray);
                        }
                    }
                }
            }
        }

        //从封面提取图片
        String images = wmNews.getImages();
        if (StrUtil.isNotEmpty(images)) {
            String[] imageUrlList = images.split(",");
            for (String imageUrl : imageUrlList) {
                if (imageUrl != null) {
                    byte[] imageByteArray = minioTemplate.downLoadFile(imageUrl);
                    imageByteArrayList.add(imageByteArray);
                }
            }
        }

        return imageByteArrayList;
    }

    private List<String> getTextFromNews(WmNews wmNews) {
        if (wmNews == null) {
            return null;
        }
        List<String> textList = new ArrayList<>();
        //从内容中提取文字
        if (StringUtils.isNotEmpty(wmNews.getContent())) {
            //转换为对象
            List<Map> list = JsonUtils.toList(wmNews.getContent(), Map.class);
            for (Map map : list) {
                if (map.get("type").equals("text")) {
                    textList.add((String) map.get("value"));
                }
            }
        }
        //存入标题
        textList.add(wmNews.getTitle());
        //存入标签
        textList.add(wmNews.getLabels());
        return textList;

//        return (List<String>) this.extractContent(content, "text");
    }

    private List extractContent(String content, String type) {
        List<byte[]> imageByteArrayList = new ArrayList<>();
        List<String> textList = new ArrayList<>();
        if (content != null) {
            List<Map> contentMapList = JsonUtils.toList(content, Map.class);
            if (contentMapList != null) {
                for (Map map : contentMapList) {
                    if ("image".equals(type)) {
                        String imageUrl = (String) map.get("value");
                        //从minio下载图片内容
                        if (imageUrl != null) {
                            byte[] imageByteArray = minioTemplate.downLoadFile(imageUrl);
                            if (imageByteArray != null) {
                                imageByteArrayList.add(imageByteArray);
                            }
                        }
                    } else if ("text".equals(type)) {
                        textList.add((String) map.get("value"));
                    }
                }
            }
        } else {
            return null;
        }

        if ("image".equals(type)) {
            return imageByteArrayList;
        } else if ("text".equals(type)) {
            return textList;
        }

        return null;
    }
}