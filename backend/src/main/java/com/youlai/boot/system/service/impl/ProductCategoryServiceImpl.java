package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.ProductCategoryMapper;
import com.youlai.boot.system.service.ProductCategoryService;
import com.youlai.boot.system.model.entity.ProductCategory;
import com.youlai.boot.system.model.form.ProductCategoryForm;
import com.youlai.boot.system.model.query.ProductCategoryQuery;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.youlai.boot.system.converter.ProductCategoryConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 分类服务实现类
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ProductCategoryConverter productCategoryConverter;

    /**
     * 获取分类分页列表
     *
     * @param queryParams 查询参数
     * @return {@link IPage<ProductCategoryVO>} 分类分页列表
     */
    @Override
    public IPage<ProductCategoryVO> getProductCategoryPage(ProductCategoryQuery queryParams) {
        Page<ProductCategoryVO> pageVO = this.baseMapper.getProductCategoryPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }

    /**
     * 获取分类表单数据
     *
     * @param id 分类ID
     * @return 分类表单数据
     */
    @Override
    public ProductCategoryForm getProductCategoryFormData(Long id) {
        ProductCategory entity = this.getById(id);
        return productCategoryConverter.toForm(entity);
    }

    /**
     * 新增分类
     *
     * @param formData 分类表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveProductCategory(ProductCategoryForm formData) {
        ProductCategory entity = productCategoryConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 更新分类
     *
     * @param id   分类ID
     * @param formData 分类表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateProductCategory(Long id,ProductCategoryForm formData) {
        ProductCategory entity = productCategoryConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 删除分类
     *
     * @param ids 分类ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteProductCategorys(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的分类数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

    /**
     * 获取分类树形结构
     *
     * @return 分类树形结构列表
     */
    @Override
    public List<ProductCategoryVO> getCategoryTree() {
        List<ProductCategoryVO> allCategories = this.baseMapper.getAllCategories();
        return buildCategoryTree(allCategories, null);
    }

    /**
     * 构建分类树形结构
     *
     * @param allCategories 所有分类列表
     * @param parentId 父级ID
     * @return 树形结构列表
     */
    private List<ProductCategoryVO> buildCategoryTree(List<ProductCategoryVO> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> {
                    if (parentId == null) {
                        return category.getParentId() == null || category.getParentId() == 0;
                    }
                    return parentId.equals(category.getParentId());
                })
                .map(category -> {
                    category.setChildren(buildCategoryTree(allCategories, category.getId()));
                    return category;
                })
                .collect(Collectors.toList());
    }

}
