import request from "@/utils/request";

const PRODUCTCATEGORY_BASE_URL = "/api/v1/product-category";

const ProductCategoryAPI = {
  /** 获取分类分页数据 */
  getPage(queryParams?: ProductCategoryPageQuery) {
    return request<any, PageResult<ProductCategoryPageVO[]>>({
      url: `${PRODUCTCATEGORY_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /**
   * 获取分类表单数据
   *
   * @param id 分类ID
   * @returns 分类表单数据
   */
  getFormData(id: number) {
    return request<any, ProductCategoryForm>({
      url: `${PRODUCTCATEGORY_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   *  添加分类
   *
   *  @param data 分类表单数据
   */
  add(data: ProductCategoryForm) {
    return request({
      url: `${PRODUCTCATEGORY_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 更新分类
   *
   * @param id 分类ID
   * @param data 分类表单数据
   */
  update(id: number, data: ProductCategoryForm) {
    return request({
      url: `${PRODUCTCATEGORY_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 批量删除分类，多个以英文逗号(,)分割
   *
   * @param ids 分类ID字符串，多个以英文逗号(,)分割
   */
  deleteByIds(ids: string) {
    return request({
      url: `${PRODUCTCATEGORY_BASE_URL}/${ids}`,
      method: "delete",
    });
  }
}

export default ProductCategoryAPI;

/** 分类分页查询参数 */
export interface ProductCategoryPageQuery extends PageQuery {
}

/** 分类表单对象 */
export interface ProductCategoryForm {
  id?:  number;
  /** 分类名称 */
  name?:  string;
  /** 分类图释 */
  icon?:  string;
  /** 一级id */
  parentId?:  number;
}

/** 分类分页对象 */
export interface ProductCategoryPageVO {
  id?: number;
  /** 分类名称 */
  name?: string;
  /** 分类图释 */
  icon?: string;
  /** 一级id */
  parentId?: number;
}
