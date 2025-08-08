package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.IdCategory;
import com.youlai.boot.system.model.form.IdCategoryForm;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 分类关联服务类
 *
 * @author liuzijun
 * @since 2025-08-08 12:24
 */
public interface IdCategoryService extends IService<IdCategory> {

    /**
     *分类关联分页列表
     *
     * @return {@link IPage<IdCategoryVO>} 分类关联分页列表
     */
    IPage<IdCategoryVO> getIdCategoryPage(IdCategoryQuery queryParams);

    /**
     * 获取分类关联表单数据
     *
     * @param id 分类关联ID
     * @return 分类关联表单数据
     */
    IdCategoryForm getIdCategoryFormData(Long id);

    /**
     * 新增分类关联
     *
     * @param formData 分类关联表单对象
     * @return 是否新增成功
     */
    boolean saveIdCategory(IdCategoryForm formData);

    /**
     * 修改分类关联
     *
     * @param id   分类关联ID
     * @param formData 分类关联表单对象
     * @return 是否修改成功
     */
    boolean updateIdCategory(Long id, IdCategoryForm formData);

    /**
     * 删除分类关联
     *
     * @param ids 分类关联ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteIdCategorys(String ids);

}
