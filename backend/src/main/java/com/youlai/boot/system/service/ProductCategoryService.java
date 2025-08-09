package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.ProductCategory;
import com.youlai.boot.system.model.form.ProductCategoryForm;
import com.youlai.boot.system.model.query.ProductCategoryQuery;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 分类服务类
 *
 * @author liuzijun
 * @since 2025-08-08 12:02
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     *分类分页列表
     *
     * @return {@link IPage<ProductCategoryVO>} 分类分页列表
     */
    IPage<ProductCategoryVO> getProductCategoryPage(ProductCategoryQuery queryParams);

    /**
     * 获取分类表单数据
     *
     * @param id 分类ID
     * @return 分类表单数据
     */
    ProductCategoryForm getProductCategoryFormData(Long id);

    /**
     * 新增分类
     *
     * @param formData 分类表单对象
     * @return 是否新增成功
     */
    boolean saveProductCategory(ProductCategoryForm formData);

    /**
     * 修改分类
     *
     * @param id   分类ID
     * @param formData 分类表单对象
     * @return 是否修改成功
     */
    boolean updateProductCategory(Long id, ProductCategoryForm formData);

    /**
     * 删除分类
     *
     * @param ids 分类ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteProductCategorys(String ids);

    /**
     * 获取分类树形结构
     *
     * @return 分类树形结构列表
     */
    List<ProductCategoryVO> getCategoryTree();

}
