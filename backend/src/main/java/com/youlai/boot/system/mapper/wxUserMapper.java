package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.wxUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.wxUserQuery;
import com.youlai.boot.system.model.vo.wxUserVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-13 15:03
 */
@Mapper
public interface wxUserMapper extends BaseMapper<wxUser> {

    /**
     * 获取用户分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<wxUserVO>} 用户分页列表
     */
    Page<wxUserVO> getwxUserPage(Page<wxUserVO> page, wxUserQuery queryParams);

}
