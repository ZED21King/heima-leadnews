package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.heima.model.pojo.BasePojo;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 自媒体图文素材信息表
 * </p>
 *
 * @author itheima
 */
@Data
@TableName("wm_news_material")
public class WmNewsMaterial extends BasePojo implements Serializable {

    private static final long serialVersionUID = 1L;

//    /**
//     * 主键
//     */
//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;

    /**
     * 素材ID
     */
    @TableField("material_id")
    private Integer materialId;

    /**
     * 图片ID
     */
    @TableField("news_id")
    private Integer newsId;

    /**
     * 引用类型
            0 内容引用
            1 主图引用
     */
    @TableField("type")
    private boolean type;

    /**
     * 引用排序
     */
    @TableField("ord")
    private boolean ord;
}