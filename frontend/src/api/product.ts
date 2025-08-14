import request from "@/utils/request";

const PRODUCT_BASE_URL = "/api/v1/product";

export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number;
  originalPrice?: number;
  stock: number;
  categoryId?: number;
  categoryName?: string;
  images?: string[];
  status?: number;
  createTime?: string;
  updateTime?: string;
}

export interface ProductPageQuery {
  pageNum?: number;
  pageSize?: number;
  keywords?: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  status?: number;
  sortBy?: string; // price_asc, price_desc, sales_desc, create_time_desc
}

export interface ProductPageVO {
  id: number;
  name: string;
  description?: string;
  price: number;
  originalPrice?: number;
  stock: number;
  categoryName?: string;
  mainImage?: string;
  images?: string[];
  status: number;
  sales?: number;
  createTime: string;
}

const ProductAPI = {
  /**
   * 获取商品分页列表
   * @param queryParams 查询参数
   */
  getPage(queryParams: ProductPageQuery): Promise<PageResult<ProductPageVO[]>> {
    return request<PageResult<ProductPageVO[]>>({
      url: `${PRODUCT_BASE_URL}/page`,
      method: "GET",
      data: queryParams,
    });
  },

  /**
   * 获取商品详情
   * @param id 商品ID
   */
  getDetail(id: number): Promise<Product> {
    return request<Product>({
      url: `${PRODUCT_BASE_URL}/${id}`,
      method: "GET",
    });
  },

  /**
   * 搜索商品
   * @param queryParams 搜索参数
   */
  search(queryParams: ProductPageQuery): Promise<PageResult<ProductPageVO[]>> {
    return request<PageResult<ProductPageVO[]>>({
      url: `${PRODUCT_BASE_URL}/search`,
      method: "GET",
      data: queryParams,
    });
  },

  /**
   * 获取热门商品
   * @param limit 数量限制
   */
  getHotProducts(limit: number = 10): Promise<ProductPageVO[]> {
    return request<ProductPageVO[]>({
      url: `${PRODUCT_BASE_URL}/hot`,
      method: "GET",
      data: { limit },
    });
  },

  /**
   * 获取推荐商品
   * @param limit 数量限制
   */
  getRecommendProducts(limit: number = 10): Promise<ProductPageVO[]> {
    return request<ProductPageVO[]>({
      url: `${PRODUCT_BASE_URL}/recommend`,
      method: "GET",
      data: { limit },
    });
  },
};

export default ProductAPI;