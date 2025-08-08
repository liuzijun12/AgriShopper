import request from "@/utils/request";

const IDTAGS_BASE_URL = "/api/v1/id-tags";

const IdTagsAPI = {
  /** 获取标签关联表分页数据 */
  getPage(queryParams?: IdTagsPageQuery) {
    return request<any, PageResult<IdTagsPageVO[]>>({
      url: `${IDTAGS_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /**
   * 获取标签关联表表单数据
   *
   * @param id 标签关联表ID
   * @returns 标签关联表表单数据
   */
  getFormData(id: number) {
    return request<any, IdTagsForm>({
      url: `${IDTAGS_BASE_URL}/${id}/form`,
      method: "get",
    });
  },

  /**
   *  添加标签关联表
   *
   *  @param data 标签关联表表单数据
   */
  add(data: IdTagsForm) {
    return request({
      url: `${IDTAGS_BASE_URL}`,
      method: "post",
      data: data,
    });
  },

  /**
   * 更新标签关联表
   *
   * @param id 标签关联表ID
   * @param data 标签关联表表单数据
   */
  update(id: number, data: IdTagsForm) {
    return request({
      url: `${IDTAGS_BASE_URL}/${id}`,
      method: "put",
      data: data,
    });
  },

  /**
   * 批量删除标签关联表，多个以英文逗号(,)分割
   *
   * @param ids 标签关联表ID字符串，多个以英文逗号(,)分割
   */
  deleteByIds(ids: string) {
    return request({
      url: `${IDTAGS_BASE_URL}/${ids}`,
      method: "delete",
    });
  }
}

export default IdTagsAPI;

/** 标签关联表分页查询参数 */
export interface IdTagsPageQuery extends PageQuery {
}

/** 标签关联表表单对象 */
export interface IdTagsForm {
  id?:  number;
  productId?:  number;
  tagsId?:  number;
}

/** 标签关联表分页对象 */
export interface IdTagsPageVO {
  id?: number;
  productId?: number;
  tagsId?: number;
}
