package com.novax.entity.mysql;

import java.util.Date;

public class CurrencyEntity {
    private Long id;

    private String currency;

    private String chineseName;

    private String englishName;

    private String imgs;

    private Integer propertyDecimal;

    private Integer sort;

    private Integer inflowType;

    private Integer outflowType;

    private Integer module;

    private Integer status;

    private Integer isPlatform;

    private Integer isFinance;

    private Integer isHidden;

    private Integer isRank;

    private String referrer;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public Integer getPropertyDecimal() {
        return propertyDecimal;
    }

    public void setPropertyDecimal(Integer propertyDecimal) {
        this.propertyDecimal = propertyDecimal;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getInflowType() {
        return inflowType;
    }

    public void setInflowType(Integer inflowType) {
        this.inflowType = inflowType;
    }

    public Integer getOutflowType() {
        return outflowType;
    }

    public void setOutflowType(Integer outflowType) {
        this.outflowType = outflowType;
    }

    public Integer getModule() {
        return module;
    }

    public void setModule(Integer module) {
        this.module = module;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsPlatform() {
        return isPlatform;
    }

    public void setIsPlatform(Integer isPlatform) {
        this.isPlatform = isPlatform;
    }

    public Integer getIsFinance() {
        return isFinance;
    }

    public void setIsFinance(Integer isFinance) {
        this.isFinance = isFinance;
    }

    public Integer getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Integer isHidden) {
        this.isHidden = isHidden;
    }

    public Integer getIsRank() {
        return isRank;
    }

    public void setIsRank(Integer isRank) {
        this.isRank = isRank;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
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