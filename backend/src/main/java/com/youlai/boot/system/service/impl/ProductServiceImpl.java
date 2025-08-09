package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.ProductMapper;
import com.youlai.boot.system.service.ProductService;
import com.youlai.boot.system.service.IdCategoryService;
import com.youlai.boot.system.service.IdTagsService;
import com.youlai.boot.system.model.entity.Product;
import com.youlai.boot.system.model.entity.IdCategory;
import com.youlai.boot.system.model.entity.IdTags;
import com.youlai.boot.system.model.form.ProductForm;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import com.youlai.boot.system.model.vo.TagVO;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.youlai.boot.system.converter.ProductConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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
    private final IdCategoryService idCategoryService;
    private final IdTagsService idTagsService;

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
        ProductForm form = productConverter.toForm(entity);
        
        // 获取关联的分类ID列表
        List<IdCategory> categoryList = idCategoryService.list(new LambdaQueryWrapper<IdCategory>()
                .eq(IdCategory::getProductId, id.intValue()));
        List<Long> categoryIds = categoryList.stream()
                .map(item -> item.getCategoryId().longValue())
                .collect(Collectors.toList());
        form.setCategoryIds(categoryIds);
        
        // 获取关联的标签ID列表
        List<IdTags> tagsList = idTagsService.list(new LambdaQueryWrapper<IdTags>()
                .eq(IdTags::getProductId, id.intValue()));
        List<Long> tagIds = tagsList.stream()
                .map(item -> item.getTagsId().longValue())
                .collect(Collectors.toList());
        form.setTagIds(tagIds);
        
        return form;
    }

    /**
     * 新增商品表
     *
     * @param formData 商品表表单对象
     * @return 是否新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProduct(ProductForm formData) {
        Product entity = productConverter.toEntity(formData);
        boolean saved = this.save(entity);
        
        if (saved && entity.getId() != null) {
            // 保存分类关联
            if (formData.getCategoryIds() != null && !formData.getCategoryIds().isEmpty()) {
                List<Integer> categoryIds = formData.getCategoryIds().stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                idCategoryService.saveProductCategories(entity.getId().intValue(), categoryIds);
            }
            
            // 保存标签关联
            if (formData.getTagIds() != null && !formData.getTagIds().isEmpty()) {
                List<Integer> tagIds = formData.getTagIds().stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                idTagsService.saveProductTags(entity.getId().intValue(), tagIds);
            }
        }
        
        return saved;
    }

    /**
     * 更新商品表
     *
     * @param id   商品表ID
     * @param formData 商品表表单对象
     * @return 是否修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProduct(Long id,ProductForm formData) {
        Product entity = productConverter.toEntity(formData);
        entity.setId(id);
        boolean updated = this.updateById(entity);
        
        if (updated) {
            // 删除原有的标签关联
            idTagsService.deleteByProductId(id.intValue());
            
            // 保存新的分类关联
            if (formData.getCategoryIds() != null && !formData.getCategoryIds().isEmpty()) {
                List<Integer> categoryIds = formData.getCategoryIds().stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                idCategoryService.saveProductCategories(id.intValue(), categoryIds);
            }
            
            // 保存新的标签关联
            if (formData.getTagIds() != null && !formData.getTagIds().isEmpty()) {
                List<Integer> tagIds = formData.getTagIds().stream()
                        .map(Long::intValue)
                        .collect(Collectors.toList());
                idTagsService.saveProductTags(id.intValue(), tagIds);
            } else {
                // 如果没有标签，删除所有关联
                idTagsService.deleteByProductId(id.intValue());
            }
        }
        
        return updated;
    }

    /**
     * 删除商品表
     *
     * @param ids 商品表ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProducts(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的商品表数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        
        // 删除关联的分类和标签
        for (Long productId : idList) {
            // 删除分类关联
            idCategoryService.deleteByProductId(productId.intValue());
            
            // 删除标签关联
            idTagsService.deleteByProductId(productId.intValue());
        }
        
        return this.removeByIds(idList);
    }

    /**
     * 根据ID获取商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    @Override
    public ProductVO getProductById(Long id) {
        Product entity = this.getById(id);
        return productConverter.toVO(entity);
    }

    /**
     * 获取商品详情（包含标签）
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @Override
    public ProductVO getProductWithTags(Long id) {
        return this.baseMapper.getProductWithTags(id);
    }

    /**
     * 通过商品ID获取标签列表
     *
     * @param productId 商品ID
     * @return 标签列表
     */
    @Override
    public List<TagVO> getTagsByProductId(Long productId) {
        return this.baseMapper.getTagsByProductId(productId);
    }

    /**
     * 获取商品详情（包含分类）
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @Override
    public ProductVO getProductWithCategories(Long id) {
        return this.baseMapper.getProductWithCategories(id);
    }

    /**
     * 通过商品ID获取分类列表
     *
     * @param productId 商品ID
     * @return 分类列表
     */
    @Override
    public List<ProductCategoryVO> getCategoriesByProductId(Long productId) {
        return this.baseMapper.getCategoriesByProductId(productId);
    }

}
