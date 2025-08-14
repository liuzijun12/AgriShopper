package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.youlai.boot.system.model.form.wxUserForm;
import com.youlai.boot.system.model.query.wxUserQuery;
import com.youlai.boot.system.model.vo.wxUserVO;
import com.youlai.boot.system.service.wxUserService;
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
 * wxUserController 单元测试
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@ExtendWith(MockitoExtension.class)
class wxUserControllerTest {

    @Mock
    private wxUserService wxUserService;

    @InjectMocks
    private wxUserController wxUserController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(wxUserController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetwxUserPage() throws Exception {
        // 准备测试数据
        wxUserVO wxUserVO = new wxUserVO();
        wxUserVO.setId(1);
        wxUserVO.setOpenid("test_openid");
        wxUserVO.setNickname("测试用户");
        wxUserVO.setAvatar("http://example.com/avatar.jpg");
        wxUserVO.setRealName("张三");
        wxUserVO.setPhone("13800138000");
        wxUserVO.setGender(1);
        wxUserVO.setProvince("广东省");
        wxUserVO.setCity("深圳市");
        wxUserVO.setDistrict("南山区");
        wxUserVO.setIsManager(0);
        wxUserVO.setIsSupermanager(0);
        wxUserVO.setBalance(new BigDecimal("100.00"));
        wxUserVO.setIsDeleted(0);
        wxUserVO.setCreateTime(LocalDateTime.now());
        wxUserVO.setUpdateTime(LocalDateTime.now());

        Page<wxUserVO> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(wxUserVO));
        page.setTotal(1);

        when(wxUserService.getwxUserPage(any(wxUserQuery.class))).thenReturn(page);

        // 执行测试
        mockMvc.perform(get("/api/v1/wxuser/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].id").value(1))
                .andExpect(jsonPath("$.data.list[0].nickname").value("测试用户"));
    }

    @Test
    void testSavewxUser() throws Exception {
        // 准备测试数据
        wxUserForm wxUserForm = new wxUserForm();
        wxUserForm.setId(1);
        wxUserForm.setOpenid("test_openid");
        wxUserForm.setNickname("测试用户");
        wxUserForm.setAvatar("http://example.com/avatar.jpg");
        wxUserForm.setRealName("张三");
        wxUserForm.setPhone("13800138000");
        wxUserForm.setGender(1);
        wxUserForm.setProvince("广东省");
        wxUserForm.setCity("深圳市");
        wxUserForm.setDistrict("南山区");
        wxUserForm.setIsManager(0);
        wxUserForm.setIsSupermanager(0);
        wxUserForm.setBalance(new BigDecimal("100.00"));
        wxUserForm.setIsDeleted(0);
        wxUserForm.setCreateTime(LocalDateTime.now());
        wxUserForm.setUpdateTime(LocalDateTime.now());

        when(wxUserService.savewxUser(any(wxUserForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/api/v1/wxuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wxUserForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testGetwxUserForm() throws Exception {
        // 准备测试数据
        wxUserForm wxUserForm = new wxUserForm();
        wxUserForm.setId(1);
        wxUserForm.setOpenid("test_openid");
        wxUserForm.setNickname("测试用户");
        wxUserForm.setAvatar("http://example.com/avatar.jpg");
        wxUserForm.setRealName("张三");
        wxUserForm.setPhone("13800138000");
        wxUserForm.setGender(1);
        wxUserForm.setProvince("广东省");
        wxUserForm.setCity("深圳市");
        wxUserForm.setDistrict("南山区");
        wxUserForm.setIsManager(0);
        wxUserForm.setIsSupermanager(0);
        wxUserForm.setBalance(new BigDecimal("100.00"));
        wxUserForm.setIsDeleted(0);
        wxUserForm.setCreateTime(LocalDateTime.now());
        wxUserForm.setUpdateTime(LocalDateTime.now());

        when(wxUserService.getwxUserFormData(1L)).thenReturn(wxUserForm);

        // 执行测试
        mockMvc.perform(get("/api/v1/wxuser/1/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }

    @Test
    void testUpdatewxUser() throws Exception {
        // 准备测试数据
        wxUserForm wxUserForm = new wxUserForm();
        wxUserForm.setId(1);
        wxUserForm.setOpenid("test_openid");
        wxUserForm.setNickname("更新用户");
        wxUserForm.setAvatar("http://example.com/avatar.jpg");
        wxUserForm.setRealName("李四");
        wxUserForm.setPhone("13800138001");
        wxUserForm.setGender(2);
        wxUserForm.setProvince("北京市");
        wxUserForm.setCity("北京市");
        wxUserForm.setDistrict("朝阳区");
        wxUserForm.setIsManager(0);
        wxUserForm.setIsSupermanager(0);
        wxUserForm.setBalance(new BigDecimal("200.00"));
        wxUserForm.setIsDeleted(0);
        wxUserForm.setCreateTime(LocalDateTime.now());
        wxUserForm.setUpdateTime(LocalDateTime.now());

        when(wxUserService.updatewxUser(eq(1L), any(wxUserForm.class))).thenReturn(true);

        // 执行测试
        mockMvc.perform(put("/api/v1/wxuser/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wxUserForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testDeletewxUsers() throws Exception {
        when(wxUserService.deletewxUsers("1,2,3")).thenReturn(true);

        // 执行测试
        mockMvc.perform(delete("/api/v1/wxuser/1,2,3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00000"));
    }

    @Test
    void testSavewxUserWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        wxUserForm wxUserForm = new wxUserForm();
        // 不设置必填字段，测试验证

        // 执行测试
        mockMvc.perform(post("/api/v1/wxuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wxUserForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatewxUserWithInvalidData() throws Exception {
        // 准备无效测试数据（缺少必填字段）
        wxUserForm wxUserForm = new wxUserForm();
        // 不设置必填字段，测试验证

        // 执行测试
        mockMvc.perform(put("/api/v1/wxuser/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wxUserForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetwxUserFormNotFound() throws Exception {
        when(wxUserService.getwxUserFormData(999L)).thenReturn(null);

        // 执行测试
        mockMvc.perform(get("/api/v1/wxuser/999/form")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}