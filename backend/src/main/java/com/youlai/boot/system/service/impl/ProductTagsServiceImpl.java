package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.ProductTagsMapper;
import com.youlai.boot.system.service.ProductTagsService;
import com.youlai.boot.system.model.entity.ProductTags;
import com.youlai.boot.system.model.form.ProductTagsForm;
import com.youlai.boot.system.model.query.ProductTagsQuery;
import com.youlai.boot.system.model.vo.ProductTagsVO;
import com.youlai.boot.system.converter.ProductTagsConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 标签服务实现类
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
@Service
@RequiredArgsConstructor
public class ProductTagsServiceImpl extends ServiceImpl<ProductTagsMapper, ProductTags> implements ProductTagsService {

    private final ProductTagsConverter productTagsConverter;

    /**
     * 获取标签分页列表
     *
     * @param queryParams 查询参数
     * @return {@link IPage<ProductTagsVO>} 标签分页列表
     */
    @Override
    public IPage<ProductTagsVO> getProductTagsPage(ProductTagsQuery queryParams) {
        Page<ProductTagsVO> pageVO = this.baseMapper.getProductTagsPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }

    /**
     * 获取标签表单数据
     *
     * @param id 标签ID
     * @return 标签表单数据
     */
    @Override
    public ProductTagsForm getProductTagsFormData(Long id) {
        ProductTags entity = this.getById(id);
        return productTagsConverter.toForm(entity);
    }

    /**
     * 新增标签
     *
     * @param formData 标签表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveProductTags(ProductTagsForm formData) {
        ProductTags entity = productTagsConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 更新标签
     *
     * @param id   标签ID
     * @param formData 标签表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateProductTags(Long id,ProductTagsForm formData) {
        ProductTags entity = productTagsConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 删除标签
     *
     * @param ids 标签ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteProductTagss(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的标签数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
