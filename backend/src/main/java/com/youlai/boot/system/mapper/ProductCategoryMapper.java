package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.ProductCategory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.ProductCategoryQuery;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * 获取分类分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<ProductCategoryVO>} 分类分页列表
     */
    Page<ProductCategoryVO> getProductCategoryPage(Page<ProductCategoryVO> page, ProductCategoryQuery queryParams);

}
