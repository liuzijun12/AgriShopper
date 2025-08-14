import request from "@/utils/request";

const CATEGORY_BASE_URL = "/api/v1/product-category";

export interface Category {
  id?: number;
  name: string;
  description?: string;
  parentId?: number;
  level?: number;
  sort?: number;
  icon?: string;
  status?: number;
  createTime?: string;
  updateTime?: string;
  children?: Category[];
}

export interface CategoryTree extends Category {
  children?: CategoryTree[];
}

const CategoryAPI = {
  /**
   * 获取分类树形结构
   */
  getTree(): Promise<CategoryTree[]> {
    return request<CategoryTree[]>({
      url: `${CATEGORY_BASE_URL}/tree`,
      method: "GET",
    });
  },

  /**
   * 获取分类列表
   * @param parentId 父级分类ID，不传则获取所有
   */
  getList(parentId?: number): Promise<Category[]> {
    return request<Category[]>({
      url: `${CATEGORY_BASE_URL}/list`,
      method: "GET",
      data: parentId ? { parentId } : {},
    });
  },

  /**
   * 获取分类详情
   * @param id 分类ID
   */
  getDetail(id: number): Promise<Category> {
    return request<Category>({
      url: `${CATEGORY_BASE_URL}/${id}`,
      method: "GET",
    });
  },

  /**
   * 获取热门分类
   * @param limit 数量限制
   */
  getHotCategories(limit: number = 10): Promise<Category[]> {
    return request<Category[]>({
      url: `${CATEGORY_BASE_URL}/hot`,
      method: "GET",
      data: { limit },
    });
  },
};

export default CategoryAPI;