package com.agrishopper.repository;

import com.agrishopper.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 可以自定义查询方法，例如通过商品名称查询
    Optional<Product> findByProductName(String productName);

    // 可以自定义查询方法，例如通过商品类别ID查询
    List<Product> findByCategoryId(Long categoryId);

    // 根据销量和库存排序获取推荐商品（销量优先，库存次之）
    @Query("SELECT p FROM Product p WHERE p.productStatus = 1 ORDER BY p.salesCount DESC, p.stockQuantity DESC")
    List<Product> findTopProductsBySalesAndStock();

    // 根据销量排序获取热门商品
    @Query("SELECT p FROM Product p WHERE p.productStatus = 1 ORDER BY p.salesCount DESC")
    List<Product> findTopProductsBySales();

    // 根据库存排序获取有库存的商品
    @Query("SELECT p FROM Product p WHERE p.productStatus = 1 AND p.stockQuantity > 0 ORDER BY p.stockQuantity DESC")
    List<Product> findTopProductsByStock();
}
