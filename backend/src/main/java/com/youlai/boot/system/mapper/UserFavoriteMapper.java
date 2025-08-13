package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.UserFavorite;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.UserFavoriteQuery;
import com.youlai.boot.system.model.vo.UserFavoriteVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-13 20:03
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 获取收藏分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<UserFavoriteVO>} 收藏分页列表
     */
    Page<UserFavoriteVO> getUserFavoritePage(Page<UserFavoriteVO> page, UserFavoriteQuery queryParams);

}
