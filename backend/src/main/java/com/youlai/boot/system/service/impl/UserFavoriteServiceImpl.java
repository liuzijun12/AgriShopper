package com.youlai.boot.system.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.mapper.UserFavoriteMapper;
import com.youlai.boot.system.service.UserFavoriteService;
import com.youlai.boot.system.model.entity.UserFavorite;
import com.youlai.boot.system.model.form.UserFavoriteForm;
import com.youlai.boot.system.model.query.UserFavoriteQuery;
import com.youlai.boot.system.model.vo.UserFavoriteVO;
import com.youlai.boot.system.converter.UserFavoriteConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 收藏服务实现类
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    private final UserFavoriteConverter userFavoriteConverter;

    /**
    * 获取收藏分页列表
    *
    * @param queryParams 查询参数
    * @return {@link IPage<UserFavoriteVO>} 收藏分页列表
    */
    @Override
    public IPage<UserFavoriteVO> getUserFavoritePage(UserFavoriteQuery queryParams) {
        Page<UserFavoriteVO> pageVO = this.baseMapper.getUserFavoritePage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return pageVO;
    }
    
    /**
     * 获取收藏表单数据
     *
     * @param id 收藏ID
     * @return 收藏表单数据
     */
    @Override
    public UserFavoriteForm getUserFavoriteFormData(Long id) {
        UserFavorite entity = this.getById(id);
        return userFavoriteConverter.toForm(entity);
    }
    
    /**
     * 新增收藏
     *
     * @param formData 收藏表单对象
     * @return 是否新增成功
     */
    @Override
    public boolean saveUserFavorite(UserFavoriteForm formData) {
        UserFavorite entity = userFavoriteConverter.toEntity(formData);
        return this.save(entity);
    }
    
    /**
     * 更新收藏
     *
     * @param id   收藏ID
     * @param formData 收藏表单对象
     * @return 是否修改成功
     */
    @Override
    public boolean updateUserFavorite(Long id,UserFavoriteForm formData) {
        UserFavorite entity = userFavoriteConverter.toEntity(formData);
        return this.updateById(entity);
    }
    
    /**
     * 删除收藏
     *
     * @param ids 收藏ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUserFavorites(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "删除的收藏数据为空");
        // 逻辑删除
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        return this.removeByIds(idList);
    }

}
