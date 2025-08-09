package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import com.youlai.boot.system.model.vo.TagVO;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

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

    /**
     * 通过商品ID获取标签列表
     *
     * @param productId 商品ID
     * @return {@link List<TagVO>} 标签列表
     */
    List<TagVO> getTagsByProductId(@Param("productId") Long productId);

    /**
     * 获取商品详情（包含标签）
     *
     * @param id 商品ID
     * @return {@link ProductVO} 商品详情
     */
    ProductVO getProductWithTags(@Param("id") Long id);

    /**
     * 通过商品ID获取分类列表
     *
     * @param productId 商品ID
     * @return {@link List<ProductCategoryVO>} 分类列表
     */
    List<ProductCategoryVO> getCategoriesByProductId(@Param("productId") Long productId);

    /**
     * 获取商品详情（包含分类）
     *
     * @param id 商品ID
     * @return {@link ProductVO} 商品详情
     */
    ProductVO getProductWithCategories(@Param("id") Long id);

}
