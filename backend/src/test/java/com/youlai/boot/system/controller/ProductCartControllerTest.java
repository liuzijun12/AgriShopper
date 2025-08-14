package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.youlai.boot.system.model.form.ProductCartForm;
import com.youlai.boot.system.model.query.ProductCartQuery;
import com.youlai.boot.system.model.vo.ProductCartVO;
import com.youlai.boot.system.service.ProductCartService;
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
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ProductCartController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-13 21:13
 */
@ExtendWith(MockitoExtension.class)
class ProductCartControllerTest {

    @Mock
    private ProductCartService productCartService;

    @InjectMocks
    private ProductCartController productCartController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productCartController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetProductCartPage() throws Exception {
        // 准备测试数据
        ProductCartVO productCartVO = new ProductCartVO();
        productCartVO.setId(1);
        productCartVO.setUserId(1);
        productCartVO.setProductId(1);
        productCartVO.setProductType("大号");
        productCartVO.setProductCount(2);
        productCartVO.setIsDeleted(0);
        productCartVO.setProductPrice(new BigDecimal("99.99"));
        productCartVO.setCreateTime(LocalDateTime.now());
        productCartVO.setUpdateTime(LocalDateTime.now());
        productCartVO.setUserName("测试用户");
        productCartVO.setProductName("测试商品");
        productCartVO.setProductImages("http://example.com/product.jpg");

        Page<ProductCartVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(productCartVO));
        page.setTotal(1);

        when(productCartService.getProductCartPage(any(ProductCartQuery.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/v1/productCart/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].userId").value(1))
                .andExpect(jsonPath("$.data.list[0].productId").value(1))
                .andExpect(jsonPath("$.data.list[0].productType").value("大号"))
                .andExpect(jsonPath("$.data.list[0].productCount").value(2))
                .andExpect(jsonPath("$.data.list[0].userName").value("测试用户"))
                .andExpect(jsonPath("$.data.list[0].productName").value("测试商品"));
    }

    @Test
    void testSaveProductCart() throws Exception {
        // 准备测试数据
        ProductCartForm productCartForm = new ProductCartForm();
        productCartForm.setUserId(1);
        productCartForm.setProductId(1);
        productCartForm.setProductType("大号");
        productCartForm.setProductCount(2);
        productCartForm.setIsDeleted(0);
        productCartForm.setProductPrice(new BigDecimal("99.99"));
        productCartForm.setCreateTime(LocalDateTime.now());
        productCartForm.setUpdateTime(LocalDateTime.now());

        when(productCartService.saveProductCart(any(ProductCartForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/api/v1/productCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCartForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetProductCartForm() throws Exception {
        // 准备测试数据
        ProductCartForm productCartForm = new ProductCartForm();
        productCartForm.setId(1);
        productCartForm.setUserId(1);
        productCartForm.setProductId(1);
        productCartForm.setProductType("大号");
        productCartForm.setProductCount(2);
        productCartForm.setIsDeleted(0);
        productCartForm.setProductPrice(new BigDecimal("99.99"));
        productCartForm.setCreateTime(LocalDateTime.now());
        productCartForm.setUpdateTime(LocalDateTime.now());

        when(productCartService.getProductCartFormData(1L)).thenReturn(productCartForm);

        // 执行测试
        mockMvc.perform(get("/api/v1/productCart/1/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.productType").value("大号"))
                .andExpect(jsonPath("$.data.productCount").value(2));
    }

    @Test
    void testUpdateProductCart() throws Exception {
        // 准备测试数据
        ProductCartForm productCartForm = new ProductCartForm();
        productCartForm.setId(1);
        productCartForm.setUserId(1);
        productCartForm.setProductId(1);
        productCartForm.setProductType("中号");
        productCartForm.setProductCount(3);
        productCartForm.setIsDeleted(0);
        productCartForm.setProductPrice(new BigDecimal("89.99"));
        productCartForm.setCreateTime(LocalDateTime.now());
        productCartForm.setUpdateTime(LocalDateTime.now());

        when(productCartService.updateProductCart(eq(1L), any(ProductCartForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/api/v1/productCart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCartForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteProductCarts() throws Exception {
        when(productCartService.deleteProductCarts("1,2,3")).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/productCart/1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testSaveProductCartWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        ProductCartForm productCartForm = new ProductCartForm();
        // 不设置userId、productId、productCount和productPrice，测试验证

        // 执行测试
        mockMvc.perform(post("/api/v1/productCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCartForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProductCartWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        ProductCartForm productCartForm = new ProductCartForm();
        // 不设置userId、productId、productCount和productPrice，测试验证

        // 执行测试
        mockMvc.perform(put("/api/v1/productCart/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCartForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProductCartFormNotFound() throws Exception {
        when(productCartService.getProductCartFormData(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/v1/productCart/999/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetProductCartPageWithFilters() throws Exception {
        // 准备测试数据
        ProductCartVO productCartVO = new ProductCartVO();
        productCartVO.setId(1);
        productCartVO.setUserId(1);
        productCartVO.setProductId(1);
        productCartVO.setProductType("小号");
        productCartVO.setProductCount(1);
        productCartVO.setProductPrice(new BigDecimal("59.99"));
        productCartVO.setUserName("张三");
        productCartVO.setProductName("苹果");

        Page<ProductCartVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(productCartVO));
        page.setTotal(1);

        when(productCartService.getProductCartPage(any(ProductCartQuery.class))).thenReturn(page);

        // 执行测试 - 带过滤条件
        mockMvc.perform(get("/api/v1/productCart/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("userId", "1")
                        .param("productId", "1")
                        .param("userName", "张三")
                        .param("productName", "苹果")
                        .param("productType", "小号")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].userName").value("张三"))
                .andExpect(jsonPath("$.data.list[0].productName").value("苹果"))
                .andExpect(jsonPath("$.data.list[0].productType").value("小号"));
    }

    @Test
    void testSaveProductCartWithValidData() throws Exception {
        // 准备完整有效的测试数据
        ProductCartForm productCartForm = new ProductCartForm();
        productCartForm.setUserId(1);
        productCartForm.setProductId(1);
        productCartForm.setProductType("特大号");
        productCartForm.setProductCount(5);
        productCartForm.setIsDeleted(0);
        productCartForm.setProductPrice(new BigDecimal("199.99"));
        productCartForm.setCreateTime(LocalDateTime.now());
        productCartForm.setUpdateTime(LocalDateTime.now());

        when(productCartService.saveProductCart(any(ProductCartForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/api/v1/productCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productCartForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.msg").value("一切ok"));
    }
}