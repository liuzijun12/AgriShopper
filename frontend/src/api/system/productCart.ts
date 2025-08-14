import request from "@/utils/request";

const PRODUCTCART_BASE_URL = "/api/v1/productCart";

const ProductCartAPI = {
    /** 获取购物车分页数据 */
    getPage(queryParams?: ProductCartPageQuery) {
        return request<any, PageResult<ProductCartPageVO[]>>({
            url: `${PRODUCTCART_BASE_URL}/page`,
            method: "get",
            params: queryParams,
        });
    },
    /**
     * 获取购物车表单数据
     *
     * @param id 购物车ID
     * @returns 购物车表单数据
     */
    getFormData(id: number) {
        return request<any, ProductCartForm>({
            url: `${PRODUCTCART_BASE_URL}/${id}/form`,
            method: "get",
        });
    },

    /**
     *  添加购物车
     *
     *  @param data 购物车表单数据
     */
    add(data: ProductCartForm) {
        return request({
            url: `${PRODUCTCART_BASE_URL}`,
            method: "post",
            data: data,
        });
    },

    /**
     * 更新购物车
     *
     * @param id 购物车ID
     * @param data 购物车表单数据
     */
     update(id: number, data: ProductCartForm) {
        return request({
            url: `${PRODUCTCART_BASE_URL}/${id}`,
            method: "put",
            data: data,
        });
    },

    /**
     * 批量删除购物车，多个以英文逗号(,)分割
     *
     * @param ids 购物车ID字符串，多个以英文逗号(,)分割
     */
     deleteByIds(ids: string) {
        return request({
            url: `${PRODUCTCART_BASE_URL}/${ids}`,
            method: "delete",
        });
    }
}

export default ProductCartAPI;

/** 购物车分页查询参数 */
export interface ProductCartPageQuery extends PageQuery {
}

/** 购物车表单对象 */
export interface ProductCartForm {
    id?:  number;
    /** 用户唯一标识 */
    userId?:  number;
    /** 商品ID */
    productId?:  number;
    /** 选择商品的规格 */
    productType?:  string;
    /** 商品的数量 */
    productCount?:  number;
    /** 是否软删除 */
    isDeleted?:  number;
    /** 所选规格的价格 */
    productPrice?:  number;
    /** 创建时间 */
    createTime?:  Date;
    /** 更新时间 */
    updateTime?:  Date;
    /** 删除时间 */
    deleteTime?:  Date;
}

/** 购物车分页对象 */
export interface ProductCartPageVO {
    id?: number;
    /** 用户唯一标识 */
    userId?: number;
    /** 商品ID */
    productId?: number;
    /** 选择商品的规格 */
    productType?: string;
    /** 商品的数量 */
    productCount?: number;
    /** 是否软删除 */
    isDeleted?: number;
    /** 所选规格的价格 */
    productPrice?: number;
    /** 创建时间 */
    createTime?: Date;
    /** 更新时间 */
    updateTime?: Date;
    /** 删除时间 */
    deleteTime?: Date;
}

