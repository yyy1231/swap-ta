package com.novax.entity.mysql;

import java.math.BigDecimal;
import java.util.Date;

public class SwapPositionFactorEntity {
    private Long id;

    private String symbol;

    private Long minVolume;

    private Long maxVolume;

    private BigDecimal coefficient;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getMinVolume() {
        return minVolume;
    }

    public void setMinVolume(Long minVolume) {
        this.minVolume = minVolume;
    }

    public Long getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(Long maxVolume) {
        this.maxVolume = maxVolume;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}