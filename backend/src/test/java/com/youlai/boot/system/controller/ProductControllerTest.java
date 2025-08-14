package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.system.model.form.ProductForm;
import com.youlai.boot.system.model.query.ProductQuery;
import com.youlai.boot.system.model.vo.ProductVO;
import com.youlai.boot.system.model.vo.TagVO;
import com.youlai.boot.system.model.vo.ProductCategoryVO;
import com.youlai.boot.system.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-14
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetProductPage() throws Exception {
        // 准备测试数据
        ProductQuery queryParams = new ProductQuery();
        queryParams.setPageNum(1);
        queryParams.setPageSize(10);
        
        ProductVO productVO = new ProductVO();
        productVO.setId(1L);
        productVO.setName("测试商品");
        productVO.setImages("[\"image1.jpg\", \"image2.jpg\"]");
        productVO.setDescription("测试商品描述");
        productVO.setPrice(new BigDecimal("99.99"));
        productVO.setHasDiscount(1);
        productVO.setDiscountPrice(new BigDecimal("79.99"));
        productVO.setSales(100);
        productVO.setOrigin("测试产地");
        productVO.setIsHot(1);
        productVO.setVirtualSales(50);
        
        IPage<ProductVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(productVO));
        page.setTotal(1);
        
        when(productService.getProductPage(any(ProductQuery.class))).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/page")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("测试商品"));
    }

    @Test
    void testGetProductById() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        ProductVO productVO = new ProductVO();
        productVO.setId(productId);
        productVO.setName("测试商品");
        productVO.setImages("[\"image1.jpg\"]");
        productVO.setDescription("测试商品描述");
        productVO.setPrice(new BigDecimal("99.99"));
        
        when(productService.getProductById(productId)).thenReturn(productVO);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试商品"));
    }

    @Test
    void testAddProduct() throws Exception {
        // 准备测试数据
        ProductForm formData = new ProductForm();
        formData.setName("新商品");
        formData.setImages("[\"new-image.jpg\"]");
        formData.setDescription("新商品描述");
        formData.setPrice(new BigDecimal("199.99"));
        formData.setHasDiscount(0);
        formData.setSales(0);
        formData.setOrigin("新产地");
        formData.setIsHot(0);
        formData.setVirtualSales(0);
        formData.setStock(100L);
        formData.setIsOnline(1);
        formData.setType("规格A");
        formData.setCategoryIds(Arrays.asList(1L, 2L));
        formData.setTagIds(Arrays.asList(1L, 2L));
        
        when(productService.saveProduct(any(ProductForm.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetProductFormData() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        ProductForm formData = new ProductForm();
        formData.setId(productId);
        formData.setName("测试商品");
        formData.setImages("[\"image1.jpg\"]");
        formData.setDescription("测试商品描述");
        formData.setPrice(new BigDecimal("99.99"));
        formData.setHasDiscount(1);
        formData.setDiscountPrice(new BigDecimal("79.99"));
        formData.setSales(100);
        formData.setOrigin("测试产地");
        formData.setIsHot(1);
        formData.setVirtualSales(50);
        formData.setStock(200L);
        formData.setIsOnline(1);
        formData.setType("规格B");
        formData.setCategoryIds(Arrays.asList(1L));
        formData.setTagIds(Arrays.asList(1L));
        
        when(productService.getProductFormData(productId)).thenReturn(formData);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/{id}/form", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试商品"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        ProductForm formData = new ProductForm();
        formData.setId(productId);
        formData.setName("更新商品");
        formData.setImages("[\"updated-image.jpg\"]");
        formData.setDescription("更新商品描述");
        formData.setPrice(new BigDecimal("299.99"));
        formData.setHasDiscount(1);
        formData.setDiscountPrice(new BigDecimal("249.99"));
        formData.setSales(150);
        formData.setOrigin("更新产地");
        formData.setIsHot(1);
        formData.setVirtualSales(75);
        formData.setStock(150L);
        formData.setIsOnline(1);
        formData.setType("规格C");
        formData.setCategoryIds(Arrays.asList(2L, 3L));
        formData.setTagIds(Arrays.asList(2L, 3L));
        
        when(productService.updateProduct(eq(productId), any(ProductForm.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(put("/api/v1/product/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // 准备测试数据
        String ids = "1,2,3";
        
        when(productService.deleteProducts(ids)).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(delete("/api/v1/product/{ids}", ids)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetProductWithTags() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        ProductVO productVO = new ProductVO();
        productVO.setId(productId);
        productVO.setName("带标签商品");
        productVO.setTagNames("标签1,标签2");
        
        when(productService.getProductWithTags(productId)).thenReturn(productVO);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/{id}/with-tags", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.tagNames").value("标签1,标签2"));
    }

    @Test
    void testGetProductTags() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        TagVO tag1 = new TagVO();
        tag1.setId(1L);
        tag1.setName("标签1");
        
        TagVO tag2 = new TagVO();
        tag2.setId(2L);
        tag2.setName("标签2");
        
        List<TagVO> tags = Arrays.asList(tag1, tag2);
        
        when(productService.getTagsByProductId(productId)).thenReturn(tags);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/{id}/tags", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("标签1"));
    }

    @Test
    void testGetProductWithCategories() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        ProductVO productVO = new ProductVO();
        productVO.setId(productId);
        productVO.setName("带分类商品");
        productVO.setCategoryNames("分类1,分类2");
        
        when(productService.getProductWithCategories(productId)).thenReturn(productVO);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/{id}/with-categories", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.categoryNames").value("分类1,分类2"));
    }

    @Test
    void testGetProductCategories() throws Exception {
        // 准备测试数据
        Long productId = 1L;
        ProductCategoryVO category1 = new ProductCategoryVO();
        category1.setId(1L);
        category1.setName("分类1");
        
        ProductCategoryVO category2 = new ProductCategoryVO();
        category2.setId(2L);
        category2.setName("分类2");
        
        List<ProductCategoryVO> categories = Arrays.asList(category1, category2);
        
        when(productService.getCategoriesByProductId(productId)).thenReturn(categories);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/product/{id}/categories", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("分类1"));
    }

    @Test
    void testAddProductWithInvalidData() throws Exception {
        // 测试无效数据的情况
        ProductForm formData = new ProductForm();
        // 不设置必填字段，测试验证失败的情况
        
        mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProductWithInvalidId() throws Exception {
        // 测试无效ID的情况
        Long invalidId = 999L;
        ProductForm formData = new ProductForm();
        formData.setId(invalidId);
        formData.setName("更新商品");
        formData.setImages("[\"image.jpg\"]");
        formData.setDescription("描述");
        formData.setPrice(new BigDecimal("99.99"));
        
        when(productService.updateProduct(eq(invalidId), any(ProductForm.class))).thenReturn(false);
        
        mockMvc.perform(put("/api/v1/product/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001")); // 根据实际返回的错误码
    }
}