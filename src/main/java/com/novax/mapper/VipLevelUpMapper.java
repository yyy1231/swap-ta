package com.novax.mapper;

import com.novax.entity.mysql.VipLevelUp;

import java.util.List;

public interface VipLevelUpMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VipLevelUp record);

    VipLevelUp selectByPrimaryKey(Long id);

    List<VipLevelUp> selectAll();

    int updateByPrimaryKey(VipLevelUp record);

    VipLevelUp selectByUserId(Long userId);
}