package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.model.MoocCinemaT;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 影院信息表 Mapper 接口
 * </p>
 *
 * @author Roger
 * @since 2019-07-11
 */
public interface MoocCinemaTMapper extends BaseMapper<MoocCinemaT> {

    void selectPage(Page<MoocCinemaT> page);
}
