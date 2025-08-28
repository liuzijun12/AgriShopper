import request, { publicRequest } from '@/utils/request'

// API基础路径
const PRODUCT_BASE_URL = '/api/v1/product'

// 商品接口类型定义
export interface ProductVO {
  id: number
  name: string
  images: string
  description: string
  price: number
  hasDiscount: number
  discountPrice: number
  sales: number
  tagNames: string
  categoryNames: string
  origin: string
  isHot: number
  virtualSales: number
  stock: number
  isOnline: number
  createTime: string
  updateTime: string
}

export interface ProductQuery {
  pageNum?: number
  pageSize?: number
  name?: string
  categoryId?: number
  isHot?: number
  isOnline?: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

// 商品API接口
const ProductAPI = {
  /**
   * 获取商品分页列表
   * @param query 查询参数
   * @returns 商品分页列表
   */
  getProductPage(query: ProductQuery): Promise<PageResult<ProductVO>> {
    return publicRequest<PageResult<ProductVO>>({
      url: `${PRODUCT_BASE_URL}/public/page`,
      method: "GET",
      data: query,
    })
  },

  /**
   * 根据ID获取商品详情
   * @param id 商品ID
   * @returns 商品详情
   */
  getProductById(id: number): Promise<ProductVO> {
    return publicRequest<ProductVO>({
      url: `${PRODUCT_BASE_URL}/public/${id}`,
      method: "GET",
    })
  },

  /**
   * 获取商品详情（包含标签）
   * @param id 商品ID
   * @returns 商品详情
   */
  getProductWithTags(id: number): Promise<ProductVO> {
    return publicRequest<ProductVO>({
      url: `${PRODUCT_BASE_URL}/${id}/with-tags`,
      method: "GET",
    })
  },

  /**
   * 获取商品详情（包含分类）
   * @param id 商品ID
   * @returns 商品详情
   */
  getProductWithCategories(id: number): Promise<ProductVO> {
    return publicRequest<ProductVO>({
      url: `${PRODUCT_BASE_URL}/${id}/with-categories`,
      method: "GET",
    })
  },

  /**
   * 获取热门商品列表
   * @param pageSize 每页数量，默认10
   * @returns 热门商品列表
   */
  getHotProducts(pageSize: number = 10): Promise<ProductVO[]> {
    console.log('🔥 调试 - 调用 getHotProducts，参数:', pageSize)
    console.log('🔥 调试 - 请求URL:', `${PRODUCT_BASE_URL}/public/hot`)
    
    return publicRequest<ProductVO[]>({
      url: `${PRODUCT_BASE_URL}/public/hot`,
      method: "GET",
      data: { limit: pageSize }
    }).then(result => {
      console.log('🔥 调试 - getHotProducts 成功返回:', result)
      return result
    }).catch(error => {
      console.error('🔥 调试 - getHotProducts 失败:', error)
      throw error
    })
  },

  /**
   * 获取上架商品列表
   * @param query 查询参数
   * @returns 上架商品列表
   */
  getOnlineProducts(query: ProductQuery): Promise<PageResult<ProductVO>> {
    return this.getProductPage({
      ...query,
      isOnline: 1
    })
  },

  /**
   * 根据分类获取商品列表
   * @param categoryId 分类ID
   * @param query 其他查询参数
   * @returns 商品列表
   */
  getProductsByCategory(categoryId: number, query: ProductQuery = {}): Promise<PageResult<ProductVO>> {
    return this.getProductPage({
      ...query,
      categoryId
    })
  },

  /**
   * 搜索商品
   * @param keyword 搜索关键词
   * @param query 其他查询参数
   * @returns 搜索结果
   */
  searchProducts(keyword: string, query: ProductQuery = {}): Promise<PageResult<ProductVO>> {
    return this.getProductPage({
      ...query,
      name: keyword
    })
  }
}

export default ProductAPI 