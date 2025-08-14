import request from "@/utils/request";

const USERFAVORITE_BASE_URL = "/api/v1/userFavorite";

const UserFavoriteAPI = {
    /** 获取收藏分页数据 */
    getPage(queryParams?: UserFavoritePageQuery) {
        return request<any, PageResult<UserFavoritePageVO[]>>({
            url: `${USERFAVORITE_BASE_URL}/page`,
            method: "get",
            params: queryParams,
        });
    },
    /**
     * 获取收藏表单数据
     *
     * @param id 收藏ID
     * @returns 收藏表单数据
     */
    getFormData(id: number) {
        return request<any, UserFavoriteForm>({
            url: `${USERFAVORITE_BASE_URL}/${id}/form`,
            method: "get",
        });
    },

    /**
     *  添加收藏
     *
     *  @param data 收藏表单数据
     */
    add(data: UserFavoriteForm) {
        return request({
            url: `${USERFAVORITE_BASE_URL}`,
            method: "post",
            data: data,
        });
    },

    /**
     * 更新收藏
     *
     * @param id 收藏ID
     * @param data 收藏表单数据
     */
     update(id: number, data: UserFavoriteForm) {
        return request({
            url: `${USERFAVORITE_BASE_URL}/${id}`,
            method: "put",
            data: data,
        });
    },

    /**
     * 批量删除收藏，多个以英文逗号(,)分割
     *
     * @param ids 收藏ID字符串，多个以英文逗号(,)分割
     */
     deleteByIds(ids: string) {
        return request({
            url: `${USERFAVORITE_BASE_URL}/${ids}`,
            method: "delete",
        });
    }
}

export default UserFavoriteAPI;

/** 收藏分页查询参数 */
export interface UserFavoritePageQuery extends PageQuery {
}

/** 收藏表单对象 */
export interface UserFavoriteForm {
    id?:  number;
    /** 识别用户的唯一标识 */
    userId?:  number;
    /** 商品的id */
    productId?:  number;
    /** 是否软删除 */
    isDeleted?:  number;
    /** 创建时间 */
    createTime?:  Date;
    /** 更新时间 */
    updateTime?:  Date;
    /** 删除时间 */
    deleteTime?:  Date;
}

/** 收藏分页对象 */
export interface UserFavoritePageVO {
    id?: number;
    /** 识别用户的唯一标识 */
    userId?: number;
    /** 商品的id */
    productId?: number;
    /** 是否软删除 */
    isDeleted?: number;
    /** 创建时间 */
    createTime?: Date;
    /** 更新时间 */
    updateTime?: Date;
    /** 删除时间 */
    deleteTime?: Date;
}
