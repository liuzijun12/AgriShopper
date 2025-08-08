package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.ProductTags;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.ProductTagsQuery;
import com.youlai.boot.system.model.vo.ProductTagsVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Mapper
public interface ProductTagsMapper extends BaseMapper<ProductTags> {

    /**
     * 获取标签分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<ProductTagsVO>} 标签分页列表
     */
    Page<ProductTagsVO> getProductTagsPage(Page<ProductTagsVO> page, ProductTagsQuery queryParams);

}
