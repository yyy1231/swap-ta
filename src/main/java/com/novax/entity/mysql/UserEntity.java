package com.novax.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private Long id;

    private Long tenantId;

    private String areaCode;

    private String phone;

    private String email;

    private String password;

    private String salt;

    private String nickname;

    private String headUrl;

    private String trueName;

    private String nationality;

    private Integer idCardType;

    private String idCard;

    private Integer authLevel;

    private Integer isAuth;

    private String assistantCurrency;

    private String language;

    private Integer feeFlag;

    private String inviteCode;

    private Long inviterId;

    private Integer status;

    private Integer level;

    private Date levelUpdateTime;

    private Integer spotDiscount;

    private Integer swapDiscount;

    private Integer swapCurrencyDiscount;

    private String googleKey;

    private Date lastModified;

    private Integer isMerchant;

    private Integer isSwapOpened;

    private Integer isAnswerPassed;

    private Integer swapStatus;

    private Integer isLimitSpot;

    private Integer isLimitWithdrawal;

    private String limitMarket;

    private Integer isDeleted;

    private Integer isStatistics;

    private Integer isWhitelist;

    private Integer type;

    private Integer registerClient;

    private String registerIp;

    private String region;

    private Date createTime;

    private Date updateTime;

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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Integer getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(Integer idCardType) {
        this.idCardType = idCardType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getAuthLevel() {
        return authLevel;
    }

    public void setAuthLevel(Integer authLevel) {
        this.authLevel = authLevel;
    }

    public Integer getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(Integer isAuth) {
        this.isAuth = isAuth;
    }

    public String getAssistantCurrency() {
        return assistantCurrency;
    }

    public void setAssistantCurrency(String assistantCurrency) {
        this.assistantCurrency = assistantCurrency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getFeeFlag() {
        return feeFlag;
    }

    public void setFeeFlag(Integer feeFlag) {
        this.feeFlag = feeFlag;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getLevelUpdateTime() {
        return levelUpdateTime;
    }

    public void setLevelUpdateTime(Date levelUpdateTime) {
        this.levelUpdateTime = levelUpdateTime;
    }

    public Integer getSpotDiscount() {
        return spotDiscount;
    }

    public void setSpotDiscount(Integer spotDiscount) {
        this.spotDiscount = spotDiscount;
    }

    public Integer getSwapDiscount() {
        return swapDiscount;
    }

    public void setSwapDiscount(Integer swapDiscount) {
        this.swapDiscount = swapDiscount;
    }

    public Integer getSwapCurrencyDiscount() {
        return swapCurrencyDiscount;
    }

    public void setSwapCurrencyDiscount(Integer swapCurrencyDiscount) {
        this.swapCurrencyDiscount = swapCurrencyDiscount;
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getIsMerchant() {
        return isMerchant;
    }

    public void setIsMerchant(Integer isMerchant) {
        this.isMerchant = isMerchant;
    }

    public Integer getIsSwapOpened() {
        return isSwapOpened;
    }

    public void setIsSwapOpened(Integer isSwapOpened) {
        this.isSwapOpened = isSwapOpened;
    }

    public Integer getIsAnswerPassed() {
        return isAnswerPassed;
    }

    public void setIsAnswerPassed(Integer isAnswerPassed) {
        this.isAnswerPassed = isAnswerPassed;
    }

    public Integer getSwapStatus() {
        return swapStatus;
    }

    public void setSwapStatus(Integer swapStatus) {
        this.swapStatus = swapStatus;
    }

    public Integer getIsLimitSpot() {
        return isLimitSpot;
    }

    public void setIsLimitSpot(Integer isLimitSpot) {
        this.isLimitSpot = isLimitSpot;
    }

    public Integer getIsLimitWithdrawal() {
        return isLimitWithdrawal;
    }

    public void setIsLimitWithdrawal(Integer isLimitWithdrawal) {
        this.isLimitWithdrawal = isLimitWithdrawal;
    }

    public String getLimitMarket() {
        return limitMarket;
    }

    public void setLimitMarket(String limitMarket) {
        this.limitMarket = limitMarket;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getIsStatistics() {
        return isStatistics;
    }

    public void setIsStatistics(Integer isStatistics) {
        this.isStatistics = isStatistics;
    }

    public Integer getIsWhitelist() {
        return isWhitelist;
    }

    public void setIsWhitelist(Integer isWhitelist) {
        this.isWhitelist = isWhitelist;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRegisterClient() {
        return registerClient;
    }

    public void setRegisterClient(Integer registerClient) {
        this.registerClient = registerClient;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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