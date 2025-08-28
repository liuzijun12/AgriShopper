package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.WxUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信用户持久层接口
 */
@Mapper
public interface WxUserMapper extends BaseMapper<WxUser> {
} 