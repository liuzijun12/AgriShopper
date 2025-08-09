import request from "@/utils/request";

const PRODUCTTAGS_BASE_URL = "/api/v1/product-tags";

const ProductTagsAPI = {
  /** 获取标签分页数据 */
  getPage(queryParams?: ProductTagsPageQuery) {
    return request<any, PageResult<ProductTagsPageVO[]>>({
      url: `${PRODUCTTAGS_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /**
   * 获取标签表单数据
   *
   * @param id 标签ID
   * @returns 标签表单数据
   */
  getFormData(id: number) {
    return request<any, ProductTagsForm>({
      url: `${PRODUCTTAGS_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   *  添加标签
   *
   *  @param data 标签表单数据
   */
  add(data: ProductTagsForm) {
    return request({
      url: `${PRODUCTTAGS_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 更新标签
   *
   * @param id 标签ID
   * @param data 标签表单数据
   */
  update(id: number, data: ProductTagsForm) {
    return request({
      url: `${PRODUCTTAGS_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 批量删除标签，多个以英文逗号(,)分割
   *
   * @param ids 标签ID字符串，多个以英文逗号(,)分割
   */
  deleteByIds(ids: string) {
    return request({
      url: `${PRODUCTTAGS_BASE_URL}/${ids}`,
      method: "delete",
    });
  },

  /**
   * 获取所有标签列表
   *
   * @returns 标签列表
   */
  getAllTags() {
    return request<any, ProductTagsVO[]>({
      url: `${PRODUCTTAGS_BASE_URL}/all`,
      method: "get",
    });
  },

  /**
   * 获取标签树形结构
   *
   * @returns 标签树形结构
   */
  getTagTree() {
    return request<any, ProductTagsVO[]>({
      url: `${PRODUCTTAGS_BASE_URL}/tree`,
      method: "get",
    });
  }
}

export default ProductTagsAPI;

/** 标签分页查询参数 */
export interface ProductTagsPageQuery extends PageQuery {
}

/** 标签表单对象 */
export interface ProductTagsForm {
  id?:  number;
  /** 标签名
   */
  name?:  string;
  parentId?:  number;
}

/** 标签分页对象 */
export interface ProductTagsPageVO {
  id?: number;
  /** 标签名
   */
  name?: string;
  parentId?: number;
}

/** 标签对象 */
export interface ProductTagsVO {
  id?: number;
  /** 标签名 */
  name?: string;
  /** 父级ID */
  parentId?: number;
  /** 子标签列表 */
  children?: ProductTagsVO[];
  /** 创建时间 */
  createTime?: string;
  /** 更新时间 */
  updateTime?: string;
}
