import request, { publicRequest } from '@/utils/request'

// API基础路径
const CATEGORY_BASE_URL = '/api/v1/productCategory'

// 分类接口类型定义
export interface ProductCategoryVO {
  id: number
  name: string
  icon: string
  description: string
  sort: number
  isEnabled: number
  createTime: string
  updateTime: string
}

export interface CategoryQuery {
  pageNum?: number
  pageSize?: number
  name?: string
  isEnabled?: number
}

export interface PageResult<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

// 分类API接口
const CategoryAPI = {
  /**
   * 获取分类分页列表
   * @param query 查询参数
   * @returns 分类分页列表
   */
  getCategoryPage(query: CategoryQuery): Promise<PageResult<ProductCategoryVO>> {
    return publicRequest<PageResult<ProductCategoryVO>>({
      url: `${CATEGORY_BASE_URL}/page`,
      method: "GET",
      data: query,
    })
  },

  /**
   * 获取所有启用的分类列表
   * @returns 分类列表
   */
  getEnabledCategories(): Promise<ProductCategoryVO[]> {
    console.log('🔥 调试 - 调用 getEnabledCategories')
    console.log('🔥 调试 - 请求URL:', `${CATEGORY_BASE_URL}/public/list`)
    
    return publicRequest<ProductCategoryVO[]>({
      url: `${CATEGORY_BASE_URL}/public/list`,
      method: "GET"
    }).then(result => {
      console.log('🔥 调试 - getEnabledCategories 成功返回:', result)
      return result
    }).catch(error => {
      console.error('🔥 调试 - getEnabledCategories 失败:', error)
      throw error
    })
  },

  /**
   * 根据ID获取分类详情
   * @param id 分类ID
   * @returns 分类详情
   */
  getCategoryById(id: number): Promise<ProductCategoryVO> {
    return publicRequest<ProductCategoryVO>({
      url: `${CATEGORY_BASE_URL}/public/${id}`,
      method: "GET",
    })
  }
}

export default CategoryAPI 