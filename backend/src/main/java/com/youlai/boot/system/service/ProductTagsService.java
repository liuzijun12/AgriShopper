package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.ProductTags;
import com.youlai.boot.system.model.form.ProductTagsForm;
import com.youlai.boot.system.model.query.ProductTagsQuery;
import com.youlai.boot.system.model.vo.ProductTagsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 标签服务类
 *
 * @author liuzijun
 * @since 2025-08-08 12:10
 */
public interface ProductTagsService extends IService<ProductTags> {

    /**
     *标签分页列表
     *
     * @return {@link IPage<ProductTagsVO>} 标签分页列表
     */
    IPage<ProductTagsVO> getProductTagsPage(ProductTagsQuery queryParams);

    /**
     * 获取标签表单数据
     *
     * @param id 标签ID
     * @return 标签表单数据
     */
    ProductTagsForm getProductTagsFormData(Long id);

    /**
     * 新增标签
     *
     * @param formData 标签表单对象
     * @return 是否新增成功
     */
    boolean saveProductTags(ProductTagsForm formData);

    /**
     * 修改标签
     *
     * @param id   标签ID
     * @param formData 标签表单对象
     * @return 是否修改成功
     */
    boolean updateProductTags(Long id, ProductTagsForm formData);

    /**
     * 删除标签
     *
     * @param ids 标签ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteProductTagss(String ids);

}
