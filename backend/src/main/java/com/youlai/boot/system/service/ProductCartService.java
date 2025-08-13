package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.ProductCart;
import com.youlai.boot.system.model.form.ProductCartForm;
import com.youlai.boot.system.model.query.ProductCartQuery;
import com.youlai.boot.system.model.vo.ProductCartVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 购物车服务类
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
public interface ProductCartService extends IService<ProductCart> {

    /**
     *购物车分页列表
     *
     * @return {@link IPage<ProductCartVO>} 购物车分页列表
     */
    IPage<ProductCartVO> getProductCartPage(ProductCartQuery queryParams);

    /**
     * 获取购物车表单数据
     *
     * @param id 购物车ID
     * @return 购物车表单数据
     */
     ProductCartForm getProductCartFormData(Long id);

    /**
     * 新增购物车
     *
     * @param formData 购物车表单对象
     * @return 是否新增成功
     */
    boolean saveProductCart(ProductCartForm formData);

    /**
     * 修改购物车
     *
     * @param id   购物车ID
     * @param formData 购物车表单对象
     * @return 是否修改成功
     */
    boolean updateProductCart(Long id, ProductCartForm formData);

    /**
     * 删除购物车
     *
     * @param ids 购物车ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteProductCarts(String ids);

}
