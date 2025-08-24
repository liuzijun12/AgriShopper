import request from "@/utils/request";

const ORDERS_BASE_URL = "/api/v1/orders";

const OrdersAPI = {
    /** 获取订单分页数据 */
    getPage(queryParams?: OrdersPageQuery) {
        return request<any, PageResult<OrdersPageVO[]>>({
            url: `${ORDERS_BASE_URL}/page`,
            method: "get",
            params: queryParams,
        });
    },
    /**
     * 获取订单表单数据
     *
     * @param id 订单ID
     * @returns 订单表单数据
     */
    getFormData(id: number) {
        return request<any, OrdersForm>({
            url: `${ORDERS_BASE_URL}/${id}/form`,
            method: "get",
        });
    },

    /**
     *  添加订单
     *
     *  @param data 订单表单数据
     */
    add(data: OrdersForm) {
        return request({
            url: `${ORDERS_BASE_URL}`,
            method: "post",
            data: data,
        });
    },

    /**
     * 更新订单
     *
     * @param id 订单ID
     * @param data 订单表单数据
     */
     update(id: number, data: OrdersForm) {
        return request({
            url: `${ORDERS_BASE_URL}/${id}`,
            method: "put",
            data: data,
        });
    },

    /**
     * 批量删除订单，多个以英文逗号(,)分割
     *
     * @param ids 订单ID字符串，多个以英文逗号(,)分割
     */
     deleteByIds(ids: string) {
        return request({
            url: `${ORDERS_BASE_URL}/${ids}`,
            method: "delete",
        });
    }
}

export default OrdersAPI;

/** 订单分页查询参数 */
export interface OrdersPageQuery extends PageQuery {
}

/** 订单表单对象 */
export interface OrdersForm {
    id?:  number;
    /** 关联用户ID */
    userId?:  number;
    /** 完整地址快照 */
    addressSnapshot?:  string;
    /** 关联原始地址 */
    originalAddressId?:  number;
    /** 订单的状态 */
    status?:  string;
    /** 订单总金额 */
    totalAmount?:  number;
    /** 是否软删除 */
    isDeleted?:  number;
    /** 创建时间 */
    createTime?:  Date;
    /** 更新时间 */
    updateTime?:  Date;
    /** 删除时间 */
    deleteTime?:  Date;
}

/** 订单分页对象 */
export interface OrdersPageVO {
    id?: number;
    /** 关联用户ID */
    userId?: number;
    /** 完整地址快照 */
    addressSnapshot?: string;
    /** 关联原始地址 */
    originalAddressId?: number;
    /** 订单的状态 */
    status?: string;
    /** 订单总金额 */
    totalAmount?: number;
    /** 是否软删除 */
    isDeleted?: number;
    /** 创建时间 */
    createTime?: Date;
    /** 更新时间 */
    updateTime?: Date;
    /** 删除时间 */
    deleteTime?: Date;
}
