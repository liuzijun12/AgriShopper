import request from "@/utils/request";

const WXUSER_BASE_URL = "/api/v1/wxuser";

const wxUserAPI = {
    /** 获取用户分页数据 */
    getPage(queryParams?: wxUserPageQuery) {
        return request<any, PageResult<wxUserPageVO[]>>({ 
            url: `${WXUSER_BASE_URL}/page`,
            method: "get",
            params: queryParams,
        });
    },
    /**
     * 获取用户表单数据
     *
     * @param id 用户ID
     * @returns 用户表单数据
     */
    getFormData(id: number) {
        return request<any, wxUserForm>({
            url: `${WXUSER_BASE_URL}/${id}/form`,
            method: "get",
        });
    },

    /**
     *  添加用户
     *
     *  @param data 用户表单数据
     */
    add(data: wxUserForm) {
        return request({
            url: `${WXUSER_BASE_URL}`,
            method: "post",
            data: data,
        });
    },

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param data 用户表单数据
     */
     update(id: number, data: wxUserForm) {
        return request({
            url: `${WXUSER_BASE_URL}/${id}`,
            method: "put",
            data: data,
        });
    },

    /**
     * 批量删除用户，多个以英文逗号(,)分割
     *
     * @param ids 用户ID字符串，多个以英文逗号(,)分割
     */
     deleteByIds(ids: string) {
        return request({
            url: `${WXUSER_BASE_URL}/${ids}`,
            method: "delete",
        });
    }
}

export default wxUserAPI;

/** 用户分页查询参数 */
export interface wxUserPageQuery extends PageQuery {
    /** 微信昵称 */
    nickname?: string;
    /** 手机号 */
    phone?: string;
    /** 真实姓名 */
    realName?: string;
}

/** 用户表单对象 */
export interface wxUserForm {
    id?:  number;
    /** 微信用户唯一ID */
    openid?:  string;
    /** 微信开放平台ID */
    unionid?:  string;
    /** 微信昵称 */
    nickname?:  string;
    /** 头像URL */
    avatar?:  string;
    /** 真实姓名 */
    realName?:  string;
    /** 手机号 */
    phone?:  string;
    /** 性别 */
    gender?:  number;
    /** 省份 */
    province?:  string;
    /** 城市 */
    city?:  string;
    /** 地区 */
    district?:  string;
    /** 是否是管理员 */
    isManager?:  number;
    /** 是否是超级管理员 */
    isSupermanager?:  number;
    /** 账户余额 */
    balance?:  number;
    /** 是否软删除 */
    isDeleted?:  number;
    /** 创建时间 */
    createTime?:  Date;
    /** 更新时间 */
    updateTime?:  Date;
    /** 删除时间 */
    deleteTime?:  Date;
}

/** 用户分页对象 */
export interface wxUserPageVO {
    id?: number;
    /** 微信用户唯一ID */
    openid?: string;
    /** 微信开放平台ID */
    unionid?: string;
    /** 微信昵称 */
    nickname?: string;
    /** 头像URL */
    avatar?: string;
    /** 真实姓名 */
    realName?: string;
    /** 手机号 */
    phone?: string;
    /** 性别 */
    gender?: number;
    /** 省份 */
    province?: string;
    /** 城市 */
    city?: string;
    /** 地区 */
    district?: string;
    /** 是否是管理员 */
    isManager?: number;
    /** 是否是超级管理员 */
    isSupermanager?: number;
    /** 账户余额 */
    balance?: number;
    /** 是否软删除 */
    isDeleted?: number;
    /** 创建时间 */
    createTime?: Date;
    /** 更新时间 */
    updateTime?: Date;
    /** 删除时间 */
    deleteTime?: Date;
}
