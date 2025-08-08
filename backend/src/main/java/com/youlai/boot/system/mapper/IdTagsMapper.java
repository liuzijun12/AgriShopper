package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.IdTags;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.query.IdTagsQuery;
import com.youlai.boot.system.model.vo.IdTagsVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签关联表Mapper接口
 *
 * @author liuzijun
 * @since 2025-08-08 12:20
 */
@Mapper
public interface IdTagsMapper extends BaseMapper<IdTags> {

    /**
     * 获取标签关联表分页数据
     *
     * @param page 分页对象
     * @param queryParams 查询参数
     * @return {@link Page<IdTagsVO>} 标签关联表分页列表
     */
    Page<IdTagsVO> getIdTagsPage(Page<IdTagsVO> page, IdTagsQuery queryParams);

}
