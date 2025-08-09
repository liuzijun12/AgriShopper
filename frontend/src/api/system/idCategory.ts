import request from "@/utils/request";

const IDCATEGORY_BASE_URL = "/api/v1/id-category";

const idCategoryApi = {
  /** 获取分类关联表分页数据 */
  getPage(queryParams?: idCategoryPageQuery) {
    return request<any, PageResult<idCategoryPageVO[]>>({
      url: `${IDCATEGORY_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /**
   * 获取分类关联表表单数据
   *
   * @param id 分类关联表ID
   * @returns 分类关联表表单数据
   */
  getFormData(id: number) {
    return request<any, idCategoryForm>({
      url: `${IDCATEGORY_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   *  添加分类关联表
   *
   *  @param data 分类关联表表单数据
   */
  add(data: idCategoryForm) {
    return request({
      url: `${IDCATEGORY_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 更新分类关联表
   *
   * @param id 分类关联表ID
   * @param data 分类关联表表单数据
   */
  update(id: number, data: idCategoryForm) {
    return request({
      url: `${IDCATEGORY_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 批量删除分类关联表，多个以英文逗号(,)分割
   *
   * @param ids 分类关联表ID字符串，多个以英文逗号(,)分割
   */
  deleteByIds(ids: string) {
    return request({
      url: `${IDCATEGORY_BASE_URL}/${ids}`,
      method: "delete",
    });
  }
}

export default idCategoryApi;

/** 分类关联表分页查询参数 */
export interface idCategoryPageQuery extends PageQuery {
}

/** 分类关联表表单对象 */
export interface idCategoryForm {
  id?:  number;
  productId?:  number;
  categoryId?:  number;
}

/** 分类关联表分页对象 */
export interface idCategoryPageVO {
  id?: number;
  productId?: number;
  categoryId?: number;
}