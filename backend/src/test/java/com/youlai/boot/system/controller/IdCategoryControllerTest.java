package com.youlai.boot.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.youlai.boot.system.model.form.IdCategoryForm;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
import com.youlai.boot.system.service.IdCategoryService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * IdCategoryController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-14
 */
@ExtendWith(MockitoExtension.class)
class IdCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IdCategoryService idCategoryService;

    @InjectMocks
    private IdCategoryController idCategoryController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(idCategoryController).build();
    }

    @Test
    void testGetIdCategoryPage() throws Exception {
        // 准备测试数据
        IdCategoryVO vo = new IdCategoryVO();
        vo.setId(1);
        vo.setProductId(1001);
        vo.setCategoryId(3001);

        IPage<IdCategoryVO> page = new Page<>();
        page.setRecords(Arrays.asList(vo));
        page.setTotal(1);
        page.setCurrent(1);
        page.setSize(10);

        when(idCategoryService.getIdCategoryPage(any(IdCategoryQuery.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-category/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].productId").value(1001))
                .andExpect(jsonPath("$.data.list[0].categoryId").value(3001));
    }

    @Test
    void testSaveIdCategory() throws Exception {
        // 准备测试数据
        IdCategoryForm form = new IdCategoryForm();
        form.setId(1);
        form.setProductId(1001);
        form.setCategoryId(3001);

        when(idCategoryService.saveIdCategory(any(IdCategoryForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/api/v1/id-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testSaveIdCategoryWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        IdCategoryForm form = new IdCategoryForm();
        // 不设置id字段，触发验证错误

        // 执行测试
        mockMvc.perform(post("/api/v1/id-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetIdCategoryForm() throws Exception{
        // 准备测试数据
        IdCategoryForm form = new IdCategoryForm();
        form.setId(1);
        form.setProductId(1001);
        form.setCategoryId(3001);

        when(idCategoryService.getIdCategoryFormData(1L)).thenReturn(form);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-category/1/form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.productId").value(1001))
                .andExpect(jsonPath("$.data.categoryId").value(3001));
    }

    @Test
    void testUpdateIdCategory() throws Exception {
        // 准备测试数据
        IdCategoryForm form = new IdCategoryForm();
        form.setId(1);
        form.setProductId(1001);
        form.setCategoryId(3002);

        when(idCategoryService.updateIdCategory(eq(1L), any(IdCategoryForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/api/v1/id-category/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteIdCategorys() throws Exception {
        when(idCategoryService.deleteIdCategorys("1,2,3")).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/id-category/1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetIdCategoryFormNotFound() throws Exception {
        when(idCategoryService.getIdCategoryFormData(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-category/999/form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateIdCategoryFailure() throws Exception {
        // 准备测试数据
        IdCategoryForm form = new IdCategoryForm();
        form.setId(1);
        form.setProductId(1001);
        form.setCategoryId(3001);

        when(idCategoryService.updateIdCategory(eq(999L), any(IdCategoryForm.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(put("/api/v1/id-category/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001"));
    }

    @Test
    void testDeleteIdCategorysFailure() throws Exception {
        when(idCategoryService.deleteIdCategorys("999")).thenReturn(false);

        // 执行测试
        mockMvc.perform(delete("/api/v1/id-category/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001"));
    }

    @Test
    void testSaveIdCategoryFailure() throws Exception {
        // 准备测试数据
        IdCategoryForm form = new IdCategoryForm();
        form.setId(1);
        form.setProductId(1001);
        form.setCategoryId(3001);

        when(idCategoryService.saveIdCategory(any(IdCategoryForm.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(post("/api/v1/id-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001"));
    }
}