package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.IdCategory;
import com.youlai.boot.system.model.form.IdCategoryForm;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

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

    /**
     * 批量保存商品分类关联
     *
     * @param productId 商品ID
     * @param categoryIds 分类ID列表
     * @return 是否保存成功
     */
    boolean saveProductCategories(Integer productId, List<Integer> categoryIds);

    /**
     * 根据商品ID删除分类关联
     *
     * @param productId 商品ID
     * @return 是否删除成功
     */
    boolean deleteByProductId(Integer productId);

    /**
     * 根据分类ID删除关联
     *
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    boolean deleteByCategoryId(Integer categoryId);

    /**
     * 根据商品ID获取关联的分类ID列表
     *
     * @param productId 商品ID
     * @return 分类ID列表
     */
    List<Integer> getCategoryIdsByProductId(Integer productId);

    /**
     * 根据分类ID获取关联的商品ID列表
     *
     * @param categoryId 分类ID
     * @return 商品ID列表
     */
    List<Integer> getProductIdsByCategoryId(Integer categoryId);

}
