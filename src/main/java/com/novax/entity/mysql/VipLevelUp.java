package com.novax.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VipLevelUp {
    private Long id;

    private Long userId;

    private BigDecimal spotVolume;

    private BigDecimal swapVolume;

    private BigDecimal platformCurrency;

    private Integer isUpdated;

    private Integer level;

    private Date expirationTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getSpotVolume() {
        return spotVolume;
    }

    public void setSpotVolume(BigDecimal spotVolume) {
        this.spotVolume = spotVolume;
    }

    public BigDecimal getSwapVolume() {
        return swapVolume;
    }

    public void setSwapVolume(BigDecimal swapVolume) {
        this.swapVolume = swapVolume;
    }

    public BigDecimal getPlatformCurrency() {
        return platformCurrency;
    }

    public void setPlatformCurrency(BigDecimal platformCurrency) {
        this.platformCurrency = platformCurrency;
    }

    public Integer getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(Integer isUpdated) {
        this.isUpdated = isUpdated;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}