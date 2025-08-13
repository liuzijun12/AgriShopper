package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.ProductCartMapper;
import com.youlai.boot.system.service.ProductCartService;
import com.youlai.boot.system.model.entity.ProductCart;
import com.youlai.boot.system.model.form.ProductCartForm;
import com.youlai.boot.system.model.query.ProductCartQuery;
import com.youlai.boot.system.model.vo.ProductCartVO;
import com.youlai.boot.system.converter.ProductCartConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 购物车服务实现类
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@Service
@RequiredArgsConstructor
public class ProductCartServiceImpl extends ServiceImpl<ProductCartMapper, ProductCart> implements ProductCartService {

    private final ProductCartConverter productCartConverter;

    /**
    * 获取购物车分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<ProductCartVO>} 购物车分页列表
    */
    @Override
    public IPage<ProductCartVO> getProductCartPage(ProductCartQuery queryParams) {
        Page<ProductCartVO> pageVO = this.baseMapper.getProductCartPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取购物车表单数据
     *
     * @param id 购物车ID
     * @return 购物车表单数据
     */
    @Override
    public ProductCartForm getProductCartFormData(Long id) {
        ProductCart entity = this.getById(id);
        return productCartConverter.toForm(entity);
    }
    
    /**
     * 新增购物车
     *
     * @param formData 购物车表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveProductCart(ProductCartForm formData) {
        ProductCart entity = productCartConverter.toEntity(formData);
        return this.save(entity);
    }
    
    /**
     * 更新购物车
     *
     * @param id   购物车ID
     * @param formData 购物车表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateProductCart(Long id,ProductCartForm formData) {
        ProductCart entity = productCartConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除购物车
     *
     * @param ids 购物车ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteProductCarts(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的购物车数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
