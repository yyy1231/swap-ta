package com.novax.entity.mysql;

import java.math.BigDecimal;
import java.util.Date;

public class SwapFrontLiquidationEntity {
    private Long id;

    private Long tenantId;

    private Long orderId;

    private Long userId;

    private BigDecimal instrumentPrice;

    private BigDecimal finalInterest;

    private BigDecimal adjustValue;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getInstrumentPrice() {
        return instrumentPrice;
    }

    public void setInstrumentPrice(BigDecimal instrumentPrice) {
        this.instrumentPrice = instrumentPrice;
    }

    public BigDecimal getFinalInterest() {
        return finalInterest;
    }

    public void setFinalInterest(BigDecimal finalInterest) {
        this.finalInterest = finalInterest;
    }

    public BigDecimal getAdjustValue() {
        return adjustValue;
    }

    public void setAdjustValue(BigDecimal adjustValue) {
        this.adjustValue = adjustValue;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}