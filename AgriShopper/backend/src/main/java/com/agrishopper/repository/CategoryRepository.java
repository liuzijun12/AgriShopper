package com.agrishopper.repository;

import com.agrishopper.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根据启用状态查询分类列表
     * @param isEnabled 是否启用
     * @return 分类列表
     */
    List<Category> findByIsEnabledOrderBySortOrderAsc(Boolean isEnabled);
    
    /**
     * 查询所有启用的分类，按排序权重升序排列
     * @return 分类列表
     */
    @Query("SELECT c FROM Category c WHERE c.isEnabled = true ORDER BY c.sortOrder ASC")
    List<Category> findAllEnabledCategories();
    
    /**
     * 根据分类名称查询分类
     * @param categoryName 分类名称
     * @return 分类
     */
    Category findByCategoryName(String categoryName);
    
    /**
     * 检查分类名称是否存在
     * @param categoryName 分类名称
     * @return 是否存在
     */
    boolean existsByCategoryName(String categoryName);
} 