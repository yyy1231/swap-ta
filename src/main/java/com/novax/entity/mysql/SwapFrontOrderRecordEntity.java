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
public class SwapFrontOrderRecordEntity {
    private Long id;

    private Long tenantId;

    private Long userId;

    private Long orderId;

    private Long orderOppositeId;

    private String symbol;

    private Integer tradeType;

    private Integer mode;

    private Integer leverage;

    private BigDecimal tradePrice;

    private Long tradeVolume;

    private BigDecimal profit;

    private BigDecimal fee;

    private Integer role;

    private String feeCurrency;

    private Byte source;

    private Long sourceId;

    private Integer status;

    private Date createTime;
}