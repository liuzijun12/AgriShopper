package com.agrishopper.controller;

import com.agrishopper.model.Category;
import com.agrishopper.service.CategoryService;
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

@Tag(name = "分类管理", description = "商品分类相关接口，包括分类的增删改查等操作")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "获取所有分类", description = "获取系统中的所有商品分类")
    @ApiResponse(responseCode = "200", description = "成功获取分类列表")
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.getEnabledCategories();
            return ResponseEntity.ok(createSuccessResponse(categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "获取分类详情", description = "根据分类ID获取分类详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取分类信息"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(createSuccessResponse(category)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("分类不存在")));
    }

    @Operation(summary = "创建分类", description = "创建新的商品分类")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "分类创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数有误")
    })
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        try {
            // 检查分类名称是否已存在
            if (categoryService.existsByCategoryName(category.getCategoryName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("分类名称已存在"));
            }
            
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createSuccessResponse(savedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "更新分类", description = "更新指定分类的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "分类更新成功"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @RequestBody Category category) {
        try {
            category.setId(id);
            Category updatedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(createSuccessResponse(updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "删除分类", description = "删除指定的分类")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "分类删除成功"),
        @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
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