package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.IdTags;
import com.youlai.boot.system.model.form.IdTagsForm;
import com.youlai.boot.system.model.query.IdTagsQuery;
import com.youlai.boot.system.model.vo.IdTagsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

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

    /**
     * 根据商品ID删除所有标签关联
     *
     * @param productId 商品ID
     * @return 是否删除成功
     */
    boolean deleteByProductId(Integer productId);

    /**
     * 根据标签ID删除所有商品关联
     *
     * @param tagId 标签ID
     * @return 是否删除成功
     */
    boolean deleteByTagId(Integer tagId);

    /**
     * 批量保存商品标签关联
     *
     * @param productId 商品ID
     * @param tagIds 标签ID列表
     * @return 是否保存成功
     */
    boolean saveProductTags(Integer productId, List<Integer> tagIds);

    /**
     * 根据商品ID获取关联的标签ID列表
     *
     * @param productId 商品ID
     * @return 标签ID列表
     */
    List<Integer> getTagIdsByProductId(Integer productId);

    /**
     * 根据标签ID获取关联的商品ID列表
     *
     * @param tagId 标签ID
     * @return 商品ID列表
     */
    List<Integer> getProductIdsByTagId(Integer tagId);

}
