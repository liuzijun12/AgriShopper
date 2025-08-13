package com.youlai.boot.system.service;

import com.youlai.boot.system.model.entity.wxUser;
import com.youlai.boot.system.model.form.wxUserForm;
import com.youlai.boot.system.model.query.wxUserQuery;
import com.youlai.boot.system.model.vo.wxUserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务类
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
public interface wxUserService extends IService<wxUser> {

    /**
     *用户分页列表
     *
     * @return {@link IPage<wxUserVO>} 用户分页列表
     */
    IPage<wxUserVO> getwxUserPage(wxUserQuery queryParams);

    /**
     * 获取用户表单数据
     *
     * @param id 用户ID
     * @return 用户表单数据
     */
     wxUserForm getwxUserFormData(Long id);

    /**
     * 新增用户
     *
     * @param formData 用户表单对象
     * @return 是否新增成功
     */
    boolean savewxUser(wxUserForm formData);

    /**
     * 修改用户
     *
     * @param id   用户ID
     * @param formData 用户表单对象
     * @return 是否修改成功
     */
    boolean updatewxUser(Long id, wxUserForm formData);

    /**
     * 删除用户
     *
     * @param ids 用户ID，多个以英文逗号(,)分割
     * @return 是否删除成功
     */
    boolean deletewxUsers(String ids);

}
