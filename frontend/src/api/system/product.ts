import request from "@/utils/request";

const PRODUCT_BASE_URL = "/api/v1/product";

const ProductAPI = {
    /** 获取商品表分页数据 */
    getPage(queryParams?: ProductPageQuery) {
        return request<any, PageResult<ProductPageVO[]>>({
            url: `${PRODUCT_BASE_URL}/page`,
            method: "get",
            params: queryParams,
        });
    },
    /**
     * 获取商品表表单数据
     *
     * @param id 商品表ID
     * @returns 商品表表单数据
     */
    getFormData(id: number) {
        return request<any, ProductForm>({
            url: `${PRODUCT_BASE_URL}/${id}/form`,
            method: "get",
        });
    },

    /**
     *  添加商品表
     *
     *  @param data 商品表表单数据
     */
    add(data: ProductForm) {
        return request({
            url: `${PRODUCT_BASE_URL}`,
            method: "post",
            data: data,
        });
    },

    /**
     * 更新商品表
     *
     * @param id 商品表ID
     * @param data 商品表表单数据
     */
     update(id: number, data: ProductForm) {
        return request({
            url: `${PRODUCT_BASE_URL}/${id}`,
            method: "put",
            data: data,
        });
    },

    /**
     * 批量删除商品表，多个以英文逗号(,)分割
     *
     * @param ids 商品表ID字符串，多个以英文逗号(,)分割
     */
     deleteByIds(ids: string) {
        return request({
            url: `${PRODUCT_BASE_URL}/${ids}`,
            method: "delete",
        });
    }
}

export default ProductAPI;

/** 商品表分页查询参数 */
export interface ProductPageQuery extends PageQuery {
}

/** 商品表表单对象 */
export interface ProductForm {
    /** 商品ID */
    id?:  number;
    /** 商品名称 */
    name?:  string;
    /** 商品图片，JSON数组格式存储多张图片URL */
    images?:  string;
    /** 商品简介 */
    description?:  string;
    /** 原价 */
    price?:  number;
    /** 是否有优惠 */
    hasDiscount?:  number;
    /** 优惠价 */
    discountPrice?:  number;
    /** 销量 */
    sales?:  number;
    /** 标签，多个标签用逗号分隔 */
    tags?:  string;
    /** 产地 */
    origin?:  string;
    /** 是否为热门推荐商品 */
    isHot?:  number;
    /** 创建时间 */
    createdAt?:  ${fieldConfig.tsType};
    /** 更新时间 */
    updatedAt?:  ${fieldConfig.tsType};
    /** 虚拟销量
 */
    virtualSales?:  number;
    /** 库存 */
    stock?:  number;
    /** 是否上架
 */
    isOnline?:  number;
    /** 规格 */
    type?:  string;
    /** 视频 */
    vedio?:  string;
}

/** 商品表分页对象 */
export interface ProductPageVO {
    /** 商品ID */
    id?: number;
    /** 商品名称 */
    name?: string;
    /** 商品图片，JSON数组格式存储多张图片URL */
    images?: string;
    /** 商品简介 */
    description?: string;
    /** 原价 */
    price?: number;
    /** 是否有优惠 */
    hasDiscount?: number;
    /** 优惠价 */
    discountPrice?: number;
    /** 销量 */
    sales?: number;
    /** 标签，多个标签用逗号分隔 */
    tags?: string;
    /** 产地 */
    origin?: string;
    /** 是否为热门推荐商品 */
    isHot?: number;
    /** 创建时间 */
    createdAt?: ${fieldConfig.tsType};
    /** 更新时间 */
    updatedAt?: ${fieldConfig.tsType};
}
