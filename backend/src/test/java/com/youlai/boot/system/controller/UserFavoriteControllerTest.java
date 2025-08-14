package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.youlai.boot.system.model.form.UserFavoriteForm;
import com.youlai.boot.system.model.query.UserFavoriteQuery;
import com.youlai.boot.system.model.vo.UserFavoriteVO;
import com.youlai.boot.system.service.UserFavoriteService;
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
 * UserFavoriteController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@ExtendWith(MockitoExtension.class)
class UserFavoriteControllerTest {

    @Mock
    private UserFavoriteService userFavoriteService;

    @InjectMocks
    private UserFavoriteController userFavoriteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userFavoriteController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetUserFavoritePage() throws Exception {
        // 准备测试数据
        UserFavoriteVO userFavoriteVO = new UserFavoriteVO();
        userFavoriteVO.setId(1);
        userFavoriteVO.setUserId(1);
        userFavoriteVO.setProductId(1);
        userFavoriteVO.setIsDeleted(0);
        userFavoriteVO.setCreateTime(LocalDateTime.now());
        userFavoriteVO.setUpdateTime(LocalDateTime.now());
        userFavoriteVO.setUserName("测试用户");
        userFavoriteVO.setProductName("测试商品");
        userFavoriteVO.setProductImages("http://example.com/product.jpg");
        userFavoriteVO.setProductPrice(new BigDecimal("99.99"));

        Page<UserFavoriteVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(userFavoriteVO));
        page.setTotal(1);

        when(userFavoriteService.getUserFavoritePage(any(UserFavoriteQuery.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/v1/userFavorite/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].userId").value(1))
                .andExpect(jsonPath("$.data.list[0].productId").value(1))
                .andExpect(jsonPath("$.data.list[0].userName").value("测试用户"))
                .andExpect(jsonPath("$.data.list[0].productName").value("测试商品"));
    }

    @Test
    void testSaveUserFavorite() throws Exception {
        // 准备测试数据
        UserFavoriteForm userFavoriteForm = new UserFavoriteForm();
        userFavoriteForm.setUserId(1);
        userFavoriteForm.setProductId(1);
        userFavoriteForm.setIsDeleted(0);
        userFavoriteForm.setCreateTime(LocalDateTime.now());
        userFavoriteForm.setUpdateTime(LocalDateTime.now());

        when(userFavoriteService.saveUserFavorite(any(UserFavoriteForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/api/v1/userFavorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFavoriteForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetUserFavoriteForm() throws Exception {
        // 准备测试数据
        UserFavoriteForm userFavoriteForm = new UserFavoriteForm();
        userFavoriteForm.setId(1);
        userFavoriteForm.setUserId(1);
        userFavoriteForm.setProductId(1);
        userFavoriteForm.setIsDeleted(0);
        userFavoriteForm.setCreateTime(LocalDateTime.now());
        userFavoriteForm.setUpdateTime(LocalDateTime.now());

        when(userFavoriteService.getUserFavoriteFormData(1L)).thenReturn(userFavoriteForm);

        // 执行测试
        mockMvc.perform(get("/api/v1/userFavorite/1/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.productId").value(1));
    }

    @Test
    void testUpdateUserFavorite() throws Exception {
        // 准备测试数据
        UserFavoriteForm userFavoriteForm = new UserFavoriteForm();
        userFavoriteForm.setId(1);
        userFavoriteForm.setUserId(1);
        userFavoriteForm.setProductId(2);
        userFavoriteForm.setIsDeleted(0);
        userFavoriteForm.setCreateTime(LocalDateTime.now());
        userFavoriteForm.setUpdateTime(LocalDateTime.now());

        when(userFavoriteService.updateUserFavorite(eq(1L), any(UserFavoriteForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/api/v1/userFavorite/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFavoriteForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteUserFavorites() throws Exception {
        when(userFavoriteService.deleteUserFavorites("1,2,3")).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/userFavorite/1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testSaveUserFavoriteWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        UserFavoriteForm userFavoriteForm = new UserFavoriteForm();
        // 不设置userId和productId，测试验证

        // 执行测试
        mockMvc.perform(post("/api/v1/userFavorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFavoriteForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserFavoriteWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        UserFavoriteForm userFavoriteForm = new UserFavoriteForm();
        // 不设置userId和productId，测试验证

        // 执行测试
        mockMvc.perform(put("/api/v1/userFavorite/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFavoriteForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUserFavoriteFormNotFound() throws Exception {
        when(userFavoriteService.getUserFavoriteFormData(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/v1/userFavorite/999/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetUserFavoritePageWithFilters() throws Exception {
        // 准备测试数据
        UserFavoriteVO userFavoriteVO = new UserFavoriteVO();
        userFavoriteVO.setId(1);
        userFavoriteVO.setUserId(1);
        userFavoriteVO.setProductId(1);
        userFavoriteVO.setUserName("张三");
        userFavoriteVO.setProductName("苹果");
        userFavoriteVO.setProductPrice(new BigDecimal("5.99"));

        Page<UserFavoriteVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(userFavoriteVO));
        page.setTotal(1);

        when(userFavoriteService.getUserFavoritePage(any(UserFavoriteQuery.class))).thenReturn(page);

        // 执行测试 - 带过滤条件
        mockMvc.perform(get("/api/v1/userFavorite/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("userId", "1")
                        .param("productId", "1")
                        .param("userName", "张三")
                        .param("productName", "苹果")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].userName").value("张三"))
                .andExpect(jsonPath("$.data.list[0].productName").value("苹果"));
    }
}