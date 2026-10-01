package com.novax.entity.mysql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwapFrontOrderEntity {
    private Long id;

    private Long tenantId;

    private Long userId;

    private Integer orderType;

    private String symbol;

    private BigDecimal price;

    private Long volume;

    private Integer tradeType;

    private Integer mode;

    private Long tradeVolume;

    private BigDecimal tradeNumber;

    private BigDecimal tradeTotal;

    private BigDecimal profit;

    private BigDecimal fee;

    private BigDecimal deductionFee;

    private BigDecimal frozen;

    private Integer leverage;

    private Integer orderPriceType;

    private Integer isStopOrder;

    private Byte source;

    private Long sourceId;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}