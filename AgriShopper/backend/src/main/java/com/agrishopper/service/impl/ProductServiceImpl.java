package com.agrishopper.service.impl;

import com.agrishopper.model.Product;
import com.agrishopper.repository.ProductRepository;
import com.agrishopper.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        if (product.getId() != null) {
            // 如果是更新操作，先检查商品是否存在
            productRepository.findById(product.getId())
                .orElseThrow(() -> new EntityNotFoundException("商品不存在"));
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("商品不存在");
        }
        productRepository.deleteById(id);
    }
} 