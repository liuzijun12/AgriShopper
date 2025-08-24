import request from "@/utils/request";

const ADDRESS_BASE_URL = "/api/v1/address";

const AddressAPI = {
    /** 获取存储用户收货地址信息分页数据 */
    getPage(queryParams?: AddressPageQuery) {
        return request<any, PageResult<AddressPageVO[]>>({
            url: `${ADDRESS_BASE_URL}/page`,
            method: "get",
            params: queryParams,
        });
    },
    /**
     * 获取存储用户收货地址信息表单数据
     *
     * @param id 存储用户收货地址信息ID
     * @returns 存储用户收货地址信息表单数据
     */
    getFormData(id: number) {
        return request<any, AddressForm>({
            url: `${ADDRESS_BASE_URL}/${id}/form`,
            method: "get",
        });
    },

    /**
     *  添加存储用户收货地址信息
     *
     *  @param data 存储用户收货地址信息表单数据
     */
    add(data: AddressForm) {
        return request({
            url: `${ADDRESS_BASE_URL}`,
            method: "post",
            data: data,
        });
    },

    /**
     * 更新存储用户收货地址信息
     *
     * @param id 存储用户收货地址信息ID
     * @param data 存储用户收货地址信息表单数据
     */
     update(id: number, data: AddressForm) {
        return request({
            url: `${ADDRESS_BASE_URL}/${id}`,
            method: "put",
            data: data,
        });
    },

    /**
     * 批量删除存储用户收货地址信息，多个以英文逗号(,)分割
     *
     * @param ids 存储用户收货地址信息ID字符串，多个以英文逗号(,)分割
     */
     deleteByIds(ids: string) {
        return request({
            url: `${ADDRESS_BASE_URL}/${ids}`,
            method: "delete",
        });
    }
}

export default AddressAPI;

/** 存储用户收货地址信息分页查询参数 */
export interface AddressPageQuery extends PageQuery {
}

/** 存储用户收货地址信息表单对象 */
export interface AddressForm {
    id?:  number;
    /** 关联用户ID */
    userId?:  number;
    /** 收货人姓名 */
    receiverName?:  string;
    /** 联系电话 */
    phone?:  string;
    /** 省份 */
    province?:  string;
    /** 城市 */
    city?:  string;
    /** 区县 */
    district?:  string;
    /** 详细地址 */
    detailAddress?:  string;
    /** 邮政编码 */
    postalCode?:  string;
    /** 是否默认地址 */
    isDefault?:  number;
    /** 是否软删除 */
    isDeleted?:  number;
    /** 创建时间 */
    createTime?:  Date;
    /** 更新时间 */
    updateTime?:  Date;
    /** 删除时间 */
    deleteTime?:  Date;
}

/** 存储用户收货地址信息分页对象 */
export interface AddressPageVO {
    id?: number;
    /** 关联用户ID */
    userId?: number;
    /** 收货人姓名 */
    receiverName?: string;
    /** 联系电话 */
    phone?: string;
    /** 省份 */
    province?: string;
    /** 城市 */
    city?: string;
    /** 区县 */
    district?: string;
    /** 详细地址 */
    detailAddress?: string;
    /** 邮政编码 */
    postalCode?: string;
    /** 是否默认地址 */
    isDefault?: number;
    /** 是否软删除 */
    isDeleted?: number;
    /** 创建时间 */
    createTime?: Date;
    /** 更新时间 */
    updateTime?: Date;
    /** 删除时间 */
    deleteTime?: Date;
}
