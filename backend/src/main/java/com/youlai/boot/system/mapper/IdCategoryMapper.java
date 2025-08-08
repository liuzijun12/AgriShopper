package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.IdCategory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.IdCategoryQuery;
import com.youlai.boot.system.model.vo.IdCategoryVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类关联Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-08 12:16
 */
@Mapper
public interface IdCategoryMapper extends BaseMapper<IdCategory> {

    /**
     * 获取分类关联分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<IdCategoryVO>} 分类关联分页列表
     */
    Page<IdCategoryVO> getIdCategoryPage(Page<IdCategoryVO> page, IdCategoryQuery queryParams);

}
