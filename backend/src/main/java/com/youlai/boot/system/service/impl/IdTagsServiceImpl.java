package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.IdTagsMapper;
import com.youlai.boot.system.service.IdTagsService;
import com.youlai.boot.system.model.entity.IdTags;
import com.youlai.boot.system.model.form.IdTagsForm;
import com.youlai.boot.system.model.query.IdTagsQuery;
import com.youlai.boot.system.model.vo.IdTagsVO;
import com.youlai.boot.system.converter.IdTagsConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 标签关联表服务实现类
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Service
@RequiredArgsConstructor
public class IdTagsServiceImpl extends ServiceImpl<IdTagsMapper, IdTags> implements IdTagsService {

    private final IdTagsConverter idTagsConverter;

    /**
     * 获取标签关联表分页列表
     *
     * @param queryParams 查询参数
     * @return {@link IPage<IdTagsVO>} 标签关联表分页列表
     */
    @Override
    public IPage<IdTagsVO> getIdTagsPage(IdTagsQuery queryParams) {
        Page<IdTagsVO> pageVO = this.baseMapper.getIdTagsPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }

    /**
     * 获取标签关联表表单数据
     *
     * @param id 标签关联表ID
     * @return 标签关联表表单数据
     */
    @Override
    public IdTagsForm getIdTagsFormData(Long id) {
        IdTags entity = this.getById(id);
        return idTagsConverter.toForm(entity);
    }

    /**
     * 新增标签关联表
     *
     * @param formData 标签关联表表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveIdTags(IdTagsForm formData) {
        IdTags entity = idTagsConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 更新标签关联表
     *
     * @param id   标签关联表ID
     * @param formData 标签关联表表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateIdTags(Long id,IdTagsForm formData) {
        IdTags entity = idTagsConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 删除标签关联表
     *
     * @param ids 标签关联表ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteIdTagss(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的标签关联表数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

    /**
     * 根据商品ID删除所有标签关联
     *
     * @param productId 商品ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteByProductId(Integer productId) {
        return this.remove(new LambdaQueryWrapper<IdTags>()
                .eq(IdTags::getProductId, productId));
    }

    /**
     * 根据标签ID删除所有商品关联
     *
     * @param tagId 标签ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteByTagId(Integer tagId) {
        return this.remove(new LambdaQueryWrapper<IdTags>()
                .eq(IdTags::getTagsId, tagId));
    }

    /**
     * 批量保存商品标签关联
     *
     * @param productId 商品ID
     * @param tagIds 标签ID列表
     * @return 是否保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProductTags(Integer productId, List<Integer> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }
        
        // 先删除原有关联
        this.deleteByProductId(productId);
        
        // 批量插入新关联
        List<IdTags> idTagsList = tagIds.stream()
                .map(tagId -> {
                    IdTags idTags = new IdTags();
                    idTags.setProductId(productId);
                    idTags.setTagsId(tagId);
                    return idTags;
                })
                .collect(Collectors.toList());
        
        return this.saveBatch(idTagsList);
    }

    /**
     * 根据商品ID获取关联的标签ID列表
     *
     * @param productId 商品ID
     * @return 标签ID列表
     */
    @Override
    public List<Integer> getTagIdsByProductId(Integer productId) {
        List<IdTags> idTagsList = this.list(new LambdaQueryWrapper<IdTags>()
                .eq(IdTags::getProductId, productId));
        return idTagsList.stream()
                .map(IdTags::getTagsId)
                .collect(Collectors.toList());
    }

    /**
     * 根据标签ID获取关联的商品ID列表
     *
     * @param tagId 标签ID
     * @return 商品ID列表
     */
    @Override
    public List<Integer> getProductIdsByTagId(Integer tagId) {
        List<IdTags> idTagsList = this.list(new LambdaQueryWrapper<IdTags>()
                .eq(IdTags::getTagsId, tagId));
        return idTagsList.stream()
                .map(IdTags::getProductId)
                .collect(Collectors.toList());
    }

}
