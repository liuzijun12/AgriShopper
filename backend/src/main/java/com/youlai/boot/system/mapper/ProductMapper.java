package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品表Mapper接口
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 获取商品表分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<ProductVO>} 商品表分页列表
     */
    Page<ProductVO> getProductPage(Page<ProductVO> page, ProductQuery queryParams);

}
