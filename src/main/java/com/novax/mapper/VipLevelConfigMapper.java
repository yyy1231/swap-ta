package com.novax.mapper;

import com.novax.entity.mysql.VipLevelConfig;

import java.util.List;

public interface VipLevelConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VipLevelConfig record);

    VipLevelConfig selectByPrimaryKey(Long id);

    List<VipLevelConfig> selectAll();

    int updateByPrimaryKey(VipLevelConfig record);

    VipLevelConfig selectByLevel(int level);
}