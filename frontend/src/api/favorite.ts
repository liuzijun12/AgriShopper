import request from "@/utils/request";

const FAVORITE_BASE_URL = "/api/v1/userFavorite";

export interface FavoriteItem {
  id?: number;
  productId: number;
  productName?: string;
  productImage?: string;
  price?: number;
  originalPrice?: number;
  status?: number;
  createTime?: string;
}

export interface FavoritePageQuery {
  pageNum?: number;
  pageSize?: number;
  keywords?: string;
}

const FavoriteAPI = {
  /**
   * 获取收藏列表
   * @param queryParams 查询参数
   */
  getPage(queryParams: FavoritePageQuery): Promise<PageResult<FavoriteItem[]>> {
    return request<PageResult<FavoriteItem[]>>({
      url: `${FAVORITE_BASE_URL}/page`,
      method: "GET",
      data: queryParams,
    });
  },

  /**
   * 添加收藏
   * @param productId 商品ID
   */
  add(productId: number): Promise<any> {
    return request<any>({
      url: `${FAVORITE_BASE_URL}/add`,
      method: "POST",
      data: { productId },
    });
  },

  /**
   * 取消收藏
   * @param productId 商品ID
   */
  remove(productId: number): Promise<any> {
    return request<any>({
      url: `${FAVORITE_BASE_URL}/remove`,
      method: "DELETE",
      data: { productId },
    });
  },

  /**
   * 批量取消收藏
   * @param ids 收藏ID数组
   */
  batchRemove(ids: number[]): Promise<any> {
    return request<any>({
      url: `${FAVORITE_BASE_URL}/batch-remove`,
      method: "DELETE",
      data: { ids },
    });
  },

  /**
   * 检查商品是否已收藏
   * @param productId 商品ID
   */
  checkFavorite(productId: number): Promise<{ isFavorite: boolean }> {
    return request<{ isFavorite: boolean }>({
      url: `${FAVORITE_BASE_URL}/check/${productId}`,
      method: "GET",
    });
  },

  /**
   * 获取收藏数量
   */
  getCount(): Promise<{ count: number }> {
    return request<{ count: number }>({
      url: `${FAVORITE_BASE_URL}/count`,
      method: "GET",
    });
  },
};

export default FavoriteAPI;