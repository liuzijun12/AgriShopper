package com.agrishopper.service;

import com.agrishopper.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    List<Category> getAllCategories();
    
    /**
     * 获取所有启用的分类
     * @return 启用的分类列表
     */
    List<Category> getEnabledCategories();
    
    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类
     */
    Optional<Category> getCategoryById(Long id);
    
    /**
     * 根据名称获取分类
     * @param categoryName 分类名称
     * @return 分类
     */
    Optional<Category> getCategoryByName(String categoryName);
    
    /**
     * 保存分类
     * @param category 分类
     * @return 保存后的分类
     */
    Category saveCategory(Category category);
    
    /**
     * 删除分类
     * @param id 分类ID
     */
    void deleteCategory(Long id);
    
    /**
     * 检查分类名称是否存在
     * @param categoryName 分类名称
     * @return 是否存在
     */
    boolean existsByCategoryName(String categoryName);
} 