package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.system.model.form.ProductTagsForm;
import com.youlai.boot.system.model.query.ProductTagsQuery;
import com.youlai.boot.system.model.vo.ProductTagsVO;
import com.youlai.boot.system.service.ProductTagsService;
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
 * ProductTagsController 单元测试
 *
 * @author liuzijun
 * @since 2025-01-27
 */
@ExtendWith(MockitoExtension.class)
class ProductTagsControllerTest {

    @Mock
    private ProductTagsService productTagsService;

    @InjectMocks
    private ProductTagsController productTagsController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productTagsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetProductTagsPage() throws Exception {
        // 准备测试数据
        ProductTagsQuery queryParams = new ProductTagsQuery();
        queryParams.setPageNum(1);
        queryParams.setPageSize(10);
        
        ProductTagsVO tagVO = new ProductTagsVO();
        tagVO.setId(1);
        tagVO.setName("测试标签");
        tagVO.setParentId(0);
        
        IPage<ProductTagsVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(tagVO));
        page.setTotal(1);
        
        when(productTagsService.getProductTagsPage(any(ProductTagsQuery.class))).thenReturn(page);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productTags/page")
                .param("pageNum", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].name").value("测试标签"));
    }

    @Test
    void testSaveProductTags() throws Exception {
        // 准备测试数据
        ProductTagsForm formData = new ProductTagsForm();
        formData.setId(1); // 新增时也需要设置id，因为有@NotNull验证
        formData.setName("新标签");
        formData.setParentId(0);
        
        when(productTagsService.saveProductTags(any(ProductTagsForm.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(post("/api/v1/productTags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetProductTagsForm() throws Exception {
        // 准备测试数据
        Long tagId = 1L;
        ProductTagsForm formData = new ProductTagsForm();
        formData.setId(1);
        formData.setName("测试标签");
        formData.setParentId(0);
        
        when(productTagsService.getProductTagsFormData(tagId)).thenReturn(formData);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productTags/{id}/form", tagId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试标签"));
    }

    @Test
    void testUpdateProductTags() throws Exception {
        // 准备测试数据
        Long tagId = 1L;
        ProductTagsForm formData = new ProductTagsForm();
        formData.setId(1); // 更新时也需要设置id
        formData.setName("更新标签");
        formData.setParentId(0);
        
        when(productTagsService.updateProductTags(eq(tagId), any(ProductTagsForm.class))).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(put("/api/v1/productTags/{id}", tagId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeleteProductTags() throws Exception {
        // 准备测试数据
        String ids = "1,2,3";
        
        when(productTagsService.deleteProductTagss(ids)).thenReturn(true);
        
        // 执行测试
        mockMvc.perform(delete("/api/v1/productTags/{ids}", ids)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetTagTree() throws Exception {
        // 准备测试数据
        ProductTagsVO parentTag = new ProductTagsVO();
        parentTag.setId(1);
        parentTag.setName("父级标签");
        parentTag.setParentId(0);
        
        ProductTagsVO childTag = new ProductTagsVO();
        childTag.setId(2);
        childTag.setName("子级标签");
        childTag.setParentId(1);
        
        List<ProductTagsVO> tagTree = Arrays.asList(parentTag, childTag);
        
        when(productTagsService.getTagTree()).thenReturn(tagTree);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productTags/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0].name").value("父级标签"))
                .andExpect(jsonPath("$.data[1].name").value("子级标签"));
    }

    @Test
    void testGetTagsByParentId() throws Exception {
        // 准备测试数据
        Integer parentId = 1;
        ProductTagsVO childTag = new ProductTagsVO();
        childTag.setId(2);
        childTag.setName("子级标签");
        childTag.setParentId(parentId);
        
        List<ProductTagsVO> childTags = Arrays.asList(childTag);
        
        when(productTagsService.getTagsByParentId(parentId)).thenReturn(childTags);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productTags/children/{parentId}", parentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0].name").value("子级标签"))
                .andExpect(jsonPath("$.data[0].parentId").value(parentId));
    }

    @Test
    void testGetAllTags() throws Exception {
        // 准备测试数据
        ProductTagsVO tag1 = new ProductTagsVO();
        tag1.setId(1);
        tag1.setName("标签1");
        tag1.setParentId(0);
        
        ProductTagsVO tag2 = new ProductTagsVO();
        tag2.setId(2);
        tag2.setName("标签2");
        tag2.setParentId(0);
        
        List<ProductTagsVO> allTags = Arrays.asList(tag1, tag2);
        
        when(productTagsService.getAllTags()).thenReturn(allTags);
        
        // 执行测试
        mockMvc.perform(get("/api/v1/productTags/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"))
                .andExpect(jsonPath("$.data[0].name").value("标签1"))
                .andExpect(jsonPath("$.data[1].name").value("标签2"));
    }

    @Test
    void testSaveProductTagsWithInvalidData() throws Exception {
        // 测试无效数据的情况
        ProductTagsForm formData = new ProductTagsForm();
        // 不设置必填字段，测试验证失败的情况
        
        mockMvc.perform(post("/api/v1/productTags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateProductTagsWithInvalidId() throws Exception {
        // 测试无效ID的情况
        Integer invalidId = 999;
        ProductTagsForm formData = new ProductTagsForm();
        formData.setId(invalidId);
        formData.setName("更新标签");
        formData.setParentId(0);
        
        when(productTagsService.updateProductTags(eq(invalidId.longValue()), any(ProductTagsForm.class))).thenReturn(false);
        
        mockMvc.perform(put("/api/v1/productTags/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(formData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("B0001")); // 根据实际返回的错误码
    }
}