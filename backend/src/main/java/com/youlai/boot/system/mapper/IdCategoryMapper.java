package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.IdCategory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 分类关联Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-08 12:16
 */
@Mapper
public interface IdCategoryMapper extends BaseMapper<IdCategory> {

    /**
     * 获取分类关联分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<IdCategoryVO>} 分类关联分页列表
     */
    Page<IdCategoryVO> getIdCategoryPage(Page<IdCategoryVO> page, IdCategoryQuery queryParams);

    /**
     * 批量插入商品分类关联
     *
     * @param productId 商品ID
     * @param categoryIds 分类ID列表
     * @return 插入的记录数
     */
    int batchInsert(@Param("productId") Integer productId, @Param("categoryIds") List<Integer> categoryIds);

    /**
     * 根据商品ID删除关联
     *
     * @param productId 商品ID
     * @return 删除的记录数
     */
    int deleteByProductId(@Param("productId") Integer productId);

    /**
     * 根据分类ID删除关联
     *
     * @param categoryId 分类ID
     * @return 删除的记录数
     */
    int deleteByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 根据商品ID获取分类ID列表
     *
     * @param productId 商品ID
     * @return 分类ID列表
     */
    List<Integer> getCategoryIdsByProductId(@Param("productId") Integer productId);

    /**
     * 根据分类ID获取商品ID列表
     *
     * @param categoryId 分类ID
     * @return 商品ID列表
     */
    List<Integer> getProductIdsByCategoryId(@Param("categoryId") Integer categoryId);

}
