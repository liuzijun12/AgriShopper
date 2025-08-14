package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.system.model.form.ProductCategoryForm;
import com.youlai.boot.system.model.query.ProductCategoryQuery;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.youlai.boot.system.service.ProductCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductCategoryController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-14
 */
@ExtendWith(MockitoExtension.class)
class ProductCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductCategoryService productCategoryService;

    @InjectMocks
    private ProductCategoryController productCategoryController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productCategoryController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetProductCategoryPage() throws Exception {
        // 准备测试数据
        ProductCategoryQuery queryParams = new ProductCategoryQuery();
        queryParams.setPageNum(1);
        queryParams.setPageSize(10);
        
        ProductCategoryVO categoryVO = new ProductCategoryVO();
        categoryVO.setId(1L);
        categoryVO.setName("测试分类");
        categoryVO.setIcon("test-icon");
        categoryVO.setParentId(0L);
        
        IPage<ProductCategoryVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(categoryVO));
        page.setTotal(1);
        
        when(productCategoryService.getProductCategoryPage(any(ProductCategoryQuery.class))).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productCategory/page")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("测试分类"));
    }

    @Test
    void testSaveProductCategory() throws Exception {
        // 准备测试数据
        ProductCategoryForm formData = new ProductCategoryForm();
        formData.setId(1);
        formData.setName("新分类");
        formData.setIcon("new-icon");
        formData.setParentId(0);
        
        when(productCategoryService.saveProductCategory(any(ProductCategoryForm.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(post("/api/v1/productCategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetProductCategoryForm() throws Exception {
        // 准备测试数据
        Long categoryId = 1L;
        ProductCategoryForm formData = new ProductCategoryForm();
        formData.setId(1);
        formData.setName("测试分类");
        formData.setIcon("test-icon");
        formData.setParentId(0);
        
        when(productCategoryService.getProductCategoryFormData(categoryId)).thenReturn(formData);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productCategory/{id}/form", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试分类"));
    }

    @Test
    void testUpdateProductCategory() throws Exception {
        // 准备测试数据
        Long categoryId = 1L;
        ProductCategoryForm formData = new ProductCategoryForm();
        formData.setId(1);
        formData.setName("更新分类");
        formData.setIcon("updated-icon");
        formData.setParentId(0);
        
        when(productCategoryService.updateProductCategory(eq(categoryId), any(ProductCategoryForm.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(put("/api/v1/productCategory/{id}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteProductCategorys() throws Exception {
        // 准备测试数据
        String ids = "1,2,3";
        
        when(productCategoryService.deleteProductCategorys(ids)).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(delete("/api/v1/productCategory/{ids}", ids)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetCategoryTree() throws Exception {
        // 准备测试数据
        ProductCategoryVO parentCategory = new ProductCategoryVO();
        parentCategory.setId(1L);
        parentCategory.setName("父级分类");
        parentCategory.setIcon("parent-icon");
        parentCategory.setParentId(0L);
        
        ProductCategoryVO childCategory = new ProductCategoryVO();
        childCategory.setId(2L);
        childCategory.setName("子级分类");
        childCategory.setIcon("child-icon");
        childCategory.setParentId(1L);
        
        List<ProductCategoryVO> categoryTree = Arrays.asList(parentCategory, childCategory);
        
        when(productCategoryService.getCategoryTree()).thenReturn(categoryTree);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productCategory/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("父级分类"));
    }

    @Test
    void testSaveProductCategoryWithInvalidData() throws Exception {
        // 测试无效数据的情况
        ProductCategoryForm formData = new ProductCategoryForm();
        // 不设置必填字段，测试验证失败的情况
        
        mockMvc.perform(post("/api/v1/productCategory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProductCategoryWithInvalidId() throws Exception {
        // 测试无效ID的情况
        Long invalidId = 999L;
        ProductCategoryForm formData = new ProductCategoryForm();
        formData.setId(999);
        formData.setName("更新分类");
        formData.setIcon("updated-icon");
        formData.setParentId(0);
        
        when(productCategoryService.updateProductCategory(eq(invalidId), any(ProductCategoryForm.class))).thenReturn(false);
        
        mockMvc.perform(put("/api/v1/productCategory/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001")); // 根据实际返回的错误码
    }
}