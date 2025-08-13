package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.UserFavorite;
import com.youlai.boot.system.model.form.UserFavoriteForm;
import com.youlai.boot.system.model.query.UserFavoriteQuery;
import com.youlai.boot.system.model.vo.UserFavoriteVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 收藏服务类
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
public interface UserFavoriteService extends IService<UserFavorite> {

    /**
     *收藏分页列表
     *
     * @return {@link IPage<UserFavoriteVO>} 收藏分页列表
     */
    IPage<UserFavoriteVO> getUserFavoritePage(UserFavoriteQuery queryParams);

    /**
     * 获取收藏表单数据
     *
     * @param id 收藏ID
     * @return 收藏表单数据
     */
     UserFavoriteForm getUserFavoriteFormData(Long id);

    /**
     * 新增收藏
     *
     * @param formData 收藏表单对象
     * @return 是否新增成功
     */
    boolean saveUserFavorite(UserFavoriteForm formData);

    /**
     * 修改收藏
     *
     * @param id   收藏ID
     * @param formData 收藏表单对象
     * @return 是否修改成功
     */
    boolean updateUserFavorite(Long id, UserFavoriteForm formData);

    /**
     * 删除收藏
     *
     * @param ids 收藏ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deleteUserFavorites(String ids);

}
