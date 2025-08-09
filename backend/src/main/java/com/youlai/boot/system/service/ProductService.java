package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.Product;
import com.youlai.boot.system.model.form.ProductForm;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import com.youlai.boot.system.model.vo.TagVO;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 商品表服务类
 *
 * @author youlaitech
 * @since 2025-08-08 11:28
 */
public interface ProductService extends IService<Product> {

    /**
     *商品表分页列表
     *
     * @return {@link IPage<ProductVO>} 商品表分页列表
     */
    IPage<ProductVO> getProductPage(ProductQuery queryParams);

    /**
     * 获取商品表表单数据
     *
     * @param id 商品表ID
     * @return 商品表表单数据
     */
    ProductForm getProductFormData(Long id);

    /**
     * 新增商品表
     *
     * @param formData 商品表表单对象
     * @return 是否新增成功
     */
    boolean saveProduct(ProductForm formData);

    /**
     * 修改商品表
     *
     * @param id   商品表ID
     * @param formData 商品表表单对象
     * @return 是否修改成功
     */
    boolean updateProduct(Long id, ProductForm formData);

    /**
     * 删除商品表
     *
     * @param ids 商品表ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteProducts(String ids);

    /**
     * 根据ID获取商品信息
     *
     * @param id 商品ID
     * @return 商品信息
     */
    ProductVO getProductById(Long id);

    /**
     * 获取商品详情（包含标签）
     *
     * @param id 商品ID
     * @return 商品详情
     */
    ProductVO getProductWithTags(Long id);

    /**
     * 通过商品ID获取标签列表
     *
     * @param productId 商品ID
     * @return 标签列表
     */
    List<TagVO> getTagsByProductId(Long productId);

    /**
     * 获取商品详情（包含分类）
     *
     * @param id 商品ID
     * @return 商品详情
     */
    ProductVO getProductWithCategories(Long id);

    /**
     * 通过商品ID获取分类列表
     *
     * @param productId 商品ID
     * @return 分类列表
     */
    List<ProductCategoryVO> getCategoriesByProductId(Long productId);

}
