package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.ProductMapper;
import com.youlai.boot.system.service.ProductService;
import com.youlai.boot.system.model.entity.Product;
import com.youlai.boot.system.model.form.ProductForm;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import com.youlai.boot.system.converter.ProductConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 商品表服务实现类
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductConverter productConverter;

    /**
     * 获取商品表分页列表
     *
     * @param queryParams 查询参数
     * @return {@link IPage<ProductVO>} 商品表分页列表
     */
    @Override
    public IPage<ProductVO> getProductPage(ProductQuery queryParams) {
        Page<ProductVO> pageVO = this.baseMapper.getProductPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }

    /**
     * 获取商品表表单数据
     *
     * @param id 商品表ID
     * @return 商品表表单数据
     */
    @Override
    public ProductForm getProductFormData(Long id) {
        Product entity = this.getById(id);
        return productConverter.toForm(entity);
    }

    /**
     * 新增商品表
     *
     * @param formData 商品表表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveProduct(ProductForm formData) {
        Product entity = productConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 更新商品表
     *
     * @param id   商品表ID
     * @param formData 商品表表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateProduct(Long id,ProductForm formData) {
        Product entity = productConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 删除商品表
     *
     * @param ids 商品表ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteProducts(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的商品表数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
