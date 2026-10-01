package com.novax.entity.mysql;

import java.math.BigDecimal;
import java.util.Date;

public class SwapStopProfitLossEntity {
    private Long id;

    private Long userId;

    private Long orderId;

    private Integer tradeType;

    private BigDecimal triggerPriceProfit;

    private BigDecimal orderPriceProfit;

    private BigDecimal triggerPriceLoss;

    private BigDecimal orderPriceLoss;

    private Integer state;

    private Date createTime;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getTriggerPriceProfit() {
        return triggerPriceProfit;
    }

    public void setTriggerPriceProfit(BigDecimal triggerPriceProfit) {
        this.triggerPriceProfit = triggerPriceProfit;
    }

    public BigDecimal getOrderPriceProfit() {
        return orderPriceProfit;
    }

    public void setOrderPriceProfit(BigDecimal orderPriceProfit) {
        this.orderPriceProfit = orderPriceProfit;
    }

    public BigDecimal getTriggerPriceLoss() {
        return triggerPriceLoss;
    }

    public void setTriggerPriceLoss(BigDecimal triggerPriceLoss) {
        this.triggerPriceLoss = triggerPriceLoss;
    }

    public BigDecimal getOrderPriceLoss() {
        return orderPriceLoss;
    }

    public void setOrderPriceLoss(BigDecimal orderPriceLoss) {
        this.orderPriceLoss = orderPriceLoss;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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