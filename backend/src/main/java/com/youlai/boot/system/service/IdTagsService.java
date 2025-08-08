package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.IdTags;
import com.youlai.boot.system.model.form.IdTagsForm;
import com.youlai.boot.system.model.query.IdTagsQuery;
import com.youlai.boot.system.model.vo.IdTagsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 标签关联表服务类
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
public interface IdTagsService extends IService<IdTags> {

    /**
     *标签关联表分页列表
     *
     * @return {@link IPage<IdTagsVO>} 标签关联表分页列表
     */
    IPage<IdTagsVO> getIdTagsPage(IdTagsQuery queryParams);

    /**
     * 获取标签关联表表单数据
     *
     * @param id 标签关联表ID
     * @return 标签关联表表单数据
     */
    IdTagsForm getIdTagsFormData(Long id);

    /**
     * 新增标签关联表
     *
     * @param formData 标签关联表表单对象
     * @return 是否新增成功
     */
    boolean saveIdTags(IdTagsForm formData);

    /**
     * 修改标签关联表
     *
     * @param id   标签关联表ID
     * @param formData 标签关联表表单对象
     * @return 是否修改成功
     */
    boolean updateIdTags(Long id, IdTagsForm formData);

    /**
     * 删除标签关联表
     *
     * @param ids 标签关联表ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteIdTagss(String ids);

}
