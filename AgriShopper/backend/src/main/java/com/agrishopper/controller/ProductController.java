package com.agrishopper.controller;

import com.agrishopper.model.Product;
import com.agrishopper.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Tag(name = "商品管理", description = "商品管理相关接口，包括商品的增删改查等操作")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "获取所有商品", description = "获取系统中的所有商品信息")
    @ApiResponse(responseCode = "200", description = "成功获取商品列表")
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(createSuccessResponse(products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取商品信息"),
        @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(createSuccessResponse(product)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("商品不存在")));
    }

    @Operation(summary = "创建商品", description = "创建新的商品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "商品创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(savedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "更新商品", description = "更新指定商品的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "商品更新成功"),
        @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @RequestBody Product product) {
        try {
            product.setId(id);
            Product updatedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(createSuccessResponse(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "删除商品", description = "删除指定的商品")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "商品删除成功"),
        @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
}
