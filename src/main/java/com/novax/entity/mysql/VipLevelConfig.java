package com.novax.entity.mysql;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class VipLevelConfig {
    private Long id;

    private Integer level;

    private BigDecimal spotBtc;

    private BigDecimal swapBtc;

    private BigDecimal platform;

    private BigDecimal spotMaker;

    private BigDecimal spotTaker;

    private BigDecimal spotMakerDiscount;

    private BigDecimal spotTakerDiscount;

    private BigDecimal swapFrontMaker;

    private BigDecimal swapFrontTaker;

    private BigDecimal swapFrontMakerDiscount;

    private BigDecimal swapFrontTakerDiscount;

    private BigDecimal swapReverseMaker;

    private BigDecimal swapReverseTaker;

    private BigDecimal swapReverseMakerDiscount;

    private BigDecimal swapReverseTakerDiscount;

    private BigDecimal withdrawalMax;

    private BigDecimal withdrawalSingleLimit;

    private String withdrawalCurrency;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public BigDecimal getSpotBtc() {
        return spotBtc;
    }

    public void setSpotBtc(BigDecimal spotBtc) {
        this.spotBtc = spotBtc;
    }

    public BigDecimal getSwapBtc() {
        return swapBtc;
    }

    public void setSwapBtc(BigDecimal swapBtc) {
        this.swapBtc = swapBtc;
    }

    public BigDecimal getPlatform() {
        return platform;
    }

    public void setPlatform(BigDecimal platform) {
        this.platform = platform;
    }

    public BigDecimal getSpotMaker() {
        return spotMaker;
    }

    public void setSpotMaker(BigDecimal spotMaker) {
        this.spotMaker = spotMaker;
    }

    public BigDecimal getSpotTaker() {
        return spotTaker;
    }

    public void setSpotTaker(BigDecimal spotTaker) {
        this.spotTaker = spotTaker;
    }

    public BigDecimal getSpotMakerDiscount() {
        return spotMakerDiscount;
    }

    public void setSpotMakerDiscount(BigDecimal spotMakerDiscount) {
        this.spotMakerDiscount = spotMakerDiscount;
    }

    public BigDecimal getSpotTakerDiscount() {
        return spotTakerDiscount;
    }

    public void setSpotTakerDiscount(BigDecimal spotTakerDiscount) {
        this.spotTakerDiscount = spotTakerDiscount;
    }

    public BigDecimal getSwapFrontMaker() {
        return swapFrontMaker;
    }

    public void setSwapFrontMaker(BigDecimal swapFrontMaker) {
        this.swapFrontMaker = swapFrontMaker;
    }

    public BigDecimal getSwapFrontTaker() {
        return swapFrontTaker;
    }

    public void setSwapFrontTaker(BigDecimal swapFrontTaker) {
        this.swapFrontTaker = swapFrontTaker;
    }

    public BigDecimal getSwapFrontMakerDiscount() {
        return swapFrontMakerDiscount;
    }

    public void setSwapFrontMakerDiscount(BigDecimal swapFrontMakerDiscount) {
        this.swapFrontMakerDiscount = swapFrontMakerDiscount;
    }

    public BigDecimal getSwapFrontTakerDiscount() {
        return swapFrontTakerDiscount;
    }

    public void setSwapFrontTakerDiscount(BigDecimal swapFrontTakerDiscount) {
        this.swapFrontTakerDiscount = swapFrontTakerDiscount;
    }

    public BigDecimal getSwapReverseMaker() {
        return swapReverseMaker;
    }

    public void setSwapReverseMaker(BigDecimal swapReverseMaker) {
        this.swapReverseMaker = swapReverseMaker;
    }

    public BigDecimal getSwapReverseTaker() {
        return swapReverseTaker;
    }

    public void setSwapReverseTaker(BigDecimal swapReverseTaker) {
        this.swapReverseTaker = swapReverseTaker;
    }

    public BigDecimal getSwapReverseMakerDiscount() {
        return swapReverseMakerDiscount;
    }

    public void setSwapReverseMakerDiscount(BigDecimal swapReverseMakerDiscount) {
        this.swapReverseMakerDiscount = swapReverseMakerDiscount;
    }

    public BigDecimal getSwapReverseTakerDiscount() {
        return swapReverseTakerDiscount;
    }

    public void setSwapReverseTakerDiscount(BigDecimal swapReverseTakerDiscount) {
        this.swapReverseTakerDiscount = swapReverseTakerDiscount;
    }

    public BigDecimal getWithdrawalMax() {
        return withdrawalMax;
    }

    public void setWithdrawalMax(BigDecimal withdrawalMax) {
        this.withdrawalMax = withdrawalMax;
    }

    public BigDecimal getWithdrawalSingleLimit() {
        return withdrawalSingleLimit;
    }

    public void setWithdrawalSingleLimit(BigDecimal withdrawalSingleLimit) {
        this.withdrawalSingleLimit = withdrawalSingleLimit;
    }

    public String getWithdrawalCurrency() {
        return withdrawalCurrency;
    }

    public void setWithdrawalCurrency(String withdrawalCurrency) {
        this.withdrawalCurrency = withdrawalCurrency;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}