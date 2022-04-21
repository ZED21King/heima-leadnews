package com.heima.common.dtos;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResponseResult<T> extends ResponseResult<List<T>> implements Serializable {
    private Integer currentPage;
    private Integer size;
    private Integer total;

    public PageResponseResult(Integer currentPage, Integer size, Integer total) {
        this.currentPage = currentPage;
        this.size = size;
        this.total = total;
    }

    public PageResponseResult(IPage<T> page) {
        this((int)page.getCurrent(),
                (int)page.getSize(),
                (int)page.getTotal());
        this.setData(page.getRecords());
    }
}