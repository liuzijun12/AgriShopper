import request from "@/utils/request";

const CART_BASE_URL = "/api/v1/product-cart";

export interface CartItem {
  id?: number;
  productId: number;
  productName?: string;
  productImage?: string;
  price?: number;
  quantity: number;
  selected?: boolean;
  createTime?: string;
  updateTime?: string;
}

export interface CartSummary {
  totalItems: number;
  totalPrice: number;
  selectedItems: number;
  selectedPrice: number;
}

const CartAPI = {
  /**
   * 获取购物车列表
   */
  getList(): Promise<CartItem[]> {
    return request<CartItem[]>({
      url: `${CART_BASE_URL}/list`,
      method: "GET",
    });
  },

  /**
   * 添加商品到购物车
   * @param productId 商品ID
   * @param quantity 数量
   */
  add(productId: number, quantity: number = 1): Promise<any> {
    return request<any>({
      url: `${CART_BASE_URL}/add`,
      method: "POST",
      data: { productId, quantity },
    });
  },

  /**
   * 更新购物车商品数量
   * @param id 购物车项ID
   * @param quantity 新数量
   */
  updateQuantity(id: number, quantity: number): Promise<any> {
    return request<any>({
      url: `${CART_BASE_URL}/${id}/quantity`,
      method: "PUT",
      data: { quantity },
    });
  },

  /**
   * 删除购物车商品
   * @param ids 购物车项ID数组
   */
  remove(ids: number[]): Promise<any> {
    return request<any>({
      url: `${CART_BASE_URL}/remove`,
      method: "DELETE",
      data: { ids },
    });
  },

  /**
   * 清空购物车
   */
  clear(): Promise<any> {
    return request<any>({
      url: `${CART_BASE_URL}/clear`,
      method: "DELETE",
    });
  },

  /**
   * 获取购物车统计信息
   */
  getSummary(): Promise<CartSummary> {
    return request<CartSummary>({
      url: `${CART_BASE_URL}/summary`,
      method: "GET",
    });
  },

  /**
   * 批量选择/取消选择购物车商品
   * @param ids 购物车项ID数组
   * @param selected 是否选中
   */
  batchSelect(ids: number[], selected: boolean): Promise<any> {
    return request<any>({
      url: `${CART_BASE_URL}/batch-select`,
      method: "PUT",
      data: { ids, selected },
    });
  },
};

export default CartAPI;