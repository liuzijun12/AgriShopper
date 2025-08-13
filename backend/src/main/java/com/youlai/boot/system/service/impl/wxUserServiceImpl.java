package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.wxUserMapper;
import com.youlai.boot.system.service.wxUserService;
import com.youlai.boot.system.model.entity.wxUser;
import com.youlai.boot.system.model.form.wxUserForm;
import com.youlai.boot.system.model.query.wxUserQuery;
import com.youlai.boot.system.model.vo.wxUserVO;
import com.youlai.boot.system.converter.wxUserConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 用户服务实现类
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Service
@RequiredArgsConstructor
public class wxUserServiceImpl extends ServiceImpl<wxUserMapper, wxUser> implements wxUserService {

    private final wxUserConverter wxUserConverter;

    /**
    * 获取用户分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<wxUserVO>} 用户分页列表
    */
    @Override
    public IPage<wxUserVO> getwxUserPage(wxUserQuery queryParams) {
        Page<wxUserVO> pageVO = this.baseMapper.getwxUserPage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取用户表单数据
     *
     * @param id 用户ID
     * @return 用户表单数据
     */
    @Override
    public wxUserForm getwxUserFormData(Long id) {
        LambdaQueryWrapper<wxUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(wxUser::getId, id)
                   .eq(wxUser::getIsDeleted, 0);
        wxUser entity = this.getOne(queryWrapper);
        return wxUserConverter.toForm(entity);
    }
    
    /**
     * 新增用户
     *
     * @param formData 用户表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean savewxUser(wxUserForm formData) {
        // 数据校验
        Assert.isTrue(StrUtil.isNotBlank(formData.getOpenid()), "openid不能为空");
        Assert.isTrue(StrUtil.isNotBlank(formData.getNickname()), "昵称不能为空");
        Assert.notNull(formData.getCreateTime(), "创建时间不能为空");

        // 转换为实体对象
        wxUser entity = wxUserConverter.toEntity(formData);

        // 设置默认值
        entity.setIsManager(0);
        entity.setIsSupermanager(0);
        entity.setBalance(new BigDecimal("0"));
        entity.setIsDeleted(0);
        entity.setUpdateTime(LocalDateTime.now());

        // 保存用户
        return this.save(entity);
    }
    
    /**
     * 更新用户
     *
     * @param id   用户ID
     * @param formData 用户表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updatewxUser(Long id,wxUserForm formData) {
        wxUser entity = wxUserConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除用户
     *
     * @param ids 用户ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deletewxUsers(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的用户数据为空");
        
        // 解析用户ID列表
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        
        // 软删除：批量更新isDeleted字段为1，设置删除时间
        List<wxUser> usersToDelete = idList.stream()
                .map(id -> {
                    wxUser user = new wxUser();
                    user.setId(id);
                    user.setIsDeleted(1);
                    user.setDeleteTime(LocalDateTime.now());
                    return user;
                })
                .collect(Collectors.toList());
        
        return this.updateBatchById(usersToDelete);
    }

}
