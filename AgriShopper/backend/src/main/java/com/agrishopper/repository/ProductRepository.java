package com.agrishopper.repository;

import com.agrishopper.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 可以自定义查询方法，例如通过商品名称查询
    Optional<Product> findByProductName(String productName);

    // 可以自定义查询方法，例如通过商品类别ID查询
    List<Product> findByCategoryId(Long categoryId);
}
