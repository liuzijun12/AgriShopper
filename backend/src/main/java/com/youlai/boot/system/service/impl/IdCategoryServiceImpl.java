package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.IdCategoryMapper;
import com.youlai.boot.system.service.IdCategoryService;
import com.youlai.boot.system.model.entity.IdCategory;
import com.youlai.boot.system.model.form.IdCategoryForm;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
import com.youlai.boot.system.converter.IdCategoryConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 分类关联服务实现类
 *
 * @author liuzijun
 * @since 2025-08-08 12:24
 */
@Service
@RequiredArgsConstructor
public class IdCategoryServiceImpl extends ServiceImpl<IdCategoryMapper, IdCategory> implements IdCategoryService {

    private final IdCategoryConverter idCategoryConverter;

    /**
     * 获取分类关联分页列表
     *
     * @param queryParams 查询参数
     * @return {@link IPage<IdCategoryVO>} 分类关联分页列表
     */
    @Override
    public IPage<IdCategoryVO> getIdCategoryPage(IdCategoryQuery queryParams) {
        Page<IdCategoryVO> pageVO = this.baseMapper.getIdCategoryPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }

    /**
     * 获取分类关联表单数据
     *
     * @param id 分类关联ID
     * @return 分类关联表单数据
     */
    @Override
    public IdCategoryForm getIdCategoryFormData(Long id) {
        IdCategory entity = this.getById(id);
        return idCategoryConverter.toForm(entity);
    }

    /**
     * 新增分类关联
     *
     * @param formData 分类关联表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveIdCategory(IdCategoryForm formData) {
        IdCategory entity = idCategoryConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 更新分类关联
     *
     * @param id   分类关联ID
     * @param formData 分类关联表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateIdCategory(Long id,IdCategoryForm formData) {
        IdCategory entity = idCategoryConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 删除分类关联
     *
     * @param ids 分类关联ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteIdCategorys(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的分类关联数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
