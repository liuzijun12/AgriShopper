package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.ProductCart;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.ProductCartQuery;
import com.youlai.boot.system.model.vo.ProductCartVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Mapper
public interface ProductCartMapper extends BaseMapper<ProductCart> {

    /**
     * 获取购物车分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<ProductCartVO>} 购物车分页列表
     */
    Page<ProductCartVO> getProductCartPage(Page<ProductCartVO> page, ProductCartQuery queryParams);

}
