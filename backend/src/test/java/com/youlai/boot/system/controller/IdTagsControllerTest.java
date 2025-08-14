package com.youlai.boot.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.youlai.boot.system.model.form.IdTagsForm;
import com.youlai.boot.system.model.query.IdTagsQuery;
import com.youlai.boot.system.model.vo.IdTagsVO;
import com.youlai.boot.system.service.IdTagsService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * IdTagsController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-14
 */
@ExtendWith(MockitoExtension.class)
class IdTagsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IdTagsService idTagsService;

    @InjectMocks
    private IdTagsController idTagsController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(idTagsController).build();
    }

    @Test
    void testGetIdTagsPage() throws Exception {
        // 准备测试数据
        IdTagsVO vo = new IdTagsVO();
        vo.setId(1);
        vo.setProductId(1001);
        vo.setTagsId(2001);

        IPage<IdTagsVO> page = new Page<>();
        page.setRecords(Arrays.asList(vo));
        page.setTotal(1);
        page.setCurrent(1);
        page.setSize(10);

        when(idTagsService.getIdTagsPage(any(IdTagsQuery.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-tags/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].productId").value(1001))
                .andExpect(jsonPath("$.data.list[0].tagsId").value(2001));
    }

    @Test
    void testSaveIdTags() throws Exception {
        // 准备测试数据
        IdTagsForm form = new IdTagsForm();
        form.setId(1);
        form.setProductId(1001);
        form.setTagsId(2001);

        when(idTagsService.saveIdTags(any(IdTagsForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/api/v1/id-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testSaveIdTagsWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        IdTagsForm form = new IdTagsForm();
        // 不设置id字段，触发验证错误

        // 执行测试
        mockMvc.perform(post("/api/v1/id-tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetIdTagsForm() throws Exception {
        // 准备测试数据
        IdTagsForm form = new IdTagsForm();
        form.setId(1);
        form.setProductId(1001);
        form.setTagsId(2001);

        when(idTagsService.getIdTagsFormData(1L)).thenReturn(form);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-tags/1/form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.productId").value(1001))
                .andExpect(jsonPath("$.data.tagsId").value(2001));
    }

    @Test
    void testUpdateIdTags() throws Exception {
        // 准备测试数据
        IdTagsForm form = new IdTagsForm();
        form.setId(1);
        form.setProductId(1001);
        form.setTagsId(2002);

        when(idTagsService.updateIdTags(eq(1L), any(IdTagsForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/api/v1/id-tags/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteIdTags() throws Exception {
        when(idTagsService.deleteIdTagss("1,2,3")).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/id-tags/1,2,3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteByProductId() throws Exception {
        when(idTagsService.deleteByProductId(1001)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/id-tags/product/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteByTagId() throws Exception {
        when(idTagsService.deleteByTagId(2001)).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/id-tags/tag/2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetTagIdsByProductId() throws Exception {
        // 准备测试数据
        List<Integer> tagIds = Arrays.asList(2001, 2002, 2003);
        when(idTagsService.getTagIdsByProductId(1001)).thenReturn(tagIds);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-tags/product/1001/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0]").value(2001))
                .andExpect(jsonPath("$.data[1]").value(2002))
                .andExpect(jsonPath("$.data[2]").value(2003));
    }

    @Test
    void testGetProductIdsByTagId() throws Exception{
        // 准备测试数据
        List<Integer> productIds = Arrays.asList(1001, 1002, 1003);
        when(idTagsService.getProductIdsByTagId(2001)).thenReturn(productIds);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-tags/tag/2001/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0]").value(1001))
                .andExpect(jsonPath("$.data[1]").value(1002))
                .andExpect(jsonPath("$.data[2]").value(1003));
    }

    @Test
    void testGetIdTagsFormNotFound() throws Exception {
        when(idTagsService.getIdTagsFormData(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/v1/id-tags/999/form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateIdTagsFailure() throws Exception {
        // 准备测试数据
        IdTagsForm form = new IdTagsForm();
        form.setId(1);
        form.setProductId(1001);
        form.setTagsId(2001);

        when(idTagsService.updateIdTags(eq(999L), any(IdTagsForm.class))).thenReturn(false);

        // 执行测试
        mockMvc.perform(put("/api/v1/id-tags/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001"));
    }
}